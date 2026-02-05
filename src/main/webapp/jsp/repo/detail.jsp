<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Repository detail page showing files, README, and repository information
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.example.myhub.dao.RepoDao" %>
<%@ page import="com.example.myhub.dao.UserDao" %>
<%@ page import="com.example.myhub.bean.Repo" %>
<%@ page import="com.example.myhub.bean.User" %>
<%@ page import="com.example.myhub.service.GitService" %>
<%@ page import="com.example.myhub.service.FileService" %>
<%@ page import="com.example.myhub.dto.FileNode" %>
<%@ page import="com.example.myhub.dto.CommitInfo" %>
<%@ page import="com.example.myhub.util.MarkdownUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.File" %>

<!DOCTYPE html>
<html>
<head>
    <title>${currentOwner}/${currentRepo}</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/repo-detail.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" />
</head>
<body>

<!-- Header -->
<jsp:include page="../common/header_detail.jsp" />

<%
    // Get parameters
    String ownerName = request.getParameter("owner");
    String repoName = request.getParameter("repo");
    String branch = request.getParameter("branch");
    if (branch == null) branch = "main";

    // Attributes passed from RepoServlet
    Repo currentRepo = (Repo) request.getAttribute("repo");
    User repoOwner = (User) request.getAttribute("owner");
    List<FileNode> files = (List<FileNode>) request.getAttribute("files");
    String readmeContent = "";
    CommitInfo latestCommit = null;

    // Ensure ownerName and repoName have values and are strings
    if (ownerName == null && repoOwner != null) {
        ownerName = repoOwner.getUsername();
    }
    if (repoName == null && currentRepo != null) {
        repoName = currentRepo.getName();
    }

    // Debug information
    System.out.println("Detail.jsp Debug - ownerName: " + ownerName);
    System.out.println("Detail.jsp Debug - repoName: " + repoName);
    System.out.println("Detail.jsp Debug - currentRepo: " + currentRepo);
    System.out.println("Detail.jsp Debug - repoOwner: " + repoOwner);

    // Set as request attributes for EL expressions
    request.setAttribute("currentOwner", ownerName);
    request.setAttribute("currentRepo", repoName);

    // If RepoServlet did not set, manually fetch
    if (currentRepo == null && ownerName != null && repoName != null) {
        UserDao userDao = new UserDao();
        RepoDao repoDao = new RepoDao();
        GitService gitService = new GitService();

        repoOwner = userDao.findByUsername(ownerName);
        if (repoOwner != null) {
            currentRepo = repoDao.findByOwnerAndName(repoOwner.getId(), repoName);

            // Get real file list
            files = gitService.listFiles(ownerName, repoName, branch, "");

            // Get latest commit information
            List<CommitInfo> commits = gitService.getCommitHistory(ownerName, repoName, branch, 1);
            if (commits != null && !commits.isEmpty()) {
                latestCommit = commits.get(0);
            }

            // Get README content
            String readmeRaw = gitService.getFileContent(ownerName, repoName, branch, "README.md");
            if (readmeRaw != null && !readmeRaw.isEmpty()) {
                readmeContent = MarkdownUtil.markdownToHtml(readmeRaw);
            }
        }
    } else if (ownerName != null && repoName != null) {
        // Have repo, supplement file list and README
        GitService gitService = new GitService();

        try {
            if (files == null) {
                files = gitService.listFiles(ownerName, repoName, branch, "");
            }

            // Get latest commit information
            if (latestCommit == null) {
                List<CommitInfo> commits = gitService.getCommitHistory(ownerName, repoName, branch, 1);
                if (commits != null && !commits.isEmpty()) {
                    latestCommit = commits.get(0);
                }
            }

            // Get README content
            String readmeRaw = gitService.getFileContent(ownerName, repoName, branch, "README.md");
            if (readmeRaw != null && !readmeRaw.isEmpty()) {
                readmeContent = MarkdownUtil.markdownToHtml(readmeRaw);
            }
        } catch (Exception e) {
            System.out.println("Error loading repo data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    request.setAttribute("currentRepoObj", currentRepo);
    request.setAttribute("repoOwner", repoOwner);
    request.setAttribute("files", files);
    request.setAttribute("readmeContent", readmeContent);
    System.out.println("Setting readmeContent attribute, length: " + (readmeContent != null ? readmeContent.length() : 0));
    request.setAttribute("latestCommit", latestCommit);
    request.setAttribute("branch", branch);

    // Debug information
    System.out.println("Debug - ownerName: " + ownerName);
    System.out.println("Debug - repoName: " + repoName);
    System.out.println("Debug - repoOwner: " + (repoOwner != null ? repoOwner.getUsername() : "null"));
    System.out.println("Debug - currentRepo: " + (currentRepo != null ? currentRepo.getName() : "null"));
%>

<div class="repo-container">
    <!-- Repository title bar -->
    <div class="repo-title-bar">
        <div class="repo-title-left">
            <!-- Real user avatar -->
            <img src="https://github.com/identicons/${currentOwner}.png"
                 class="repo-avatar" alt="${currentOwner}">
            <!-- Real project name -->
            <h1 class="repo-name">${currentRepo}</h1>
            <!-- Real visibility label -->
            <span class="visibility-label">Public</span>
        </div>
        <div class="repo-title-right">
            <!-- Fake button data -->
            <button class="repo-action-btn">
                <i class="fas fa-thumbtack"></i> Pin
            </button>
            <button class="repo-action-btn">
                <i class="far fa-eye"></i> Watch <strong>0</strong>
            </button>
            <button class="repo-action-btn">
                <i class="fas fa-code-branch"></i> Fork <strong>0</strong>
            </button>
            <button class="repo-action-btn starred">
                <i class="fas fa-star"></i> Starred <strong>1</strong>
            </button>
        </div>
    </div>

    <div class="repo-content">
        <!-- Left side -->
        <div class="repo-main">
            <!-- First row: main button + Code button -->
            <div class="branch-bar">
                <div class="branch-left">
                    <button class="branch-btn">
                        <i class="fas fa-code-branch"></i>
                        ${branch}
                        <i class="fas fa-caret-down"></i>
                    </button>
                    <div class="branch-info">
                        <span>1 Branch</span>
                        <span>0 Tags</span>
                    </div>
                </div>
                <!-- Code button on the right -->
                <div class="code-dropdown">
                    <button class="code-btn" onclick="toggleCodeDropdown()">
                        <i class="fas fa-download"></i>
                        Code
                        <i class="fas fa-caret-down"></i>
                    </button>
                    <div class="code-dropdown-menu" id="codeDropdownMenu">
                        <div class="dropdown-section">
                            <div class="dropdown-header">
                                <i class="fas fa-terminal"></i>
                                Clone
                            </div>
                            <div class="clone-tabs">
                                <span class="clone-tab active">HTTPS</span>
                            </div>
                            <div class="clone-url-container">
                                <input type="text" class="clone-url" id="cloneUrl"
                                       value="http://localhost:8080/git/${currentOwner}/${currentRepo}.git" readonly>
                                <button class="copy-btn" onclick="copyCloneUrl()">
                                    <i class="fas fa-copy"></i>
                                </button>
                            </div>
                            <p class="clone-description">Clone using the web URL.</p>
                        </div>
                        <div class="dropdown-divider"></div>
                        <div class="dropdown-section">
                            <button class="download-zip-btn" onclick="downloadZip()">
                                <i class="fas fa-file-archive"></i>
                                Download ZIP
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Second row: file list -->
            <div class="file-list-container">
                <!-- File list header - display latest commit information -->
                <div class="file-list-header">
                    <div class="commit-info">
                        <img src="${repoOwner != null && repoOwner.avatar != null ? repoOwner.avatar : 'https://github.com/identicons/'.concat(currentOwner).concat('.png')}"
                             class="commit-avatar" alt="${currentOwner}">
                        <span class="commit-author">${currentOwner}</span>
                        <span class="commit-message">
                            <c:choose>
                                <c:when test="${latestCommit != null}">
                                    ${latestCommit.message}
                                </c:when>
                                <c:otherwise>
                                    Initial commit
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="commit-meta">
                        <span class="commit-hash">
                            <c:choose>
                                <c:when test="${latestCommit != null}">
                                    ${latestCommit.shortHash}
                                </c:when>
                                <c:otherwise>
                                    a1b2c3d
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <span>
                            <c:choose>
                                <c:when test="${latestCommit != null}">
                                    ${latestCommit.time}
                                </c:when>
                                <c:otherwise>
                                    2 hours ago
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <strong style="margin-left: 16px; color: #1f2328;">12 commits</strong>
                    </div>
                </div>

                <!-- File table -->
                <table class="files-table">
                    <c:choose>
                        <c:when test="${not empty files}">
                            <c:forEach var="file" items="${files}">
                                <tr class="file-row">
                                    <td class="file-icon">
                                        <c:choose>
                                            <c:when test="${file.type == 'directory'}">
                                                <i class="fas fa-folder" style="color: #54aeff;"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="far fa-file" style="color: #656d76;"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="width: 40%;">
                                        <c:choose>
                                            <c:when test="${file.type == 'file'}">
                                                <a href="${pageContext.request.contextPath}/jsp/repo/code.jsp?owner=${currentOwner}&repo=${currentRepo}&file=${file.name}&branch=${branch}"
                                                   class="file-name">${file.name}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="file-name">${file.name}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="file-message">
                                        <c:choose>
                                            <c:when test="${not empty file.lastCommit}">
                                                ${file.lastCommit}
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${file.name == 'README.md'}">Update README</c:when>
                                                    <c:otherwise>Add files</c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="file-age">
                                        <c:choose>
                                            <c:when test="${not empty file.lastCommitTime}">
                                                ${file.lastCommitTime}
                                            </c:when>
                                            <c:otherwise>
                                                3 days ago
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <!-- Empty repository prompt -->
                            <tr>
                                <td colspan="4" style="padding: 40px; text-align: center; color: #656d76;">
                                    <h3>This is an empty repository</h3>
                                    <p>Get started by creating a new file or uploading an existing file.</p>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </table>
            </div>

            <!-- README section -->
            <c:if test="${not empty readmeContent}">
                <div class="readme-container">
                    <div class="readme-header">
                        <i class="fas fa-book"></i>
                        README.md
                    </div>
                    <div class="readme-body">
                        <div class="markdown-body">
                                ${readmeContent}
                        </div>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Sidebar -->
        <div class="repo-sidebar">
            <!-- About -->
            <div class="sidebar-section">
                <div class="sidebar-heading">
                    About
                    <i class="fas fa-cog" style="color: #656d76; cursor: pointer;"></i>
                </div>

                <div style="margin-bottom: 16px;">
                    <c:choose>
                        <c:when test="${not empty currentRepoObj.description}">
                            ${currentRepoObj.description}
                        </c:when>
                        <c:otherwise>
                            <span style="color: #656d76; font-style: italic;">No description provided.</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="sidebar-item">
                    <i class="fas fa-link"></i>
                    <a href="#" style="color: #0969da; font-weight: 600;">github.com/topics/rendering</a>
                </div>

                <div style="margin: 12px 0;">
                    <a href="#" class="topic-tag">c++</a>
                    <a href="#" class="topic-tag">python</a>
                    <a href="#" class="topic-tag">networking</a>
                </div>

                <div class="sidebar-item">
                    <i class="far fa-file-alt"></i>
                    Readme
                </div>
                <div class="sidebar-item">
                    <i class="fas fa-balance-scale"></i>
                    MIT license
                </div>
                <div class="sidebar-item">
                    <i class="fas fa-chart-line"></i>
                    Activity
                </div>
                <div class="sidebar-item">
                    <i class="far fa-eye"></i>
                    <strong>0</strong> watching
                </div>
                <div class="sidebar-item">
                    <i class="far fa-star"></i>
                    <strong>1</strong> stars
                </div>
                <div class="sidebar-item">
                    <i class="fas fa-code-branch"></i>
                    <strong>0</strong> forks
                </div>
            </div>

            <!-- Languages -->
            <div class="sidebar-section">
                <div class="sidebar-heading">Languages</div>

                <div class="language-bar">
                    <div style="width: 65.4%; background-color: #f34b7d;"></div>
                    <div style="width: 34.6%; background-color: #00599c;"></div>
                </div>

                <div class="language-stats">
                    <div class="language-item">
                        <span class="language-dot" style="color: #f34b7d;">●</span>
                        <strong>C++</strong> 65.4%
                    </div>
                    <div class="language-item">
                        <span class="language-dot" style="color: #00599c;">●</span>
                        <strong>Python</strong> 34.6%
                    </div>
                </div>
            </div>

            <!-- Releases -->
            <div class="sidebar-section">
                <div class="sidebar-heading">Releases</div>
                <div style="color: #656d76; font-size: 12px;">No releases published</div>
                <div style="margin-top: 8px;">
                    <a href="#" style="color: #0969da; font-weight: 600;">Create a new release</a>
                </div>
            </div>

            <!-- Suggested workflows -->
            <div class="sidebar-section">
                <div class="sidebar-heading">Suggested workflows</div>
                <div style="color: #656d76; font-size: 12px; margin-bottom: 16px;">Based on your tech stack</div>

                <div class="workflow-card">
                    <div class="workflow-info">
                        <div class="workflow-icon">
                            <i class="fab fa-python" style="color: #3776ab;"></i>
                        </div>
                        <div class="workflow-details">
                            <h4>Python Package</h4>
                            <p>Create and test a Python package</p>
                        </div>
                    </div>
                    <button class="workflow-btn">Configure</button>
                </div>

                <div class="workflow-card">
                    <div class="workflow-info">
                        <div class="workflow-icon">
                            <i class="fas fa-cogs" style="color: #00599c;"></i>
                        </div>
                        <div class="workflow-details">
                            <h4>C++ CMake</h4>
                            <p>Build and test C++ projects</p>
                        </div>
                    </div>
                    <button class="workflow-btn">Configure</button>
                </div>

                <div class="workflow-card">
                    <div class="workflow-info">
                        <div class="workflow-icon">
                            <i class="fas fa-shield-alt" style="color: #d94a38;"></i>
                        </div>
                        <div class="workflow-details">
                            <h4>CodeQL Analysis</h4>
                            <p>Security analysis for C++ and Python</p>
                        </div>
                    </div>
                    <button class="workflow-btn">Configure</button>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Code按钮下拉菜单功能
    function toggleCodeDropdown() {
        const dropdown = document.getElementById('codeDropdownMenu');
        dropdown.classList.toggle('show');
    }

    // 点击其他地方关闭下拉菜单
    document.addEventListener('click', function(e) {
        const dropdown = document.getElementById('codeDropdownMenu');
        const codeBtn = document.querySelector('.code-btn');

        if (!dropdown.contains(e.target) && !codeBtn.contains(e.target)) {
            dropdown.classList.remove('show');
        }
    });

    // 复制clone URL
    function copyCloneUrl() {
        const cloneUrl = document.getElementById('cloneUrl');
        cloneUrl.select();
        cloneUrl.setSelectionRange(0, 99999);

        try {
            document.execCommand('copy');
            // 显示复制成功提示
            const copyBtn = document.querySelector('.copy-btn');
            const originalIcon = copyBtn.innerHTML;
            copyBtn.innerHTML = '<i class="fas fa-check"></i>';
            copyBtn.style.color = '#28a745';

            setTimeout(() => {
                copyBtn.innerHTML = originalIcon;
                copyBtn.style.color = '';
            }, 2000);
        } catch (err) {
            alert('复制失败，请手动复制');
        }
    }

    // 下载ZIP - 简单实现
    function downloadZip() {
        const owner = '${currentOwner}';
        const repo = '${currentRepo}';

        // 直接跳转到下载URL
        window.location.href = '${pageContext.request.contextPath}/download/' + owner + '/' + repo + '/archive/main.zip';

        // 关闭下拉菜单
        document.getElementById('codeDropdownMenu').classList.remove('show');
    }
</script>

</body>
</html>