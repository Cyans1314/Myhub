<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Header component for dashboard page
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/Primer/19.1.0/primer.min.css" rel="stylesheet">
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" />
</head>
<body>

<header class="gh-header">
    <div style="display: flex; align-items: center; flex: 1;">
        <a href="${pageContext.request.contextPath}/index" class="header-logo">
            <i class="fab fa-github fa-2x"></i>
        </a>
        <span class="mr-3 text-bold" style="color:#24292f; font-size:14px;">Dashboard</span>

        <div class="header-search-wrapper">
            <form action="${pageContext.request.contextPath}/search" method="get" style="width: 100%;">
                <input type="text" name="q" class="header-search-input" placeholder="Type / to search" />
            </form>
            <span class="header-slash">/</span>
        </div>
    </div>

    <div class="header-right">
        <!-- Plus button -->
        <details class="details-overlay mr-3" style="position: relative;">
            <summary class="header-icon-btn" title="Create new...">
                <i class="fas fa-plus"></i> <i class="fas fa-caret-down"></i>
            </summary>
            <div class="dropdown-menu" style="width: 160px; right: 0;">
                <a href="${pageContext.request.contextPath}/repo/create" class="dropdown-item">New repository</a>
            </div>
        </details>

        <!-- Avatar + dropdown menu -->
        <details class="details-overlay" style="position: relative;">
            <summary class="header-avatar-btn">
                <img src="${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'https://github.com/identicons/user.png'}"
                     class="header-avatar">
                <span class="header-caret"></span>
            </summary>

            <div class="dropdown-menu">
                <div class="dropdown-header">
                    Signed in as <br>
                    <strong style="color: #24292f;">${sessionScope.user.username}</strong>
                </div>

                <div class="dropdown-divider"></div>

                <a href="${pageContext.request.contextPath}/user/profile" class="dropdown-item">Your profile</a>

                <div class="dropdown-divider"></div>

                <!-- Keep only sign out -->
                <a href="${pageContext.request.contextPath}/user/logout" class="dropdown-item">Sign out</a>
            </div>
        </details>
    </div>
</header>