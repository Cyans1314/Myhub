package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling issue management
 * ============================================================================
 */

import com.example.myhub.bean.Comment;
import com.example.myhub.bean.Issue;
import com.example.myhub.bean.Repo;
import com.example.myhub.bean.User;
import com.example.myhub.dao.IssueDao;
import com.example.myhub.dao.CommentDao;
import com.example.myhub.dao.RepoDao;
import com.example.myhub.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class IssueServlet extends HttpServlet {
    private IssueDao issueDao = new IssueDao();
    private CommentDao commentDao = new CommentDao();
    private RepoDao repoDao = new RepoDao();
    private UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "";

        try {
            if (path.equals("/list")) {
                listIssues(request, response);
            } else if (path.equals("/detail")) {
                showIssueDetail(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "";

        try {
            if (path.equals("/create")) {
                createIssue(request, response);
            } else if (path.equals("/comment")) {
                addComment(request, response);
            } else if (path.equals("/close")) {
                closeIssue(request, response);
            } else if (path.equals("/reopen")) {
                reopenIssue(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listIssues(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ownerName = request.getParameter("owner");
        String repoName = request.getParameter("repo");
        String status = request.getParameter("status");

        if (ownerName == null || repoName == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing owner or repo parameter");
            return;
        }

        // Get user and repository
        User owner = userDao.findByUsername(ownerName);
        if (owner == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        Repo repo = repoDao.findByOwnerAndName(owner.getId(), repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Repository not found");
            return;
        }

        // Get Issues
        List<Issue> issues;
        if ("closed".equals(status)) {
            issues = issueDao.findByRepoAndStatus(repo.getId(), "CLOSED");
        } else {
            issues = issueDao.findByRepoAndStatus(repo.getId(), "OPEN");
        }

        // Add username and comment count to each Issue
        for (Issue issue : issues) {
            User issueUser = userDao.findById(issue.getUserId());
            if (issueUser != null) {
                issue.setUsername(issueUser.getUsername());
            }
            int commentCount = commentDao.countByIssue(issue.getId());
            issue.setCommentCount(commentCount);
        }

        // Count statistics
        int openCount = issueDao.countByRepoAndStatus(repo.getId(), "OPEN");
        int closedCount = issueDao.countByRepoAndStatus(repo.getId(), "CLOSED");

        // Pass objects to JSP
        request.setAttribute("owner", owner);
        request.setAttribute("repo", repo);
        request.setAttribute("issues", issues);
        request.setAttribute("openCount", openCount);
        request.setAttribute("closedCount", closedCount);

        // Forward to JSP with URL parameters for header_detail.jsp to read
        request.getRequestDispatcher("/jsp/repo/issues.jsp?owner=" + ownerName + "&repo=" + repoName).forward(request, response);
    }

    private void showIssueDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String issueIdStr = request.getParameter("id");
        String ownerName = request.getParameter("owner");
        String repoName = request.getParameter("repo");

        if (issueIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing issue ID");
            return;
        }

        try {
            int issueId = Integer.parseInt(issueIdStr);
            Issue issue = issueDao.findById(issueId);

            if (issue == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Issue not found");
                return;
            }

            // Get owner and repo objects
            User owner = userDao.findByUsername(ownerName);
            Repo repo = null;
            if (owner != null) {
                repo = repoDao.findByOwnerAndName(owner.getId(), repoName);
            }

            // Get Issue author information
            User issueUser = userDao.findById(issue.getUserId());
            if (issueUser != null) {
                issue.setUsername(issueUser.getUsername());
            }

            // Get comment list
            List<Comment> comments = commentDao.findByIssue(issueId);

            // Add username to each comment
            for (Comment comment : comments) {
                User commentUser = userDao.findById(comment.getUserId());
                if (commentUser != null) {
                    comment.setUsername(commentUser.getUsername());
                }
            }

            int commentCount = comments.size();

            // Pass objects to JSP
            request.setAttribute("owner", owner);
            request.setAttribute("repo", repo);
            request.setAttribute("issue", issue);
            request.setAttribute("comments", comments);
            request.setAttribute("commentCount", commentCount);

            // Forward with URL parameters
            request.getRequestDispatcher("/jsp/repo/issue_detail.jsp?owner=" + ownerName + "&repo=" + repoName).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid issue ID");
        }
    }

    private void createIssue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String owner = request.getParameter("owner");
        String repoName = request.getParameter("repo");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (owner == null || repoName == null || title == null || content == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }

        // Get repository
        User repoOwner = userDao.findByUsername(owner);
        if (repoOwner == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        Repo repo = repoDao.findByOwnerAndName(repoOwner.getId(), repoName);
        if (repo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Repository not found");
            return;
        }

        // Create Issue
        Issue issue = new Issue();
        issue.setRepoId(repo.getId());
        issue.setUserId(user.getId());
        issue.setTitle(title.trim());
        issue.setContent(content.trim());
        issue.setStatus("OPEN");

        boolean success = issueDao.insert(issue);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/issue/detail?id=" + issue.getId() +
                    "&owner=" + owner + "&repo=" + repoName);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create issue");
        }
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String issueIdStr = request.getParameter("issueId");
        String owner = request.getParameter("owner");
        String repoName = request.getParameter("repo");
        String commentContent = request.getParameter("content");

        if (issueIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing issue ID");
            return;
        }

        try {
            int issueId = Integer.parseInt(issueIdStr);

            // Only create comment if content is not empty
            if (commentContent != null && !commentContent.trim().isEmpty()) {
                Comment comment = new Comment();
                comment.setIssueId(issueId);
                comment.setUserId(user.getId());
                comment.setContent(commentContent.trim());
                commentDao.insert(comment);
            }

            response.sendRedirect(request.getContextPath() + "/issue/detail?id=" + issueId +
                    "&owner=" + owner + "&repo=" + repoName);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid issue ID");
        }
    }

    private void closeIssue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        changeIssueStatus(request, response, "CLOSED");
    }

    private void reopenIssue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        changeIssueStatus(request, response, "OPEN");
    }

    private void changeIssueStatus(HttpServletRequest request, HttpServletResponse response, String status)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        String issueIdStr = request.getParameter("issueId");
        String owner = request.getParameter("owner");
        String repoName = request.getParameter("repo");
        String commentContent = request.getParameter("content");

        if (issueIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing issue ID");
            return;
        }

        try {
            int issueId = Integer.parseInt(issueIdStr);

            // If there is comment content, add comment first
            if (commentContent != null && !commentContent.trim().isEmpty()) {
                User user = (User) session.getAttribute("user");
                Comment comment = new Comment();
                comment.setIssueId(issueId);
                comment.setUserId(user.getId());
                comment.setContent(commentContent.trim());
                commentDao.insert(comment);
            }

            // Update Issue status
            boolean success = issueDao.updateStatus(issueId, status);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/issue/detail?id=" + issueId +
                        "&owner=" + owner + "&repo=" + repoName);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update issue status");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid issue ID");
        }
    }
}
