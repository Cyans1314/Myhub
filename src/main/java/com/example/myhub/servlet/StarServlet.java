package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling repository star/unstar operations
 * ============================================================================
 */

import com.example.myhub.bean.Star;
import com.example.myhub.bean.User;
import com.example.myhub.dao.RepoDao;
import com.example.myhub.dao.StarDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class StarServlet extends HttpServlet {
    private StarDao starDao = new StarDao();
    private RepoDao repoDao = new RepoDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            out.print("{\"success\":false,\"message\":\"Please login first\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        String repoIdStr = request.getParameter("repoId");
        String action = request.getParameter("action"); // "star" or "unstar"

        if (repoIdStr == null || action == null) {
            out.print("{\"success\":false,\"message\":\"Missing parameters\"}");
            return;
        }

        try {
            int repoId = Integer.parseInt(repoIdStr);
            boolean success;
            int newCount;

            if ("star".equals(action)) {
                // Check if already starred
                if (starDao.isStarred(user.getId(), repoId)) {
                    newCount = starDao.getStarCount(repoId);
                    out.print("{\"success\":true,\"starred\":true,\"count\":" + newCount + "}");
                    return;
                }

                Star star = new Star();
                star.setUserId(user.getId());
                star.setRepoId(repoId);
                success = starDao.addStar(star);

                if (success) {
                    // Update repository star count
                    repoDao.updateStarCount(repoId, 1);
                    newCount = starDao.getStarCount(repoId);
                    out.print("{\"success\":true,\"starred\":true,\"count\":" + newCount + "}");
                } else {
                    out.print("{\"success\":false,\"message\":\"Failed to star\"}");
                }
            } else if ("unstar".equals(action)) {
                success = starDao.removeStar(user.getId(), repoId);

                if (success) {
                    // Update repository star count
                    repoDao.updateStarCount(repoId, -1);
                    newCount = starDao.getStarCount(repoId);
                    out.print("{\"success\":true,\"starred\":false,\"count\":" + newCount + "}");
                } else {
                    out.print("{\"success\":false,\"message\":\"Failed to unstar\"}");
                }
            } else {
                out.print("{\"success\":false,\"message\":\"Invalid action\"}");
            }

        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"Invalid repo ID\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        String repoIdStr = request.getParameter("repoId");

        if (repoIdStr == null) {
            out.print("{\"success\":false,\"message\":\"Missing repo ID\"}");
            return;
        }

        try {
            int repoId = Integer.parseInt(repoIdStr);
            int count = starDao.getStarCount(repoId);
            boolean starred = user != null && starDao.isStarred(user.getId(), repoId);

            out.print("{\"success\":true,\"starred\":" + starred + ",\"count\":" + count + "}");

        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"Invalid repo ID\"}");
        }
    }
}
