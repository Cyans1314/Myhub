package com.example.myhub.filter;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Authentication filter for request authorization
 * ============================================================================
 */

import com.example.myhub.bean.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // Whitelist path prefixes
    private static final String[] ALLOWED_PATHS = {
            "/user/login",
            "/user/register",
            "/git/",
            "/index.jsp", // Allow access to home page container (specific data controlled by Servlet)
            "/index"      // Allow access to home page data
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length()); // Get relative path, e.g. /css/style.css

        // Git protocol requests are allowed directly without any checks!
        if (path.startsWith("/git/")) {
            chain.doFilter(request, response);
            return;
        }

        // Static resources are allowed directly!
        // If the request is for css, js, images, etc., pass through without checking login!
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") || path.startsWith("/uploads/") || path.endsWith(".css") || path.endsWith(".js")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Check whitelist
        boolean allowed = false;
        for (String allowedPath : ALLOWED_PATHS) {
            // Change equals to startsWith
            // This way "/git/admin/test1.git" can match "/git/"!
            if (path.startsWith(allowedPath)) {
                allowed = true;
                break;
            }
        }

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        // 3. Logic judgment
        if (allowed || isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            // Not logged in and accessing restricted resources -> redirect to login
            httpResponse.sendRedirect(contextPath + "/user/login");
        }
    }

    @Override
    public void destroy() {}
}