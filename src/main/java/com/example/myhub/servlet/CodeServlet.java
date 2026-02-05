package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling code browsing operations
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import com.example.myhub.bean.User;
import com.example.myhub.dto.CommitInfo;
import com.example.myhub.dto.FileNode;
import com.example.myhub.service.GitService;
import com.example.myhub.service.RepoService;
import com.example.myhub.service.UserService;
import com.example.myhub.util.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class CodeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CodeServlet.class);
    private GitService gitService = new GitService();
    private RepoService repoService = new RepoService();
    private UserService userService = new UserService();  // New: used to find user by username

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "";

        try {
            if (path.startsWith("/tree/")) {
                // Format: /tree/{owner}/{repoName}/{branch}/{path}
                showTree(request, response, path);
            } else if (path.startsWith("/blob/")) {
                // Format: /blob/{owner}/{repoName}/{branch}/{filePath}
                showBlob(request, response, path);
            } else if (path.startsWith("/commits/")) {
                // Format: /commits/{owner}/{repoName}/{branch}
                showCommits(request, response, path);
            } else if (path.startsWith("/branches/")) {
                // Format: /branches/{owner}/{repoName}
                showBranches(request, response, path);
            } else if (path.startsWith("/raw/")) {
                // Format: /raw/{owner}/{repoName}/{branch}/{filePath}
                showRawFile(request, response, path);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to process code request: " + path, e);
            request.setAttribute("error", "Internal server error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error/500.jsp").forward(request, response);
        }
    }

    private void showTree(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path: /tree/{owner}/{repoName}/{branch}/{path}
        String[] parts = path.substring(6).split("/");
        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];
        String branch = parts[2];
        String filePath = parts.length > 3 ?
                String.join("/", java.util.Arrays.copyOfRange(parts, 3, parts.length)) : "";

        // Get repository information
        Repo repo = getRepoByOwnerAndName(ownerName, repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check repository permission
        if (!checkRepoAccess(request, repo)) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // Get file list
        List<FileNode> files = gitService.listFiles(ownerName, repoName, branch, filePath);

        request.setAttribute("repo", repo);
        request.setAttribute("ownerName", ownerName);
        request.setAttribute("branch", branch);
        request.setAttribute("path", filePath);
        request.setAttribute("files", files);

        // Check if there is README.md
        String readmeContent = getReadmeContent(ownerName, repoName, branch, filePath);
        if (readmeContent != null) {
            request.setAttribute("readme", readmeContent);
        }

        request.getRequestDispatcher("/jsp/repo/tree.jsp").forward(request, response);
    }

    private void showBlob(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path: /blob/{owner}/{repoName}/{branch}/{filePath}
        String[] parts = path.substring(6).split("/");
        if (parts.length < 4) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];
        String branch = parts[2];
        String filePath = String.join("/", java.util.Arrays.copyOfRange(parts, 3, parts.length));

        // Get repository information
        Repo repo = getRepoByOwnerAndName(ownerName, repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check repository permission
        if (!checkRepoAccess(request, repo)) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // Get file content
        String content = gitService.getFileContent(ownerName, repoName, branch, filePath);

        if (content == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get file type
        String fileType = getFileType(filePath);

        request.setAttribute("repo", repo);
        request.setAttribute("ownerName", ownerName);
        request.setAttribute("branch", branch);
        request.setAttribute("path", filePath);
        request.setAttribute("content", content);
        request.setAttribute("fileType", fileType);

        // If Markdown file, render as HTML
        if (MarkdownUtil.isMarkdownFile(filePath)) {
            String htmlContent = MarkdownUtil.markdownToHtml(content);
            request.setAttribute("htmlContent", htmlContent);
        }

        request.getRequestDispatcher("/jsp/repo/blob.jsp").forward(request, response);
    }

    private void showCommits(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path: /commits/{owner}/{repoName}/{branch}
        String[] parts = path.substring(9).split("/");
        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];
        String branch = parts[2];

        // Get repository information
        Repo repo = getRepoByOwnerAndName(ownerName, repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check repository permission
        if (!checkRepoAccess(request, repo)) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // Get commit history
        List<CommitInfo> commits = gitService.getCommitHistory(ownerName, repoName, branch, 50);

        request.setAttribute("repo", repo);
        request.setAttribute("ownerName", ownerName);
        request.setAttribute("branch", branch);
        request.setAttribute("commits", commits);

        request.getRequestDispatcher("/jsp/repo/commits.jsp").forward(request, response);
    }

    private void showBranches(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path: /branches/{owner}/{repoName}
        String[] parts = path.substring(10).split("/");
        if (parts.length < 2) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];

        // Get repository information
        Repo repo = getRepoByOwnerAndName(ownerName, repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check repository permission
        if (!checkRepoAccess(request, repo)) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // Get branch list
        List<String> branches = gitService.getBranches(ownerName, repoName);

        request.setAttribute("repo", repo);
        request.setAttribute("ownerName", ownerName);
        request.setAttribute("branches", branches);
        request.setAttribute("defaultBranch", repo.getDefaultBranch());

        request.getRequestDispatcher("/jsp/repo/branches.jsp").forward(request, response);
    }

    private void showRawFile(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path: /raw/{owner}/{repoName}/{branch}/{filePath}
        String[] parts = path.substring(5).split("/");
        if (parts.length < 4) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];
        String branch = parts[2];
        String filePath = String.join("/", java.util.Arrays.copyOfRange(parts, 3, parts.length));

        // Get repository information
        Repo repo = getRepoByOwnerAndName(ownerName, repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check repository permission
        if (!checkRepoAccess(request, repo)) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // Get file content
        String content = gitService.getFileContent(ownerName, repoName, branch, filePath);

        if (content == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set response headers
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Content-Disposition", "inline");

        // Write response
        response.getWriter().write(content);
    }

    /**
     * Fixed: Get repository by owner name and repo name
     * 1. First find user by username
     * 2. Then find repository by user ID and repo name
     */
    private Repo getRepoByOwnerAndName(String ownerName, String repoName) {
        try {
            // 1. Find user by username
            User owner = userService.getUserByUsername(ownerName);
            if (owner == null) {
                logger.warn("User does not exist: " + ownerName);
                return null;
            }

            // 2. Find repository by user ID and repo name
            Repo repo = repoService.getRepoByOwnerAndName(owner.getId(), repoName);
            if (repo == null) {
                logger.warn("Repository does not exist: ownerId={}, repoName={}", owner.getId(), repoName);
                return null;
            }

            return repo;
        } catch (Exception e) {
            logger.error("Failed to get repository: owner={}, repo={}", ownerName, repoName, e);
            return null;
        }
    }

    private boolean checkRepoAccess(HttpServletRequest request, Repo repo) {
        HttpSession session = request.getSession(false);

        // If repository is public, anyone can access
        if (repo.isPublic()) {
            return true;
        }

        // If private repository, user must be logged in and be the repository owner
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        return user != null && user.getId() == repo.getOwnerId();
    }

    private String getReadmeContent(String ownerName, String repoName, String branch, String path) {
        // Check for README.md in current directory
        String[] readmeFiles = {"README.md", "readme.md", "README.MD"};

        for (String filename : readmeFiles) {
            String filePath = path.isEmpty() ? filename : path + "/" + filename;
            String content = gitService.getFileContent(ownerName, repoName, branch, filePath);
            if (content != null && !content.isEmpty()) {
                return MarkdownUtil.markdownToHtml(content);
            }
        }

        return null;
    }

    private String getFileType(String filename) {
        if (filename == null) return "text";

        filename = filename.toLowerCase();

        if (filename.endsWith(".java")) return "java";
        if (filename.endsWith(".js")) return "javascript";
        if (filename.endsWith(".html") || filename.endsWith(".htm")) return "html";
        if (filename.endsWith(".css")) return "css";
        if (filename.endsWith(".xml")) return "xml";
        if (filename.endsWith(".md")) return "markdown";
        if (filename.endsWith(".py")) return "python";
        if (filename.endsWith(".cpp") || filename.endsWith(".c") || filename.endsWith(".h")) return "cpp";
        if (filename.endsWith(".sql")) return "sql";
        if (filename.endsWith(".json")) return "json";

        return "text";
    }
}