<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: User profile header component with navigation
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${targetUser.username} · MyHub</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/Primer/19.1.0/primer.min.css" rel="stylesheet">
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" />
</head>
<body>

<div class="gh-header-wrapper">
    <div class="gh-top-bar">
        <div class="d-flex items-center">
            <a href="${pageContext.request.contextPath}/index" class="mr-3" style="color: #24292f;">
                <i class="fab fa-github fa-2x"></i>
            </a>
            <div class="header-search-wrapper">
                <input type="text" placeholder="Type / to search" class="header-search-input">
                <span style="color:#57606a; font-size:10px; border:1px solid #d0d7de; border-radius:4px; padding:0 4px;">/</span>
            </div>
        </div>

        <div class="d-flex items-center" style="gap:16px;">
            <details style="position: relative;">
                <summary style="cursor: pointer; color: #24292f;"><i class="fas fa-plus"></i> <i class="fas fa-caret-down"></i></summary>
                <div class="dropdown-menu">
                    <a href="${pageContext.request.contextPath}/repo/create" class="dropdown-item">New repository</a>
                </div>
            </details>
            <details style="position: relative;">
                <summary class="header-avatar-btn">
                    <img src="${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'https://github.com/identicons/user.png'}" class="header-avatar">
                    <i class="fas fa-caret-down ml-1" style="font-size: 10px; color: #24292f; margin-left: 4px;"></i>
                </summary>
                <div class="dropdown-menu">
                    <div style="padding: 8px 16px; border-bottom: 1px solid #d0d7de; font-size: 12px;">
                        Signed in as <strong>${sessionScope.user.username}</strong>
                    </div>
                    <a href="${pageContext.request.contextPath}/user/profile" class="dropdown-item">Your profile</a>
                    <div class="dropdown-divider"></div>
                    <a href="${pageContext.request.contextPath}/user/logout" class="dropdown-item">Sign out</a>
                </div>
            </details>
        </div>
    </div>

    <!-- Navigation bar -->
    <div class="gh-nav-bar">
        <div class="nav-spacer"></div>
        <a href="?name=${targetUser.username}&tab=overview" class="nav-item ${tab == 'overview' ? 'selected' : ''}">
            <i class="fas fa-book-open"></i> Overview
        </a>
        <a href="?name=${targetUser.username}&tab=repositories" class="nav-item ${tab == 'repositories' ? 'selected' : ''}">
            <i class="fas fa-book"></i> Repositories
        </a>
        <a href="?name=${targetUser.username}&tab=stars" class="nav-item ${tab == 'stars' ? 'selected' : ''}">
            <i class="far fa-star"></i> Stars
        </a>
    </div>
</div>