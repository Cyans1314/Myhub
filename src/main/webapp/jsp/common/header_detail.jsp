<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Repository detail header component with navigation tabs
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Define variables: prioritize URL parameters, fallback to Servlet objects -->
<c:set var="currentOwner" value="${not empty param.owner ? param.owner : owner.username}" />
<c:set var="currentRepo" value="${not empty param.repo ? param.repo : repo.name}" />
<c:set var="currentTab" value="${not empty param.tab ? param.tab : 'code'}" />

<div class="repo-header-wrapper">

    <!-- First row: Top Bar -->
    <div class="gh-top-bar" style="padding: 12px 24px; display: flex; align-items: center; justify-content: space-between;">

        <!-- Left side: Menu + Logo + Breadcrumb -->
        <div class="d-flex items-center">

            <a href="${pageContext.request.contextPath}/index" class="mr-3" style="color: #24292f; display: flex; align-items: center;">
                <svg height="32" viewBox="0 0 16 16" version="1.1" width="32" aria-hidden="true" fill="currentColor">
                    <path fill-rule="evenodd" d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"></path>
                </svg>
            </a>

            <div class="repo-breadcrumb-simple" style="display: flex; align-items: center; font-size: 14px;">
                <a href="${pageContext.request.contextPath}/user/profile?name=${currentOwner}" style="color: #0969da; text-decoration: none;">
                    ${currentOwner}
                </a>
                <span style="margin: 0 8px; color: #57606a;">/</span>
                <a href="${pageContext.request.contextPath}/repo/${currentOwner}/${currentRepo}" style="color: #0969da; font-weight: 600; text-decoration: none;">
                    ${currentRepo}
                </a>
                <span class="Label Label--secondary ml-2" style="font-size: 12px; border:1px solid #d0d7de; border-radius:10px; padding:0 8px; color: #57606a; margin-left: 8px;">Public</span>
            </div>

        </div>

        <!-- Right side (unchanged) -->
        <div class="d-flex items-center" style="gap:16px;">
            <div class="header-search-wrapper">
                <input type="text" placeholder="Type / to search" class="header-search-input">
                <span style="color:#57606a; font-size:10px; border:1px solid #d0d7de; border-radius:4px; padding:0 4px;">/</span>
            </div>

            <!-- Avatar -->
            <details style="position: relative;">
                <summary style="cursor: pointer; display: flex; align-items: center; list-style: none;">
                    <img src="${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'https://github.com/identicons/user.png'}"
                         class="header-avatar" style="border:1px solid #d0d7de;">
                    <span class="dropdown-caret" style="border-top-color: #24292f;"></span>
                </summary>
                <div class="dropdown-menu" style="right:0;">
                    <a href="${pageContext.request.contextPath}/user/profile" class="dropdown-item">Your profile</a>
                    <div class="dropdown-divider"></div>
                    <a href="${pageContext.request.contextPath}/user/logout" class="dropdown-item">Sign out</a>
                </div>
            </details>
        </div>
    </div>

    <!-- Second row: Tab navigation -->
    <div class="repo-nav-bg" style="background-color: #f6f8fa; border-bottom: 1px solid #d0d7de; padding-top: 8px;">
        <div class="container-xl" style="display: block !important;">
            <div class="repo-nav-wrapper" style="overflow-x: auto;">
                <nav class="repo-nav" style="display: flex; gap: 24px;">

                    <!-- Code Tab -->
                    <!-- Fix: Use parameters ${currentOwner} and ${currentRepo} -->
                    <a href="${pageContext.request.contextPath}/jsp/repo/detail.jsp?owner=${currentOwner}&repo=${currentRepo}&tab=code"
                       class="repo-nav-item ${currentTab == 'code' ? 'selected' : ''}"
                       style="padding-bottom: 10px; border-bottom: 2px solid ${currentTab == 'code' ? '#fd8c73' : 'transparent'}; font-weight: ${currentTab == 'code' ? '600' : '400'}; color: #24292f; display: flex; align-items: center; gap: 6px; text-decoration: none;">
                        <svg height="16" viewBox="0 0 16 16" version="1.1" width="16" aria-hidden="true" fill="currentColor" style="color: #57606a;"><path fill-rule="evenodd" d="M4.72 3.22a.75.75 0 011.06 1.06L2.06 8l3.72 3.72a.75.75 0 11-1.06 1.06L.47 8.53a.75.75 0 010-1.06l4.25-4.25zm6.56 0a.75.75 0 10-1.06 1.06L13.94 8l-3.72 3.72a.75.75 0 101.06 1.06l4.25-4.25a.75.75 0 000-1.06l-4.25-4.25z"></path></svg>
                        Code
                    </a>

                    <!-- Issues Tab -->
                    <a href="${pageContext.request.contextPath}/jsp/repo/issues.jsp?owner=${currentOwner}&repo=${currentRepo}&tab=issues"
                       class="repo-nav-item ${currentTab == 'issues' ? 'selected' : ''}"
                       style="padding-bottom: 10px; border-bottom: 2px solid ${currentTab == 'issues' ? '#fd8c73' : 'transparent'}; font-weight: ${currentTab == 'issues' ? '600' : '400'}; color: #24292f; display: flex; align-items: center; gap: 6px; text-decoration: none;">
                        <svg height="16" viewBox="0 0 16 16" version="1.1" width="16" aria-hidden="true" fill="currentColor" style="color: #57606a;"><path d="M8 9.5a1.5 1.5 0 100-3 1.5 1.5 0 000 3z"></path><path fill-rule="evenodd" d="M8 0a8 8 0 100 16A8 8 0 008 0zM1.5 8a6.5 6.5 0 1113 0 6.5 6.5 0 01-13 0z"></path></svg>
                        Issues
                    </a>

                    <!-- Settings Tab -->
                    <!-- Only show when current logged-in user is repository owner -->
                    <c:if test="${sessionScope.user.username == currentOwner}">
                        <a href="${pageContext.request.contextPath}/jsp/repo/settings.jsp?owner=${currentOwner}&repo=${currentRepo}&tab=settings"
                           class="repo-nav-item ${currentTab == 'settings' ? 'selected' : ''}"
                           style="padding-bottom: 10px; border-bottom: 2px solid ${currentTab == 'settings' ? '#fd8c73' : 'transparent'}; font-weight: ${currentTab == 'settings' ? '600' : '400'}; color: #24292f; display: flex; align-items: center; gap: 6px; text-decoration: none;">
                            <svg height="16" viewBox="0 0 16 16" version="1.1" width="16" aria-hidden="true" fill="currentColor" style="color: #57606a;"><path fill-rule="evenodd" d="M7.429 1.525a6.593 6.593 0 011.142 0c.036.003.108.036.137.146l.253 1.17a2.25 2.25 0 001.388 1.58l1.168.41a.22.22 0 01.109.117l.79 1.054c.486.649.486 1.548 0 2.196l-.79 1.054a.22.22 0 01-.11.117l-1.168.41a2.25 2.25 0 00-1.388 1.58l-.253 1.17c-.029.11-.101.143-.137.146a6.623 6.623 0 01-1.142 0c-.036-.003-.108-.036-.137-.146l-.253-1.17a2.25 2.25 0 00-1.388-1.58l-1.168-.41a.22.22 0 01-.109-.117l-.79-1.054a1.75 1.75 0 010-2.196l.79-1.054a.22.22 0 01.11-.117l1.168-.41a2.25 2.25 0 001.388-1.58l.253-1.17c.029-.11.101-.143.137-.146zM8 8.25a2.25 2.25 0 100-4.5 2.25 2.25 0 000 4.5zM12.75 11a.75.75 0 100-1.5.75.75 0 000 1.5z"></path></svg>
                            Settings
                        </a>
                    </c:if>

                </nav>
            </div>
        </div>
    </div>
</div>