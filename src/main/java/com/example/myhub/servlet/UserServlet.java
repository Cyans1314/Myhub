package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling user authentication and profile management
 * ============================================================================
 */

import com.example.myhub.bean.User;
import com.example.myhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import com.example.myhub.dao.RepoDao;
import com.example.myhub.dao.StarDao;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 1024 * 1024 * 5,        // 5 MB
        maxRequestSize = 1024 * 1024 * 10     // 10 MB
)
public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the path after /user
        // For example, accessing /user/login will get /login here
        String path = request.getPathInfo();

        // Null check to prevent NullPointerException
        if (path == null) {
            path = "";
        }

        try {
            switch (path) {
                case "/login":
                    showLoginPage(request, response);
                    break;
                case "/register":
                    showRegisterPage(request, response);
                    break;
                case "/logout":
                    logout(request, response);
                    break;
                case "/profile":
                    showProfilePage(request, response);
                    break;
                default:
                    // Debug log: if still blank, check console output
                    System.err.println("UserServlet 404: Unable to match path [" + path + "]");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to process request: " + path, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // POST requests follow the same logic
        String path = request.getPathInfo();
        if (path == null) path = "";

        try {
            switch (path) {
                case "/login":
                    handleLogin(request, response);
                    break;
                case "/register":
                    handleRegister(request, response);  // Fixed: use UserService for registration
                    break;
                case "/updateProfile":
                    handleUpdateProfile(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to process POST: " + path, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // ================= Business Methods =================

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // If already logged in, redirect to home page
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        // Forward to JSP
        request.getRequestDispatcher("/jsp/user/login.jsp").forward(request, response);
    }

    private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
    }

    private com.example.myhub.dao.RepoDao repoDao = new com.example.myhub.dao.RepoDao();
    private com.example.myhub.dao.StarDao starDao = new com.example.myhub.dao.StarDao();

    // ================== New: User Profile Page Logic ==================
    private void showProfilePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //  Get current logged-in user
        HttpSession session = request.getSession(false);
        com.example.myhub.bean.User currentUser = (session != null) ? (com.example.myhub.bean.User) session.getAttribute("user") : null;

        // Determine "target user" to view
        String targetUsername = request.getParameter("name");
        com.example.myhub.bean.User targetUser = null;

        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            // No name provided -> view self
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            targetUser = currentUser;
        } else {
            // Name provided -> view that user
            targetUser = userService.getUserByUsername(targetUsername);
        }

        // User not found -> 404
        if (targetUser == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        // Set target user information
        request.setAttribute("targetUser", targetUser);

        // Check if viewing own profile (for showing Edit button)
        boolean isOwner = (currentUser != null && currentUser.getId() == targetUser.getId());
        request.setAttribute("isOwner", isOwner);

        // Handle Tab logic (overview, repositories, stars)
        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) tab = "overview";
        request.setAttribute("tab", tab);

        // Load different data based on Tab
        if ("overview".equals(tab)) {
            // A. Overview page: top 6 projects + contribution chart
            java.util.List<com.example.myhub.bean.Repo> popularRepos = repoDao.findPopularRepos(targetUser.getId());
            request.setAttribute("popularRepos", popularRepos);

            java.util.Map<String, Integer> contributionMap = repoDao.getContributionData(targetUser.getId());
            request.setAttribute("contributionMap", contributionMap);

        } else if ("repositories".equals(tab)) {
            // B. Repository page: show all if viewing self, only public if viewing others
            java.util.List<com.example.myhub.bean.Repo> repos;
            if (isOwner) {
                repos = repoDao.findByOwnerId(targetUser.getId()); // Get all (existing method)
            } else {
                repos = repoDao.findPublicByOwnerId(targetUser.getId()); // Get public only (new method)
            }
            request.setAttribute("repoList", repos);

        } else if ("stars".equals(tab)) {
            // C. Stars page: show starred projects
            java.util.List<com.example.myhub.bean.Repo> starredRepos = starDao.findStarredReposByUser(targetUser.getId());
            request.setAttribute("starredRepos", starredRepos);
        }

        // Forward to JSP
        request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Debug log
        System.out.println("========== Entering UserServlet handleLogin method ==========");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Received parameters -> Username: " + username + ", Password: " + password);

        try {
            // Call Service
            User user = userService.login(username, password);

            if (user != null) {
                System.out.println("========== Login successful! User ID: " + user.getId() + " ==========");
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/index");
            } else {
                System.out.println("========== Login failed: Invalid username or password ==========");
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/jsp/user/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("!!!!!!!!!! Serious exception occurred while processing login !!!!!!!!!!!");
            e.printStackTrace(); // Print stack trace
            throw new ServletException(e); // Throw exception for outer layer to catch
        }
    }

    /**
     * Fixed: Use UserService.register() method to unify password encryption logic
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Simple validation
        if (userService.checkUsernameExists(username)) {
            request.setAttribute("error", "Username already exists");
            request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
            return;
        }

        // Fixed: Use UserService's register method, which handles password encryption internally
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);  // UserService.register() will encrypt this password
        // Default avatar
        user.setAvatar("https://avatars.githubusercontent.com/u/1024025?v=4");

        // Fixed: Call UserService.register(), which handles password encryption
        if (userService.register(user)) {
            // After successful registration, get complete user information (including ID)
            User registeredUser = userService.getUserByUsername(username);
            if (registeredUser != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", registeredUser);
                response.sendRedirect(request.getContextPath() + "/index");
            } else {
                request.setAttribute("error", "Registration successful but login failed");
                request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Registration failed");
            request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/user/login");
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String oldUsername = currentUser.getUsername();
        String username = null;
        String email = null;
        String bio = null;
        String avatarPath = currentUser.getAvatar(); // Default: keep original avatar

        // Handle multipart/form-data
        try {
            for (javax.servlet.http.Part part : request.getParts()) {
                String partName = part.getName();
                if ("avatar".equals(partName) && part.getSize() > 0) {
                    // Handle avatar upload
                    String fileName = getFileName(part);
                    if (fileName != null && !fileName.isEmpty()) {
                        // Generate unique filename
                        String ext = fileName.substring(fileName.lastIndexOf("."));
                        String newFileName = "avatar_" + currentUser.getId() + "_" + System.currentTimeMillis() + ext;

                        // Save to webapp/uploads/avatars directory
                        String uploadDir = request.getServletContext().getRealPath("/uploads/avatars");
                        java.io.File dir = new java.io.File(uploadDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String filePath = uploadDir + java.io.File.separator + newFileName;
                        part.write(filePath);

                        // Set relative path for database storage
                        avatarPath = request.getContextPath() + "/uploads/avatars/" + newFileName;
                    }
                } else if ("username".equals(partName)) {
                    username = readPartValue(part);
                } else if ("email".equals(partName)) {
                    email = readPartValue(part);
                } else if ("bio".equals(partName)) {
                    bio = readPartValue(part);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process file upload", e);
        }

        // If username changed, need to rename folder on disk
        if (username != null && !username.equals(oldUsername)) {
            com.example.myhub.util.JGitUtil.renameUserFolder(oldUsername, username);
        }

        // Update database
        com.example.myhub.dao.UserDao userDao = new com.example.myhub.dao.UserDao();
        boolean success = userDao.updateProfile(currentUser.getId(), username, email, bio, avatarPath);

        if (success) {
            // Update user information in session
            currentUser.setUsername(username);
            currentUser.setEmail(email);
            currentUser.setBio(bio);
            currentUser.setAvatar(avatarPath);
            session.setAttribute("user", currentUser);
        }

        // Redirect back to profile page
        response.sendRedirect(request.getContextPath() + "/user/profile?name=" + java.net.URLEncoder.encode(username, "UTF-8"));
    }

    private String getFileName(javax.servlet.http.Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private String readPartValue(javax.servlet.http.Part part) throws IOException {
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(part.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

}
