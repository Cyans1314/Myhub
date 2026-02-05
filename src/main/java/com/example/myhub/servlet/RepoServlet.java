package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling repository operations
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import com.example.myhub.bean.User;
import com.example.myhub.dao.RepoDao;
import com.example.myhub.dao.UserDao;
import com.example.myhub.service.RepoService;
import com.example.myhub.util.Config;
import com.example.myhub.util.JGitUtil;
import com.example.myhub.util.ZipUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.example.myhub.service.GitService;
import com.example.myhub.dto.FileNode;
import java.util.List;

public class RepoServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RepoServlet.class);
    private RepoService repoService = new RepoService();
    private GitService gitService = new GitService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "";

        try {
            if (path.equals("/create")) {
                showCreatePage(request, response);
            } else if (path.equals("/new")) {
                createRepo(request, response);
            } else if (path.endsWith("/download")) {
                // New: Handle ZIP download request
                handleDownloadZip(request, response, path);
            } else if (path.startsWith("/") && path.length() > 1) {
                // Format: /{owner}/{repoName}
                handleRepoDetail(request, response, path);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to process repository request: " + path, e);
            request.setAttribute("error", "Internal server error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "";

        try {
            if (path.equals("/create")) {
                processCreateRepo(request, response);
            } else if (path.equals("/delete")) {
                deleteRepo(request, response);
            } else if (path.equals("/update")) {
                updateRepo(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("处理POST请求失败: " + path, e);
            request.setAttribute("error", "服务器内部错误: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error/500.jsp").forward(request, response);
        }
    }

    private void updateRepo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String repoIdStr = request.getParameter("repoId");
        String owner = request.getParameter("owner");
        String action = request.getParameter("action");

        if (repoIdStr == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int repoId = Integer.parseInt(repoIdStr);
            Repo repo = repoService.getRepoById(repoId);

            if (repo == null || repo.getOwnerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            RepoDao repoDao = new RepoDao();
            boolean success = false;
            String oldRepoName = repo.getName();
            String newRepoName = repo.getName();

            if ("rename".equals(action)) {
                String newName = request.getParameter("newName");
                if (newName != null && !newName.trim().isEmpty() && !newName.trim().equals(oldRepoName)) {
                    // Try to rename folder on disk (may not exist, ignore failure)
                    JGitUtil.renameRepository(owner, oldRepoName, newName.trim());
                    // Update database
                    success = repoDao.updateName(repoId, newName.trim());
                    if (success) {
                        newRepoName = newName.trim();
                    }
                }
            } else if ("description".equals(action)) {
                String description = request.getParameter("description");
                success = repoDao.updateDescription(repoId, description);
            } else if ("visibility".equals(action)) {
                String visibility = request.getParameter("visibility");
                boolean isPublic = "public".equals(visibility);
                success = repoDao.updateVisibility(repoId, isPublic);
            }

            String encodedOwner = java.net.URLEncoder.encode(owner, "UTF-8");
            String encodedRepo = java.net.URLEncoder.encode(newRepoName, "UTF-8");
            response.sendRedirect(request.getContextPath() + "/jsp/repo/settings.jsp?owner=" + encodedOwner + "&repo=" + encodedRepo);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showCreatePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        request.getRequestDispatcher("/jsp/repo/create.jsp").forward(request, response);
    }

    private void createRepo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);
        request.getRequestDispatcher("/jsp/repo/create.jsp").forward(request, response);
    }

    private void handleRepoDetail(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path format: /{owner}/{repoName}
        String[] parts = path.substring(1).split("/");
        if (parts.length < 2) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];

        // Query repository owner by ownerName
        UserDao userDao = new UserDao();
        User repoOwner = userDao.findByUsername(ownerName);

        if (repoOwner == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        Repo repo = repoService.getRepoByOwnerAndName(repoOwner.getId(), repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Repository not found");
            return;
        }

        // Check access permission: public repos can be accessed by anyone, private repos only by owner
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("user") : null;

        if (!repo.isPublic()) {
            // Private repo: only owner can access
            if (currentUser == null || currentUser.getId() != repo.getOwnerId()) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
        }

        // Generate clone URL
        String cloneUrl = repoService.getCloneUrl(ownerName, repoName);
        request.setAttribute("cloneUrl", cloneUrl);

        // Generate ZIP download URL
        String zipDownloadUrl = request.getContextPath() + "/repo/" + ownerName + "/" + repoName + "/download";

        String branch = request.getParameter("branch");
        if (branch == null || branch.isEmpty()) {
            branch = repo.getDefaultBranch();
        }

        // 2. Determine current viewing path (currently in root directory, pass "")
        String currentPath = "";

        // 3. [KEY] Call GitService to read files from physical disk!
        // GitService already has listFiles method, use it directly
        List<FileNode> files = gitService.listFiles(ownerName, repoName, branch, currentPath);

        // 4. Put file list into request so JSP can iterate and display
        request.setAttribute("files", files);
        request.setAttribute("branch", branch);
        request.setAttribute("currentPath", currentPath);

        request.setAttribute("zipDownloadUrl", zipDownloadUrl);
        request.setAttribute("repo", repo);
        request.setAttribute("owner", repoOwner);

        // If there are sub-paths, like /{owner}/{repoName}/tree, handle by other Servlet
        if (parts.length > 2) {
            // Redirect to corresponding handler
            String subPath = String.join("/", java.util.Arrays.copyOfRange(parts, 2, parts.length));
            request.getRequestDispatcher("/code/" + subPath).forward(request, response);
        } else {
            // Show repository home page
            request.getRequestDispatcher("/jsp/repo/detail.jsp").forward(request, response);
        }
    }

    /**
     * New: Handle ZIP download request
     * Path format: /{owner}/{repoName}/download
     * Optional parameter: branch (defaults to repository default branch)
     */
    private void handleDownloadZip(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Parse path: /{owner}/{repoName}/download
        String[] parts = path.substring(1).split("/");
        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ownerName = parts[0];
        String repoName = parts[1];
        String branch = request.getParameter("branch"); // Optional parameter

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        // Get repository information
        Repo repo = null;
        if (user != null && user.getUsername().equals(ownerName)) {
            repo = repoService.getRepoByOwnerAndName(user.getId(), repoName);
        }

        // If user doesn't match or repo doesn't exist, try to find public repo
        if (repo == null) {
            // Here we need to first query user table to get ownerId, simplified handling: use current user for now
            // In real application, should query user table first to get ownerId
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // Check repository permission
        if (!repo.isPublic() && (user == null || user.getId() != repo.getOwnerId())) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // If branch not specified, use repository default branch
        if (branch == null || branch.trim().isEmpty()) {
            branch = repo.getDefaultBranch();
        }

        try {
            // Get Git repository path
            String repoPath = JGitUtil.getRepoPath(ownerName, repoName);
            File repoDir = new File(repoPath);

            if (!repoDir.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Repository does not exist");
                return;
            }

            // Create temporary working directory
            String tempDirPath = Config.get("temp.dir", "D:/MyGitRepoStore/temp");
            Path tempDir = Files.createTempDirectory(Paths.get(tempDirPath), "clone_" + repoName + "_");
            String tempDirStr = tempDir.toString();

            // Clone repository to temporary directory
            try (Git git = Git.cloneRepository()
                    .setURI("file://" + repoPath)
                    .setDirectory(tempDir.toFile())
                    .setBranch(branch)
                    .call()) {

                // Generate ZIP file
                String zipFileName = repoName + "-" + branch + "-" + System.currentTimeMillis() + ".zip";
                String zipFilePath = tempDirPath + "/" + zipFileName;

                boolean zipCreated = ZipUtil.createZip(tempDirStr, zipFilePath);

                if (!zipCreated) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create ZIP file");
                    return;
                }

                // Set response headers
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + repoName + "-" + branch + ".zip\"");
                response.setHeader("Content-Transfer-Encoding", "binary");

                // Send ZIP file
                try (FileInputStream fis = new FileInputStream(zipFilePath);
                     OutputStream out = response.getOutputStream()) {

                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    out.flush();
                }

                // Clean up temporary file
                try {
                    Files.deleteIfExists(Paths.get(zipFilePath));
                } catch (IOException e) {
                    logger.warn("Failed to delete temporary ZIP file: " + zipFilePath, e);
                }

            } catch (Exception e) {
                logger.error("Failed to clone repository: owner={}, repo={}, branch={}",
                        ownerName, repoName, branch, e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to clone repository: " + e.getMessage());
                return;
            } finally {
                // Clean up temporary directory
                try {
                    deleteDirectory(tempDir.toFile());
                } catch (IOException e) {
                    logger.warn("Failed to clean up temporary directory: " + tempDirStr, e);
                }
            }

        } catch (Exception e) {
            logger.error("Failed to process ZIP download: owner={}, repo={}", ownerName, repoName, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Download failed: " + e.getMessage());
        }
    }

    /**
     * Recursively delete directory
     */
    private void deleteDirectory(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        Files.deleteIfExists(dir.toPath());
    }

    private void processCreateRepo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }
        User user = (User) session.getAttribute("user");

        String repoName = request.getParameter("repoName");
        String description = request.getParameter("description");
        // Get Public/Private radio button from frontend
        boolean isPublic = !"private".equals(request.getParameter("visibility"));

        // Get whether frontend checked "Initialize with README"
        // Assume frontend checkbox name="autoInit"
        boolean autoInit = request.getParameter("autoInit") != null;

        if (repoName == null || repoName.trim().isEmpty()) {
            request.setAttribute("error", "Repository name cannot be empty");
            request.getRequestDispatcher("/jsp/repo/create.jsp").forward(request, response);
            return;
        }

        // Check if repository already exists
        if (repoService.repoExists(user.getId(), repoName.trim())) {
            request.setAttribute("error", "Repository name already exists");
            request.getRequestDispatcher("/jsp/repo/create.jsp").forward(request, response);
            return;
        }

        // Create repository object
        Repo repo = new Repo(repoName.trim(), description, user.getId());
        repo.setPublic(isPublic);

        // Pass autoInit parameter!
        boolean success = repoService.createRepo(repo, user, autoInit);

        if (success) {
            // Success redirect
            response.sendRedirect(request.getContextPath() + "/repo/" + user.getUsername() + "/" + repoName);
        } else {
            request.setAttribute("error", "Failed to create repository, please try again later");
            request.getRequestDispatcher("/jsp/repo/create.jsp").forward(request, response);
        }
    }

    private void deleteRepo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String repoIdStr = request.getParameter("repoId");
        String repoName = request.getParameter("repoName");

        if (repoIdStr == null || repoName == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int repoId = Integer.parseInt(repoIdStr);
            Repo repo = repoService.getRepoById(repoId);

            if (repo == null || repo.getOwnerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            boolean success = repoService.deleteRepo(repoId, user.getUsername(), repoName);

            if (success) {
                logger.info("User {} successfully deleted repository {}", user.getUsername(), repoName);
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                request.setAttribute("error", "Failed to delete repository");
                request.getRequestDispatcher("/jsp/repo/settings.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}