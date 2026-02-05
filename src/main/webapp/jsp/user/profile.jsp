<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: User profile page showing user information and repositories
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ include file="../common/header_profile.jsp" %>

<div class="profile-container">

    <!-- Left Sidebar -->
    <div class="profile-sidebar">
        <img src="${targetUser.avatar != null ? targetUser.avatar : 'https://github.com/identicons/user.png'}"
             class="big-avatar">

        <div class="profile-names">
            <h1>${targetUser.username}</h1>
        </div>

        <div class="mb-3 mt-3">
            <c:choose>
                <c:when test="${isOwner}">
                    <button class="btn-edit" onclick="showEditModal()">Edit profile</button>
                </c:when>
                <c:otherwise>
                    <button class="btn-edit">Follow</button>
                </c:otherwise>
            </c:choose>
        </div>

        <div style="font-size: 14px; color: #24292f;">
            <i class="far fa-envelope mr-2"></i> ${targetUser.email}
        </div>
    </div>

    <!-- Right Content -->
    <div class="profile-content">
        <c:choose>
            <c:when test="${tab == 'overview'}">
                <div class="text-small color-fg-muted mb-2">Popular repositories</div>

                <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px; margin-bottom:24px;">
                    <c:forEach items="${popularRepos}" var="repo">
                        <div style="border:1px solid #d0d7de; border-radius:6px; padding:16px; background:#fff;">
                            <div class="d-flex justify-between items-center mb-2">
                                <a href="${pageContext.request.contextPath}/repo/${targetUser.username}/${repo.name}" class="text-bold" style="color:#0969da; text-decoration:none;">${repo.name}</a>
                                <span class="text-small color-fg-muted border px-1 rounded-1">Public</span>
                            </div>
                            <p class="text-small color-fg-muted mb-3 flex-auto">${repo.description}</p>
                            <div class="text-small color-fg-muted">
                                <i class="fas fa-circle" style="color:#b07219"></i> Java
                                <span class="mr-3"><i class="far fa-star"></i> ${repo.starCount}</span>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Flex header (title left, button right) -->
                <div class="contribution-header">
                    <div class="text-small color-fg-muted">Contributions in the last year</div>
                    <button class="year-btn">2025</button>
                </div>

                <div class="contribution-box">
                    <div class="graph-container">
                        <div class="graph-body">
                            <!-- Container -->
                            <div class="pixels">
                                <%
                                    Map<String, Integer> data = (Map<String, Integer>) request.getAttribute("contributionMap");
                                    if(data == null) data = new HashMap<>();

                                    Calendar cal = Calendar.getInstance();
                                    // Core fix: lock to January 1st of this year
                                    int currentYear = cal.get(Calendar.YEAR);
                                    cal.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat tooltipFmt = new SimpleDateFormat("MMM d, yyyy");

                                    // Loop until January 1st of next year (fill the entire year)
                                    while (cal.get(Calendar.YEAR) == currentYear) {

                                        String d = sdf.format(cal.getTime());
                                        String t = tooltipFmt.format(cal.getTime());
                                        Integer c = data.get(d);

                                        int l = 0;
                                        if (c != null && c > 0) {
                                            if (c <= 2) l = 1; else if (c <= 5) l = 2; else if (c <= 8) l = 3; else l = 4;
                                        }
                                %>
                                <div class="pixel l<%=l%>" data-title="<%= (c!=null?c:"No") %> contributions on <%=t%>"></div>
                                <%
                                        // Add 1 day for each grid
                                        cal.add(Calendar.DAY_OF_YEAR, 1);
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <!-- Repositories / Stars (unchanged) -->
                <div class="d-flex border-bottom pb-3 mb-3">
                    <input type="text" placeholder="Find a repository..." style="width:100%; padding:5px 12px; border:1px solid #d0d7de; border-radius:6px; outline:none;">
                </div>

                <c:set var="listToShow" value="${tab == 'repositories' ? repoList : starredRepos}" />
                <c:forEach items="${listToShow}" var="repo">
                    <div class="repo-row">
                        <div>
                            <div class="mb-2">
                                <a href="${pageContext.request.contextPath}/repo/${targetUser.username}/${repo.name}" class="text-bold" style="font-size:20px; color:#0969da; text-decoration:none;">${repo.name}</a>
                                <span class="text-small color-fg-muted border px-1 rounded-1 ml-2">Public</span>
                            </div>
                            <div class="text-small color-fg-muted mb-2">${repo.description}</div>
                            <div class="text-small color-fg-muted">
                                <i class="fas fa-circle" style="color:#b07219"></i> Java
                                <span class="ml-3">Updated recently</span>
                            </div>
                        </div>
                        <div><button class="btn-star">Star</button></div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Edit Profile Modal -->
<div id="editProfileModal" class="modal" style="display: none;">
    <div class="modal-content" style="max-width: 500px;">
        <h3 style="margin: 0 0 20px 0; font-size: 20px;">Edit profile</h3>
        <form action="${pageContext.request.contextPath}/user/updateProfile" method="post" enctype="multipart/form-data">
            <div style="margin-bottom: 20px; text-align: center;">
                <img id="avatarPreview" src="${targetUser.avatar != null ? targetUser.avatar : 'https://github.com/identicons/user.png'}"
                     style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 2px solid #d0d7de;">
                <div style="margin-top: 10px;">
                    <label style="padding: 6px 12px; font-size: 13px; background: #f6f8fa; color: #24292f; border: 1px solid #d0d7de; border-radius: 6px; cursor: pointer;">
                        <i class="fas fa-camera"></i> Change avatar
                        <input type="file" name="avatar" accept="image/*" style="display: none;" onchange="previewAvatar(this)">
                    </label>
                </div>
            </div>
            <div style="margin-bottom: 16px;">
                <label style="display: block; font-size: 14px; font-weight: 600; margin-bottom: 6px;">Username</label>
                <input type="text" name="username" value="${targetUser.username}"
                       style="width: 100%; padding: 8px 12px; font-size: 14px; border: 1px solid #d0d7de; border-radius: 6px; box-sizing: border-box;">
            </div>
            <div style="margin-bottom: 16px;">
                <label style="display: block; font-size: 14px; font-weight: 600; margin-bottom: 6px;">Email</label>
                <input type="email" name="email" value="${targetUser.email}"
                       style="width: 100%; padding: 8px 12px; font-size: 14px; border: 1px solid #d0d7de; border-radius: 6px; box-sizing: border-box;">
            </div>
            <div style="margin-bottom: 16px;">
                <label style="display: block; font-size: 14px; font-weight: 600; margin-bottom: 6px;">Bio</label>
                <textarea name="bio" rows="3" placeholder="Tell us a little bit about yourself"
                          style="width: 100%; padding: 8px 12px; font-size: 14px; border: 1px solid #d0d7de; border-radius: 6px; box-sizing: border-box; resize: vertical;">${targetUser.bio}</textarea>
            </div>
            <div style="display: flex; gap: 8px; justify-content: flex-end;">
                <button type="button" onclick="hideEditModal()"
                        style="padding: 8px 16px; font-size: 14px; background: #f6f8fa; color: #24292f; border: 1px solid #d0d7de; border-radius: 6px; cursor: pointer;">Cancel</button>
                <button type="submit"
                        style="padding: 8px 16px; font-size: 14px; background: #2da44e; color: #fff; border: 1px solid #2da44e; border-radius: 6px; cursor: pointer;">Save</button>
            </div>
        </form>
    </div>
</div>

<style>
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
        width: 90%;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }
</style>

<script>
    function showEditModal() {
        document.getElementById('editProfileModal').style.display = 'flex';
    }
    function hideEditModal() {
        document.getElementById('editProfileModal').style.display = 'none';
    }
    function previewAvatar(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('avatarPreview').src = e.target.result;
            }
            reader.readAsDataURL(input.files[0]);
        }
    }
</script>

</body>
</html>