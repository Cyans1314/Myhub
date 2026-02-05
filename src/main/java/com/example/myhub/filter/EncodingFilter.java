package com.example.myhub.filter;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Character encoding filter for UTF-8 encoding
 * ============================================================================
 */

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class EncodingFilter implements Filter {
    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null) {
            encoding = encodingParam;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Set request encoding (prevent Chinese character garbling)
        httpRequest.setCharacterEncoding(encoding);

        // 2. Set response encoding (prevent garbling)
        httpResponse.setCharacterEncoding(encoding);

        // Never write the line below! CSS will break if you do!
        // httpResponse.setContentType("text/html;charset=" + encoding);

        // 3. Pass through
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}