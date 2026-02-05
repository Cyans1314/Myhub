<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Code viewer page for displaying repository files with syntax highlighting
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.example.myhub.service.GitService" %>
<%@ page import="com.example.myhub.dto.FileNode" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <title>${param.owner}/${param.repo} - ${param.file}</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/code.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" />
    <!-- Code highlighting - using highlight.js -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/cpp.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/python.min.js"></script>
</head>
<body>

<!-- Header -->
<jsp:include page="../common/header.jsp" />

<%
    // Get parameters
    String ownerName = request.getParameter("owner");
    String repoName = request.getParameter("repo");
    String fileName = request.getParameter("file");
    String branch = request.getParameter("branch");
    if (branch == null) branch = "main";

    // If repoName looks like an object reference, try to extract the real repository name from the URL
    if (repoName != null && repoName.contains("@")) {
        // This is an object reference, we need to get the real repository name from elsewhere
        // Try to extract from HTTP Referer
        String referer = request.getHeader("Referer");

        if (referer != null) {
            // Extract repository name from referer URL
            // Example: http://localhost:8080/MyHub/jsp/repo/detail.jsp?owner=admin&repo=test2
            String[] parts = referer.split("[?&]");
            for (String part : parts) {
                if (part.startsWith("repo=") && !part.contains("@")) {
                    repoName = part.substring(5); // Remove "repo=" prefix
                    break;
                }
            }
        }

        // If still not found, use default value
        if (repoName == null || repoName.contains("@")) {
            repoName = "test2"; // Last resort
        }
    }

    // Get file content and file list
    String fileContent = "";
    List<FileNode> files = null;
    String language = "text";

    if (ownerName != null && repoName != null && fileName != null) {
        GitService gitService = new GitService();

        try {
            // Get file content
            fileContent = gitService.getFileContent(ownerName, repoName, branch, fileName);

            if (fileContent == null || fileContent.trim().isEmpty()) {
                fileContent = "File does not exist or cannot be read.";
            }

            // Get file list
            files = gitService.listFiles(ownerName, repoName, branch, "");

        } catch (Exception e) {
            fileContent = "Error reading file: " + e.getMessage();
        }

        // Determine language based on file extension
        if (fileName.endsWith(".cpp") || fileName.endsWith(".c") || fileName.endsWith(".h")) {
            language = "cpp";
        } else if (fileName.endsWith(".py")) {
            language = "python";
        } else if (fileName.endsWith(".java")) {
            language = "java";
        } else if (fileName.endsWith(".js")) {
            language = "javascript";
        } else if (fileName.endsWith(".html")) {
            language = "html";
        } else if (fileName.endsWith(".css")) {
            language = "css";
        }
    } else {
        fileContent = "Missing required parameters";
    }

    request.setAttribute("fileContent", fileContent);
    request.setAttribute("files", files);
    request.setAttribute("language", language);
    request.setAttribute("fileName", fileName);
    request.setAttribute("ownerName", ownerName);
    request.setAttribute("repoName", repoName);
    request.setAttribute("branch", branch);

    // Also set as param-style attributes, overriding original parameters
    request.setAttribute("correctedOwner", ownerName);
    request.setAttribute("correctedRepo", repoName);
    request.setAttribute("correctedFile", fileName);
    request.setAttribute("correctedBranch", branch);
%>

<div class="code-container">
    <div class="code-content">
        <!-- Left side file list -->
        <div class="file-sidebar">
            <div class="file-sidebar-header">
                <i class="fas fa-folder"></i> Files
            </div>
            <div class="file-list">
                <c:choose>
                    <c:when test="${files != null}">
                        <c:forEach var="file" items="${files}">
                            <c:if test="${file.type == 'file'}">
                                <a href="${pageContext.request.contextPath}/jsp/repo/code.jsp?owner=${correctedOwner}&repo=${correctedRepo}&file=${file.name}&branch=${correctedBranch}"
                                   class="file-item ${file.name == correctedFile ? 'selected' : ''}">
                                    <i class="file-icon fas fa-file"></i>
                                        ${file.name}
                                </a>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div style="padding: 16px; color: #666;">No file data</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Right side code area -->
        <div class="code-main">
            <!-- Code header -->
            <div class="code-header">
                <div class="code-breadcrumb">
                    ${correctedOwner} / ${correctedRepo} / ${correctedFile}
                </div>
                <a href="${pageContext.request.contextPath}/jsp/repo/detail.jsp?owner=${correctedOwner}&repo=${correctedRepo}"
                   class="back-btn">
                    <i class="fas fa-arrow-left"></i>
                    Back to repository
                </a>
            </div>

            <!-- Code container -->
            <div class="code-container-box">
                <div class="code-file-header">
                    <div>
                        <i class="fas fa-file-code"></i>
                        ${correctedFile}
                    </div>
                    <div>
                        <c:choose>
                            <c:when test="${fileContent != null}">
                                ${fileContent.length()} bytes
                            </c:when>
                            <c:otherwise>
                                0 bytes
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="code-content-area">
                    <div class="code-with-line-numbers">
                        <%
                            String content = (String) request.getAttribute("fileContent");
                            if (content != null && !content.isEmpty()) {
                                String[] lines = content.split("\n");
                                for (int i = 0; i < lines.length; i++) {
                                    String line = lines[i];
                                    // HTML escape
                                    line = line.replace("&", "&amp;")
                                            .replace("<", "&lt;")
                                            .replace(">", "&gt;")
                                            .replace("\"", "&quot;")
                                            .replace("'", "&#39;");
                        %>
                        <div class="code-line">
                            <div class="line-number"><%= i + 1 %></div>
                            <div class="code-content-cell">
                                <code class="hljs language-<%= request.getAttribute("language") %>"><%= line %></code>
                            </div>
                        </div>
                        <%
                            }
                        } else {
                        %>
                        <div class="code-line">
                            <div class="line-number">1</div>
                            <div class="code-content-cell">
                                <code class="hljs">File content is empty or cannot be read</code>
                            </div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Apply syntax highlighting
        if (typeof hljs !== 'undefined') {
            document.querySelectorAll('code.hljs').forEach(function(block) {
                hljs.highlightElement(block);
            });
        }
    });
</script>

</body>
</html>