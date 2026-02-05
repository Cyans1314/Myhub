package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling repository search functionality
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import com.example.myhub.service.RepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SearchServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);
    private RepoService repoService = new RepoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");
        String type = request.getParameter("type"); // repo, user, code

        if (query == null || query.trim().isEmpty()) {
            // If no search query, redirect to home page
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        query = query.trim();

        try {
            if (type == null || type.equals("repo")) {
                // Search repositories
                List<Repo> results = repoService.searchRepos(query);

                request.setAttribute("query", query);
                request.setAttribute("type", "repo");
                request.setAttribute("results", results);
                request.setAttribute("resultCount", results.size());

                request.getRequestDispatcher("/search.jsp").forward(request, response);
            } else {
                // Other search types are not supported yet
                request.setAttribute("query", query);
                request.setAttribute("type", type);
                request.setAttribute("results", List.of());
                request.setAttribute("resultCount", 0);
                request.setAttribute("message", "Search type " + type + " is not supported yet");

                request.getRequestDispatcher("/search.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Search failed: query={}, type={}", query, type, e);
            request.setAttribute("error", "Search failed: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error/500.jsp").forward(request, response);
        }
    }
}