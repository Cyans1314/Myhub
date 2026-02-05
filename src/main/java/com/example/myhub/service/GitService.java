package com.example.myhub.service;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Git service for repository operations
 * ============================================================================
 */

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;

import com.example.myhub.dto.FileNode;
import com.example.myhub.dto.CommitInfo;
import com.example.myhub.util.JGitUtil;
import com.example.myhub.bean.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GitService {

    // Get repository file list
    public List<FileNode> listFiles(String username, String repoName, String branch, String path) {
        List<FileNode> files = new ArrayList<>();

        try {
            String repoPath = JGitUtil.getRepoPath(username, repoName);
            Git git = JGitUtil.openRepository(repoPath);

            if (git == null) {
                return files;
            }

            Repository repository = git.getRepository();

            // Get latest commit of the branch
            ObjectId branchId = repository.resolve(branch);

            // If specified branch does not exist, try to use default branch
            if (branchId == null) {
                logger.warn("Branch does not exist: {}, trying to use default branch", branch);
                branch = "main";  // Try main branch
                branchId = repository.resolve(branch);

                // If main also does not exist, try master
                if (branchId == null) {
                    branch = "master";
                    branchId = repository.resolve(branch);
                }

                // If still does not exist, return empty list
                if (branchId == null) {
                    logger.error("Repository {} has no available branches", repoName);
                    git.close();
                    return files;
                }
            }

            // Traverse tree
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(branchId);
                RevTree tree = commit.getTree();

                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(false);

                    // If specified path, navigate to that path
                    if (path != null && !path.isEmpty()) {
                        String[] pathParts = path.split("/");
                        for (String part : pathParts) {
                            if (part.isEmpty()) continue;

                            while (treeWalk.next()) {
                                if (treeWalk.getNameString().equals(part)) {
                                    if (treeWalk.isSubtree()) {
                                        treeWalk.enterSubtree();
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    // Traverse files and subdirectories in current directory
                    while (treeWalk.next()) {
                        FileNode node = new FileNode();
                        node.setName(treeWalk.getNameString());
                        node.setPath(path != null ? path + "/" + node.getName() : node.getName());

                        if (treeWalk.isSubtree()) {
                            node.setType("directory");
                        } else {
                            node.setType("file");

                            // Fix: Correctly get file size
                            try {
                                ObjectId objectId = treeWalk.getObjectId(0);
                                ObjectLoader loader = repository.open(objectId);
                                node.setSize(loader.getSize());
                            } catch (IOException e) {
                                node.setSize(0);
                                e.printStackTrace();
                            }
                        }

                        // Get last commit information
                        RevCommit lastCommit = getLastCommitForPath(git, path, node.getName());
                        if (lastCommit != null) {
                            node.setLastCommit(lastCommit.getShortMessage());
                            node.setLastCommitTime(lastCommit.getCommitterIdent().getWhen().toString());
                        }

                        files.add(node);
                    }
                }
            }

            git.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    // Get file content
    public String getFileContent(String username, String repoName, String branch, String filePath) {
        try {
            String repoPath = JGitUtil.getRepoPath(username, repoName);
            Git git = JGitUtil.openRepository(repoPath);

            if (git == null) {
                return null;
            }

            Repository repository = git.getRepository();

            // Get latest commit of the branch
            ObjectId branchId = repository.resolve(branch);
            if (branchId == null) {
                git.close();
                return null;
            }

            // Find file
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(branchId);
                RevTree tree = commit.getTree();

                try (TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, tree)) {
                    if (treeWalk == null) {
                        git.close();
                        return null;
                    }

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    loader.copyTo(output);

                    git.close();

                    // Determine encoding based on file type
                    if (filePath.toLowerCase().endsWith(".txt") ||
                            filePath.toLowerCase().endsWith(".java") ||
                            filePath.toLowerCase().endsWith(".js") ||
                            filePath.toLowerCase().endsWith(".html") ||
                            filePath.toLowerCase().endsWith(".css") ||
                            filePath.toLowerCase().endsWith(".md") ||
                            filePath.toLowerCase().endsWith(".xml") ||
                            filePath.toLowerCase().endsWith(".cpp") ||
                            filePath.toLowerCase().endsWith(".c") ||
                            filePath.toLowerCase().endsWith(".h") ||
                            filePath.toLowerCase().endsWith(".py") ||
                            filePath.toLowerCase().endsWith(".json") ||
                            filePath.toLowerCase().endsWith(".yml") ||
                            filePath.toLowerCase().endsWith(".yaml")) {
                        return output.toString(StandardCharsets.UTF_8.name());
                    } else {
                        // Binary files return empty or base64 encoded
                        return "";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get commit history
    public List<CommitInfo> getCommitHistory(String username, String repoName, String branch, int limit) {
        List<CommitInfo> commits = new ArrayList<>();

        try {
            String repoPath = JGitUtil.getRepoPath(username, repoName);
            Git git = JGitUtil.openRepository(repoPath);

            if (git == null) {
                return commits;
            }

            //Add branch existence check
            Repository repository = git.getRepository();
            ObjectId branchId = repository.resolve(branch);
            if (branchId == null) {
                logger.warn("Branch {} does not exist, using default branch", branch);
                // Try to get default branch
                List<String> branches = getBranches(username, repoName);
                if (!branches.isEmpty()) {
                    // Prefer main, otherwise use first branch
                    if (branches.contains("main")) {
                        branch = "main";
                    } else {
                        branch = branches.get(0);
                    }
                }
            }

            Iterable<RevCommit> gitCommits = git.log()
                    .add(branchId != null ? branchId : repository.resolve("HEAD"))
                    .setMaxCount(limit)
                    .call();

            for (RevCommit gitCommit : gitCommits) {
                CommitInfo commit = new CommitInfo();
                commit.setHash(gitCommit.getName());
                commit.setShortHash(gitCommit.getName().substring(0, 7));
                commit.setAuthor(gitCommit.getAuthorIdent().getName());
                commit.setEmail(gitCommit.getAuthorIdent().getEmailAddress());
                commit.setTime(gitCommit.getAuthorIdent().getWhen());
                commit.setMessage(gitCommit.getShortMessage());

                commits.add(commit);
            }

            git.close();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }

        return commits;
    }

    // Get branch list
    public List<String> getBranches(String username, String repoName) {
        List<String> branches = new ArrayList<>();

        try {
            String repoPath = JGitUtil.getRepoPath(username, repoName);
            Git git = JGitUtil.openRepository(repoPath);

            if (git == null) {
                return branches;
            }

            List<org.eclipse.jgit.lib.Ref> refs = git.branchList().call();

            for (org.eclipse.jgit.lib.Ref ref : refs) {
                String branchName = ref.getName().replace("refs/heads/", "");
                branches.add(branchName);
            }

            git.close();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return branches;
    }

    // Get last commit information for a file
    private RevCommit getLastCommitForPath(Git git, String path, String fileName) {
        try {
            String fullPath = (path != null && !path.isEmpty()) ? path + "/" + fileName : fileName;

            Iterable<RevCommit> commits = git.log()
                    .addPath(fullPath)
                    .setMaxCount(1)
                    .call();

            for (RevCommit commit : commits) {
                return commit;
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void createInitialCommit(String repoName, User user) {
        // 获取用户名
        String username = user.getUsername();

        String repoPath = com.example.myhub.util.JGitUtil.getRepoPath(username, repoName);
        String tempPath = com.example.myhub.util.Config.get("temp.dir", "D:/MyGitRepoStore/temp")
                + "/" + System.currentTimeMillis();

        File tempDir = new File(tempPath);

        try {
            // Clone
            org.eclipse.jgit.api.Git git = org.eclipse.jgit.api.Git.cloneRepository()
                    .setURI("file:///" + repoPath)
                    .setDirectory(tempDir)
                    .call();

            // Write README
            File readme = new File(tempDir, "README.md");
            String content = "# " + repoName + "\n\nThis repository is created by " + username;
            java.nio.file.Files.write(readme.toPath(), content.getBytes("UTF-8"));

            // Add
            git.add().addFilepattern("README.md").call();

            // Commit (modified here! Use real user identity!)
            git.commit()
                    .setMessage("Initial commit") // Commit message
                    .setSign(false)
                    // Use current logged-in user's name and email! This is authentic!
                    .setCommitter(user.getUsername(), user.getEmail())
                    .setAuthor(user.getUsername(), user.getEmail())
                    .call();

            // Push
            git.push().call();
            git.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(tempDir);
            } catch (Exception ignored) {}
        }
    }

    // Fix: Add logger reference
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GitService.class);
}