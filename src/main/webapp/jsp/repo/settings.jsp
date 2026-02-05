<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Repository settings page for managing repository configuration
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.example.myhub.dao.*" %>
<%@ page import="com.example.myhub.bean.*" %>

<%
    String ownerName = request.getParameter("owner");
    String repoName = request.getParameter("repo");

    UserDao userDao = new UserDao();
    RepoDao repoDao = new RepoDao();

    User owner = userDao.findByUsername(ownerName);
    Repo repo = null;

    if (owner != null) {
        repo = repoDao.findByOwnerAndName(owner.getId(), repoName);
    }

    request.setAttribute("owner", owner);
    request.setAttribute("repo", repo);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Settings · ${param.owner}/${param.repo}</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/repo-detail.css" rel="stylesheet" />
    <style>
        .settings-wrapper {
            display: flex;
            max-width: 1200px;
            margin: 0 auto;
            padding: 32px 40px;
            gap: 48px;
        }

        .settings-sidebar {
            width: 200px;
            flex-shrink: 0;
        }

        .settings-nav-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 14px;
            font-size: 15px;
            color: #1f2328;
            text-decoration: none;
            border-radius: 6px;
            background-color: #f6f8fa;
            font-weight: 600;
        }

        .settings-nav-item i {
            color: #57606a;
        }

        .settings-main {
            flex: 1;
            max-width: 800px;
        }

        .settings-title {
            font-size: 26px;
            font-weight: 600;
            color: #1f2328;
            margin: 0 0 24px 0;
            padding-bottom: 16px;
            border-bottom: 1px solid #d0d7de;
        }

        .settings-section {
            margin-bottom: 32px;
            padding-bottom: 24px;
            border-bottom: 1px solid #d0d7de;
        }

        .section-title {
            font-size: 16px;
            font-weight: 600;
            color: #1f2328;
            margin: 0 0 12px 0;
        }

        .form-row {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .form-input {
            padding: 8px 12px;
            font-size: 14px;
            border: 1px solid #d0d7de;
            border-radius: 6px;
            background: #fff;
        }

        .form-input:focus {
            outline: none;
            border-color: #0969da;
            box-shadow: 0 0 0 3px rgba(9, 105, 218, 0.3);
        }

        .btn {
            padding: 8px 16px;
            font-size: 14px;
            font-weight: 500;
            border-radius: 6px;
            cursor: pointer;
            border: 1px solid;
            white-space: nowrap;
        }

        .btn-secondary {
            background-color: #f6f8fa;
            color: #24292f;
            border-color: #d0d7de;
        }

        .btn-secondary:hover {
            background-color: #f3f4f6;
        }

        .btn-danger {
            background-color: #cf222e;
            color: #fff;
            border-color: #cf222e;
        }

        .btn-danger:hover {
            background-color: #a40e26;
        }

        .btn-danger:disabled {
            background-color: #f6f8fa;
            color: #8c959f;
            border-color: #d0d7de;
            cursor: not-allowed;
        }

        .visibility-options {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .visibility-option {
            display: flex;
            align-items: flex-start;
            padding: 16px;
            border: 1px solid #d0d7de;
            border-radius: 6px;
            cursor: pointer;
            background: #fff;
        }

        .visibility-option:hover {
            background-color: #f6f8fa;
        }

        .visibility-option.selected {
            border-color: #0969da;
            background-color: #ddf4ff;
        }

        .visibility-option input[type="radio"] {
            margin: 4px 12px 0 0;
        }

        .option-content {
            display: flex;
            align-items: flex-start;
            gap: 12px;
        }

        .option-content i {
            font-size: 20px;
            color: #57606a;
            margin-top: 2px;
        }

        .option-content strong {
            display: block;
            font-size: 14px;
            color: #1f2328;
            margin-bottom: 4px;
        }

        .option-content p {
            font-size: 13px;
            color: #57606a;
            margin: 0;
        }

        .danger-zone {
            border-bottom: none;
        }

        .danger-box {
            border: 1px solid #cf222e;
            border-radius: 6px;
        }

        .danger-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px;
            gap: 16px;
        }

        .danger-info strong {
            display: block;
            font-size: 14px;
            color: #1f2328;
            margin-bottom: 4px;
        }

        .danger-info p {
            font-size: 13px;
            color: #57606a;
            margin: 0;
        }

        .modal {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }

        .modal-content {
            background: #fff;
            padding: 24px;
            border-radius: 12px;
            max-width: 480px;
            width: 90%;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }

        .modal-content h3 {
            font-size: 18px;
            margin: 0 0 16px 0;
        }

        .modal-content p {
            font-size: 14px;
            line-height: 1.5;
            color: #57606a;
            margin: 0 0 16px 0;
        }

        .modal-content strong {
            color: #1f2328;
        }
    </style>
</head>
<body>

<jsp:include page="../common/header_detail.jsp">
    <jsp:param name="tab" value="settings"/>
</jsp:include>

<div class="settings-wrapper">
    <div class="settings-sidebar">
        <a href="#general" class="settings-nav-item">
            <i class="fas fa-gear"></i> General
        </a>
    </div>

    <div class="settings-main">
        <h2 class="settings-title">General</h2>

        <!-- 仓库名称 -->
        <div class="settings-section">
            <h3 class="section-title">Repository name</h3>
            <form action="${pageContext.request.contextPath}/repo/update" method="post">
                <input type="hidden" name="repoId" value="${repo.id}">
                <input type="hidden" name="owner" value="${param.owner}">
                <input type="hidden" name="action" value="rename">
                <div class="form-row">
                    <input type="text" name="newName" value="${repo.name}" class="form-input" style="width: 280px;">
                    <button type="submit" class="btn btn-secondary">Rename</button>
                </div>
            </form>
        </div>

        <!-- 描述 -->
        <div class="settings-section">
            <h3 class="section-title">Description</h3>
            <form action="${pageContext.request.contextPath}/repo/update" method="post">
                <input type="hidden" name="repoId" value="${repo.id}">
                <input type="hidden" name="owner" value="${param.owner}">
                <input type="hidden" name="action" value="description">
                <div class="form-row">
                    <input type="text" name="description" value="${repo.description}" class="form-input" placeholder="Short description of this repository" style="flex: 1;">
                    <button type="submit" class="btn btn-secondary">Save</button>
                </div>
            </form>
        </div>

        <!-- 可见性 -->
        <div class="settings-section">
            <h3 class="section-title">Visibility</h3>
            <form action="${pageContext.request.contextPath}/repo/update" method="post">
                <input type="hidden" name="repoId" value="${repo.id}">
                <input type="hidden" name="owner" value="${param.owner}">
                <input type="hidden" name="action" value="visibility">
                <div class="visibility-options">
                    <label class="visibility-option ${repo.isPublic() ? 'selected' : ''}">
                        <input type="radio" name="visibility" value="public" ${repo.isPublic() ? 'checked' : ''}>
                        <div class="option-content">
                            <i class="fas fa-book"></i>
                            <div>
                                <strong>Public</strong>
                                <p>Anyone on the internet can see this repository.</p>
                            </div>
                        </div>
                    </label>
                    <label class="visibility-option ${!repo.isPublic() ? 'selected' : ''}">
                        <input type="radio" name="visibility" value="private" ${!repo.isPublic() ? 'checked' : ''}>
                        <div class="option-content">
                            <i class="fas fa-lock"></i>
                            <div>
                                <strong>Private</strong>
                                <p>You choose who can see and commit to this repository.</p>
                            </div>
                        </div>
                    </label>
                </div>
                <button type="submit" class="btn btn-secondary" style="margin-top: 12px;">Change visibility</button>
            </form>
        </div>

        <!-- 危险区域 -->
        <div class="settings-section danger-zone">
            <h3 class="section-title" style="color: #cf222e;">Danger Zone</h3>
            <div class="danger-box">
                <div class="danger-item">
                    <div class="danger-info">
                        <strong>Delete this repository</strong>
                        <p>Once you delete a repository, there is no going back. Please be certain.</p>
                    </div>
                    <button type="button" class="btn btn-danger" onclick="showDeleteModal()">Delete this repository</button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 删除确认弹窗 -->
<div id="deleteModal" class="modal" style="display: none;">
    <div class="modal-content">
        <h3 style="color: #cf222e;">Are you absolutely sure?</h3>
        <p>This action <strong>cannot</strong> be undone. This will permanently delete the <strong>${param.owner}/${param.repo}</strong> repository, wiki, issues, comments, and remove all collaborator associations.</p>
        <form action="${pageContext.request.contextPath}/repo/delete" method="post">
            <input type="hidden" name="repoId" value="${repo.id}">
            <input type="hidden" name="repoName" value="${repo.name}">
            <p>Please type <strong>${param.owner}/${param.repo}</strong> to confirm.</p>
            <input type="text" id="confirmInput" class="form-input" style="width: 100%; margin-bottom: 16px; box-sizing: border-box;" oninput="checkConfirm()">
            <div style="display: flex; gap: 8px; justify-content: flex-end;">
                <button type="button" class="btn btn-secondary" onclick="hideDeleteModal()">Cancel</button>
                <button type="submit" id="confirmDeleteBtn" class="btn btn-danger" disabled>I understand, delete this repository</button>
            </div>
        </form>
    </div>
</div>

<script>
    function showDeleteModal() {
        document.getElementById('deleteModal').style.display = 'flex';
    }

    function hideDeleteModal() {
        document.getElementById('deleteModal').style.display = 'none';
        document.getElementById('confirmInput').value = '';
        document.getElementById('confirmDeleteBtn').disabled = true;
    }

    function checkConfirm() {
        var input = document.getElementById('confirmInput').value;
        var expected = '${param.owner}/${param.repo}';
        document.getElementById('confirmDeleteBtn').disabled = (input !== expected);
    }
</script>

</body>
</html>
