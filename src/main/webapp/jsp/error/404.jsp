<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: 404 error page
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/header.jsp" %>

<div class="container-xl p-responsive py-6 text-center" style="margin-top: 50px;">
    <!-- Reference GitHub official 404 image -->
    <img src="https://github.githubassets.com/images/modules/site/octocat-404.png" alt="404" style="width: 300px;">

    <h1 class="mt-4">This is not the web page you are looking for.</h1>
    <p class="f4 color-fg-muted mt-2">404 - Page Not Found</p>

    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Take me home</a>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>