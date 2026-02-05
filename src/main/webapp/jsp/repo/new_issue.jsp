<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Create new issue page
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>New Issue · ${param.owner}/${param.repo}</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/repo-detail.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/issues.css" rel="stylesheet" />
</head>
<body>

<!-- Header with tab=issues -->
<jsp:include page="../common/header_detail.jsp">
    <jsp:param name="owner" value="${param.owner}"/>
    <jsp:param name="repo" value="${param.repo}"/>
    <jsp:param name="tab" value="issues"/>
</jsp:include>

<div class="new-issue-container">
    <div class="new-issue-header">
        <h2 style="font-size: 24px; font-weight: 600; margin: 0;">New issue</h2>
    </div>

    <form action="${pageContext.request.contextPath}/issue/create" method="post" class="new-issue-form">
        <input type="hidden" name="owner" value="${param.owner}">
        <input type="hidden" name="repo" value="${param.repo}">

        <div class="form-group">
            <label class="form-label" for="title">Title</label>
            <input type="text"
                   id="title"
                   name="title"
                   class="form-input"
                   placeholder="Title"
                   required
                   autofocus>
        </div>

        <div class="form-group">
            <label class="form-label" for="content">Description</label>
            <textarea id="content"
                      name="content"
                      class="form-textarea"
                      placeholder="Leave a comment"
                      required></textarea>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/issue/list?owner=${param.owner}&repo=${param.repo}"
               class="btn btn-secondary">
                Cancel
            </a>
            <button type="submit" class="btn btn-primary">
                Submit new issue
            </button>
        </div>
    </form>
</div>



</body>
</html>
