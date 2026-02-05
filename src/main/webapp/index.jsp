<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Dashboard home page showing trending repositories and user repositories
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="jsp/common/header.jsp" %>

<div class="container-xl mt-4 dashboard-layout">

    <!-- Left Sidebar -->
    <div class="dashboard-sidebar">
        <div class="d-flex justify-between items-center mb-2">
            <span class="text-bold" style="font-size: 16px;">Top repositories</span>
            <a href="${pageContext.request.contextPath}/repo/create" class="btn-new">
                <i class="fas fa-book-bookmark"></i> New
            </a>
        </div>

        <input type="text" class="repo-filter-input" placeholder="Find a repository..." style="font-size: 14px;">

        <ul class="repo-list">
            <c:choose>
                <c:when test="${not empty userRepos}">
                    <c:forEach items="${userRepos}" var="repo">
                        <li class="repo-list-item" style="font-size: 14px;"></li>
                        <img src="${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'https://github.com/identicons/user.png'}" width="16" height="16" style="border-radius: 50%;">
                        <a href="${pageContext.request.contextPath}/repo/${sessionScope.user.username}/${repo.name}" class="repo-name">
                                ${sessionScope.user.username}/${repo.name}
                        </a>
                        </li>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <li class="color-fg-muted mt-3" style="font-size: 14px;">No repositories found.</li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>

    <!-- Right Content Area -->
    <div class="dashboard-content">
        <div class="feed-main">

            <div class="d-flex justify-between items-center mb-3">
                <h2 class="feed-title" style="font-size: 26px;">Home</h2>
            </div>

            <!-- AI Chat Box -->
            <div style="margin-bottom: 20px;">
                <div style="border: 1px solid #d0d7de; border-radius: 8px; padding: 12px 16px; background: #fff; cursor: pointer;"
                     onclick="window.open('https://chat.deepseek.com/', '_blank')">
                    <div style="display: flex; align-items: center; gap: 10px; color: #57606a;">
                        <i class="fas fa-robot" style="font-size: 18px;"></i>
                        <span style="font-size: 15px;">Ask anything...</span>
                        <span style="margin-left: auto; font-size: 12px; color: #8c959f;">Powered by DeepSeek</span>
                    </div>
                </div>
            </div>

            <div class="mb-2">
                <span class="text-bold" style="font-size: 18px;">Trending repositories</span>
                <span class="color-fg-muted ml-2" style="font-size: 15px;">See what the community is most excited about today.</span>
            </div>

            <div class="gh-box">
                <c:choose>
                    <c:when test="${not empty publicRepos}">
                        <c:forEach items="${publicRepos}" var="repo" varStatus="status">
                            <div class="gh-box-row repo-item" data-index="${status.index}" style="${status.index >= 4 ? 'display:none;' : ''}">
                                <div style="flex:1;">
                                    <div class="d-flex items-center mb-1">
                                        <c:set var="repoOwner" value="${repo.ownerName != null ? repo.ownerName : repo.ownerId}" />

                                        <a href="${pageContext.request.contextPath}/repo/${repoOwner}/${repo.name}" style="color:#0969da; font-size: 22px; font-weight: 600; text-decoration: none;">
                                                ${repoOwner} / <strong>${repo.name}</strong>
                                        </a>

                                        <span class="Label Label--secondary ml-2" style="font-weight: normal; font-size: 13px; border:1px solid #d0d7de; border-radius:10px; padding:0 7px; color:#57606a;">Public</span>
                                    </div>
                                    <p class="color-fg-muted mb-2" style="font-size: 16px;">
                                            ${repo.description != null ? repo.description : 'No description provided.'}
                                    </p>
                                    <div class="d-flex items-center color-fg-muted" style="font-size: 15px;">
                                        <c:choose>
                                            <c:when test="${repo.id % 2 == 0}">
                                                <span class="mr-3"><i class="fas fa-circle" style="color: #3572A5; font-size: 10px;"></i> Python</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="mr-3"><i class="fas fa-circle" style="color: #f34b7d; font-size: 10px;"></i> C++</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="mr-3"><i class="far fa-star"></i> ${repo.starCount}</span>
                                        <span>Updated recently</span>
                                    </div>
                                </div>
                                <div>
                                    <button class="btn-star" data-repo-id="${repo.id}" onclick="toggleStar(this, ${repo.id})">
                                        <i class="far fa-star"></i> Star
                                    </button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div style="padding: 32px; text-align: center; color: #57606a;">
                            No trending repositories found.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div style="text-align: center; margin-top: 16px;" id="pagination">
                <!-- Pagination generated by JavaScript -->
            </div>

        </div>
    </div>
</div>

<style>
    .page-btn {
        min-width: 32px;
        height: 32px;
        padding: 0 10px;
        border: 1px solid #d0d7de;
        border-radius: 6px;
        background: #fff;
        color: #0969da;
        font-size: 14px;
        cursor: pointer;
        margin: 0 4px;
    }
    .page-btn:hover {
        background: #f6f8fa;
    }
    .page-btn.active {
        background: #0969da;
        color: #fff;
        border-color: #0969da;
    }
    .page-btn:disabled {
        color: #8c959f;
        cursor: not-allowed;
        background: #f6f8fa;
    }
</style>

<script>
    var pageSize = 4;
    var currentPage = 1;
    var items = document.querySelectorAll('.repo-item');
    var totalItems = items.length;
    var totalPages = Math.ceil(totalItems / pageSize);

    function showPage(page) {
        currentPage = page;
        var start = (page - 1) * pageSize;
        var end = start + pageSize;

        for (var i = 0; i < items.length; i++) {
            if (i >= start && i < end) {
                items[i].style.display = '';
            } else {
                items[i].style.display = 'none';
            }
        }

        renderPagination();
    }

    function renderPagination() {
        var container = document.getElementById('pagination');
        if (totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        var html = '';
        html += '<button class="page-btn" onclick="showPage(' + (currentPage - 1) + ')" ' + (currentPage === 1 ? 'disabled' : '') + '>&lt; Previous</button>';

        for (var i = 1; i <= totalPages; i++) {
            html += '<button class="page-btn ' + (i === currentPage ? 'active' : '') + '" onclick="showPage(' + i + ')">' + i + '</button>';
        }

        html += '<button class="page-btn" onclick="showPage(' + (currentPage + 1) + ')" ' + (currentPage === totalPages ? 'disabled' : '') + '>Next &gt;</button>';

        container.innerHTML = html;
    }

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
            .then(function(response) { return response.json(); })
            .then(function(data) {
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

    // Initialize
    showPage(1);
</script>

</body>
</html>
