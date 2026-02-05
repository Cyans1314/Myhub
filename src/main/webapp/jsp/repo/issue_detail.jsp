<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Issue detail page showing issue content and comments
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>${issue.title} · Issue #${issue.id} · ${param.owner}/${param.repo}</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/repo-detail.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/issues.css" rel="stylesheet" />
</head>
<body>

<!-- Header -->
<jsp:include page="../common/header_detail.jsp">
    <jsp:param name="tab" value="issues"/>
</jsp:include>

<div class="issue-detail-container">
    <div class="issue-header">
        <div class="issue-title-row">
            <h1 class="issue-title-text">${issue.title}</h1>
            <span class="issue-number-badge">#${issue.id}</span>
        </div>
        <div class="issue-status-bar">
            <span class="status-badge ${issue.status == 'OPEN' ? 'open' : 'closed'}">
                <i class="far ${issue.status == 'OPEN' ? 'fa-circle-dot' : 'fa-circle-check'}"></i>
                ${issue.status == 'OPEN' ? 'Open' : 'Closed'}
            </span>
            <span class="issue-meta-text">
                <strong>${issue.username != null ? issue.username : 'User'}</strong>
                opened this issue
                <fmt:formatDate value="${issue.createdAt}" pattern="MMM d, yyyy"/>
                · ${commentCount != null ? commentCount : 0} comments
            </span>
        </div>
    </div>

    <!-- Issue主楼 -->
    <div class="comment-thread">
        <div class="comment-box">
            <div class="comment-header">
                <strong class="comment-author">${issue.username != null ? issue.username : 'User'}</strong>
                <span class="comment-badge">Author</span>
                <span class="comment-time">
                    commented <fmt:formatDate value="${issue.createdAt}" pattern="MMM d, yyyy"/>
                </span>
            </div>
            <div class="comment-body">
                ${issue.content}
            </div>
        </div>
    </div>

    <!-- 评论列表 -->
    <c:if test="${not empty comments}">
        <c:forEach var="comment" items="${comments}">
            <div class="comment-thread">
                <div class="comment-box">
                    <div class="comment-header">
                        <strong class="comment-author">${comment.username != null ? comment.username : 'User'}</strong>
                        <span class="comment-time">
                            commented <fmt:formatDate value="${comment.createdAt}" pattern="MMM d, yyyy"/>
                        </span>
                    </div>
                    <div class="comment-body">
                            ${comment.content}
                    </div>
                </div>
            </div>
        </c:forEach>
    </c:if>

    <!-- 评论框 -->
    <div class="new-comment-form">
        <div class="comment-form-header">
            Write a comment
        </div>
        <div class="comment-form-body">
            <form action="${pageContext.request.contextPath}/issue/comment" method="post">
                <input type="hidden" name="issueId" value="${issue.id}">
                <input type="hidden" name="owner" value="${param.owner}">
                <input type="hidden" name="repo" value="${param.repo}">
                <textarea name="content" class="comment-textarea" placeholder="Leave a comment"></textarea>
                <div class="comment-form-actions">
                    <c:if test="${issue.status == 'OPEN'}">
                        <button type="submit" formaction="${pageContext.request.contextPath}/issue/close" class="btn btn-danger">
                            Close issue
                        </button>
                    </c:if>
                    <c:if test="${issue.status == 'CLOSED'}">
                        <button type="submit" formaction="${pageContext.request.contextPath}/issue/reopen" class="btn btn-secondary">
                            Reopen issue
                        </button>
                    </c:if>
                    <button type="submit" class="btn btn-primary">
                        Comment
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>


</body>
</html>
