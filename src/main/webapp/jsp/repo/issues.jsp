<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Issues list page for repository
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.example.myhub.dao.*" %>
<%@ page import="com.example.myhub.bean.*" %>
<%@ page import="java.util.List" %>

<%
    String ownerName = request.getParameter("owner");
    String repoName = request.getParameter("repo");
    String status = request.getParameter("status");
    if (status == null) status = "open";

    UserDao userDao = new UserDao();
    RepoDao repoDao = new RepoDao();
    IssueDao issueDao = new IssueDao();
    CommentDao commentDao = new CommentDao();

    User owner = userDao.findByUsername(ownerName);
    Repo repo = null;
    List<Issue> issues = null;
    int openCount = 0;
    int closedCount = 0;

    if (owner != null) {
        repo = repoDao.findByOwnerAndName(owner.getId(), repoName);
        if (repo != null) {
            if ("closed".equals(status)) {
                issues = issueDao.findByRepoAndStatus(repo.getId(), "CLOSED");
            } else {
                issues = issueDao.findByRepoAndStatus(repo.getId(), "OPEN");
            }

            // 为每个Issue添加用户名和评论数
            if (issues != null) {
                for (Issue issue : issues) {
                    User issueUser = userDao.findById(issue.getUserId());
                    if (issueUser != null) {
                        issue.setUsername(issueUser.getUsername());
                    }
                    int commentCount = commentDao.countByIssue(issue.getId());
                    issue.setCommentCount(commentCount);
                }
            }

            openCount = issueDao.countByRepoAndStatus(repo.getId(), "OPEN");
            closedCount = issueDao.countByRepoAndStatus(repo.getId(), "CLOSED");
        }
    }

    request.setAttribute("issues", issues);
    request.setAttribute("openCount", openCount);
    request.setAttribute("closedCount", closedCount);
%>

<!DOCTYPE html>
<html>
<head>
    <title>${param.owner}/${param.repo} - Issues</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/repo-detail.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/issues.css" rel="stylesheet" />
</head>
<body>

<jsp:include page="../common/header_detail.jsp">
    <jsp:param name="tab" value="issues"/>
</jsp:include>

<div class="issues-container">
    <div class="issues-header">
        <h2 style="font-size: 24px; font-weight: 600; margin: 0;">Issues</h2>
        <a href="${pageContext.request.contextPath}/jsp/repo/new_issue.jsp?owner=${param.owner}&repo=${param.repo}"
           class="new-issue-btn">
            <i class="fas fa-plus"></i>
            New issue
        </a>
    </div>

    <div class="issues-tabs">
        <a href="?owner=${param.owner}&repo=${param.repo}&status=open"
           class="issues-tab ${empty param.status || param.status == 'open' ? 'active' : ''}">
            <i class="far fa-circle-dot"></i>
            ${openCount} Open
        </a>
        <a href="?owner=${param.owner}&repo=${param.repo}&status=closed"
           class="issues-tab ${param.status == 'closed' ? 'active' : ''}">
            <i class="far fa-circle-check"></i>
            ${closedCount} Closed
        </a>
    </div>

    <div class="issue-list">
        <c:choose>
            <c:when test="${not empty issues}">
                <c:forEach var="issue" items="${issues}">
                    <div class="issue-item">
                        <div class="issue-icon ${issue.status == 'OPEN' ? 'open' : 'closed'}">
                            <i class="far ${issue.status == 'OPEN' ? 'fa-circle-dot' : 'fa-circle-check'}"></i>
                        </div>
                        <div class="issue-content">
                            <a href="${pageContext.request.contextPath}/issue/detail?id=${issue.id}&owner=${param.owner}&repo=${param.repo}"
                               class="issue-title">
                                    ${issue.title}
                            </a>
                            <div class="issue-meta">
                                <span class="issue-number">#${issue.id}</span>
                                opened
                                <fmt:formatDate value="${issue.createdAt}" pattern="MMM d, yyyy"/>
                                by ${issue.username}
                                <c:if test="${issue.commentCount > 0}">
                                    · <i class="far fa-comment"></i> ${issue.commentCount}
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
        </c:choose>
    </div>
</div>



</body>
</html>
