package com.example.myhub.util;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: JGit utility for Git repository operations
 * ============================================================================
 */

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;
import java.io.IOException;

public class JGitUtil {

    /**
     * Initialize Git bare repository
     * @param repoPath Repository path (full path)
     * @return Whether successful
     */
    public static boolean initBareRepository(String repoPath) {
        try {
            File repoDir = new File(repoPath);
            if (!repoDir.exists()) {
                repoDir.mkdirs();
            }

            Git.init()
                    .setBare(true)
                    .setDirectory(repoDir)
                    .call();

            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Open existing repository
     * @param repoPath Repository path
     * @return Git object, returns null on failure
     */
    public static Git openRepository(String repoPath) {
        try {
            File repoDir = new File(repoPath);
            if (!repoDir.exists()) {
                return null;
            }

            Repository repository = new FileRepositoryBuilder()
                    .setGitDir(new File(repoPath))
                    .readEnvironment()
                    .findGitDir()
                    .build();

            return new Git(repository);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get repository path (based on username and repository name)
     * @param username Username
     * @param repoName Repository name
     * @return Full repository path
     */
    public static String getRepoPath(String username, String repoName) {
        String basePath = Config.get("git.repo.base.path", "D:/MyGitRepoStore");
        return basePath + "/" + username + "/" + repoName + ".git";
    }

    /**
     * Check if repository exists
     * @param username Username
     * @param repoName Repository name
     * @return Whether exists
     */
    public static boolean repositoryExists(String username, String repoName) {
        String repoPath = getRepoPath(username, repoName);
        File repoDir = new File(repoPath);
        return repoDir.exists() && repoDir.isDirectory();
    }

    /**
     * Delete repository
     * @param username Username
     * @param repoName Repository name
     * @return Whether successful
     */
    public static boolean deleteRepository(String username, String repoName) {
        String repoPath = getRepoPath(username, repoName);
        File repoDir = new File(repoPath);

        if (repoDir.exists()) {
            return deleteDirectory(repoDir);
        }
        return false;
    }

    /**
     * Recursively delete directory
     */
    private static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        return dir.delete();
    }

    /**
     * Rename repository folder
     * @param username Username
     * @param oldRepoName Old repository name
     * @param newRepoName New repository name
     * @return Whether successful
     */
    public static boolean renameRepository(String username, String oldRepoName, String newRepoName) {
        String oldPath = getRepoPath(username, oldRepoName);
        String newPath = getRepoPath(username, newRepoName);
        File oldDir = new File(oldPath);
        File newDir = new File(newPath);

        if (oldDir.exists()) {
            return oldDir.renameTo(newDir);
        }
        return false;
    }

    /**
     * Rename user folder (including all repositories)
     * @param oldUsername Old username
     * @param newUsername New username
     * @return Whether successful
     */
    public static boolean renameUserFolder(String oldUsername, String newUsername) {
        String basePath = Config.get("git.repo.base.path", "D:/MyGitRepoStore");
        File oldDir = new File(basePath + "/" + oldUsername);
        File newDir = new File(basePath + "/" + newUsername);

        if (oldDir.exists()) {
            return oldDir.renameTo(newDir);
        }
        return true; // If old folder does not exist, also consider it successful
    }
}