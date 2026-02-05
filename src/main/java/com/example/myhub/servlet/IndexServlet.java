package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling home page
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import com.example.myhub.bean.User;
import com.example.myhub.service.RepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class IndexServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(IndexServlet.class);
    private RepoService repoService = new RepoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if (path.equals("/") || path.equals("/index")) {
                handleHomePage(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to handle home page request", e);
            request.setAttribute("error", "服务器内部错误: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error/500.jsp").forward(request, response);
        }
    }

    private void handleHomePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        // Get public repository list
        List<Repo> publicRepos = repoService.getPublicRepos();
        request.setAttribute("publicRepos", publicRepos);
        request.setAttribute("publicRepoCount", publicRepos.size());

        // If user is logged in, get user's repositories
        if (user != null) {
            List<Repo> userRepos = repoService.getUserRepos(user.getId());
            request.setAttribute("userRepos", userRepos);
            request.setAttribute("userRepoCount", userRepos.size());
            request.setAttribute("currentUser", user);
        }

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}