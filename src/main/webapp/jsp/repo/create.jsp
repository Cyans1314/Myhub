<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Create new repository page
 * ============================================================================
--%>
<%@ include file="../common/header.jsp" %>
<div class="container-md py-6">
    <div class="border-bottom mb-4 pb-2">
        <h2 class="f3">Create a new repository</h2>
        <p class="color-fg-muted">A repository contains all project files, including the revision history.</p>
    </div>

    <form action="${pageContext.request.contextPath}/repo/create" method="post">
        <c:if test="${not empty error}">
            <div class="flash flash-error mb-3">${error}</div>
        </c:if>

        <label class="d-block text-bold mb-2">Repository name <span class="color-fg-danger">*</span></label>
        <input type="text" name="repoName" class="form-control input-block mb-3" style="max-width: 400px;" required>

        <label class="d-block text-bold mb-2">Description <span class="text-normal color-fg-muted">(optional)</span></label>
        <input type="text" name="description" class="form-control input-block mb-3">

        <div class="form-group">
            <div class="form-checkbox">
                <input type="radio" name="visibility" value="public" checked>
                <label>
                    <i class="fas fa-globe color-fg-muted mr-1"></i> Public
                    <span class="d-block text-normal color-fg-muted pl-4">Anyone on the internet can see this repository.</span>
                </label>
            </div>
            <div class="form-checkbox">
                <input type="radio" name="visibility" value="private">
                <label>
                    <i class="fas fa-lock color-fg-muted mr-1"></i> Private
                    <span class="d-block text-normal color-fg-muted pl-4">You choose who can see and commit to this repository.</span>
                </label>
            </div>
        </div>

        <div class="border-top pt-3 mt-3">
            <div class="form-checkbox">
                <input type="checkbox" name="autoInit" value="true" checked>
                <label class="text-bold">Initialize this repository with a README</label>
            </div>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Create repository</button>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>