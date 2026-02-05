<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Search results page for repositories
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="jsp/common/header.jsp" %>
<link href="${pageContext.request.contextPath}/css/search.css" rel="stylesheet" />

<div class="search-page">
    <!-- Left filter sidebar -->
    <div class="search-sidebar">
        <h3 class="search-sidebar-title">Filter by</h3>
        <ul class="search-filter-list">
            <li class="search-filter-item active">
                <i class="fas fa-book"></i>
                <span>Repositories</span>
                <span class="filter-count">${resultCount}</span>
            </li>
            <li class="search-filter-item">
                <i class="fas fa-code"></i>
                <span>Code</span>
            </li>
            <li class="search-filter-item">
                <i class="fas fa-circle-dot"></i>
                <span>Issues</span>
            </li>
            <li class="search-filter-item">
                <i class="fas fa-users"></i>
                <span>Users</span>
            </li>
        </ul>
    </div>

    <!-- Right results area -->
    <div class="search-results">
        <div class="search-results-header">
            <h2 class="search-results-count">${resultCount} repository results</h2>
        </div>

        <c:choose>
            <c:when test="${not empty results}">
                <c:forEach items="${results}" var="repo">
                    <div class="search-result-card">
                        <div class="result-card-header">
                            <i class="fas fa-book result-repo-icon"></i>
                            <a href="${pageContext.request.contextPath}/repo/${repo.ownerName}/${repo.name}" class="result-repo-link">
                                    ${repo.ownerName} / <strong>${repo.name}</strong>
                            </a>
                            <span class="result-visibility">Public</span>
                        </div>
                        <p class="result-card-desc">
                                ${repo.description != null && repo.description != '' ? repo.description : 'No description, website, or topics provided.'}
                        </p>
                        <div class="result-card-meta">
                            <c:choose>
                                <c:when test="${repo.id % 2 == 0}">
                                    <span class="meta-lang"><span class="lang-dot" style="background-color: #3572A5;"></span> Python</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="meta-lang"><span class="lang-dot" style="background-color: #f34b7d;"></span> C++</span>
                                </c:otherwise>
                            </c:choose>
                            <span class="meta-star"><i class="far fa-star"></i> ${repo.starCount}</span>
                            <span class="meta-updated">Updated <fmt:formatDate value="${repo.updatedAt}" pattern="MMM d, yyyy"/></span>
                        </div>
                        <button class="btn-star-card" onclick="toggleStar(this, ${repo.id})">
                            <i class="far fa-star"></i> Star
                        </button>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="search-empty">
                    <i class="fas fa-search" style="font-size: 48px; color: #d0d7de; margin-bottom: 16px;"></i>
                    <h3>We couldn't find any repositories matching '${query}'</h3>
                    <p>Try different keywords or check your spelling.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    function toggleStar(btn, repoId) {
        var icon = btn.querySelector('i');
        var isStarred = icon.classList.contains('fas');
        var action = isStarred ? 'unstar' : 'star';

        fetch('${pageContext.request.contextPath}/star', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'repoId=' + repoId + '&action=' + action
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    if (data.starred) {
                        btn.innerHTML = '<i class="fas fa-star" style="color:#e3b341;"></i> Starred';
                    } else {
                        btn.innerHTML = '<i class="far fa-star"></i> Star';
                    }
                } else {
                    alert(data.message || 'Operation failed');
                }
            });
    }
</script>

</body>
</html>
