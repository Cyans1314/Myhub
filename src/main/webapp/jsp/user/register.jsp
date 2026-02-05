<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: User registration page
 * ============================================================================
--%>
<%@ include file="../common/header.jsp" %>
<div class="container-md py-6" style="max-width: 340px;">
    <div class="text-center mb-4">
        <h3 class="f3">Create your account</h3>
    </div>

    <div class="Box p-4">
        <c:if test="${not empty error}">
            <div class="flash flash-error mb-3">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/register" method="post">
            <label class="d-block mb-2">Username</label>
            <input type="text" name="username" class="form-control input-block mb-3" required>

            <label class="d-block mb-2">Email</label>
            <input type="email" name="email" class="form-control input-block mb-3" required>

            <label class="d-block mb-2">Password</label>
            <input type="password" name="password" class="form-control input-block mb-3" required>

            <button type="submit" class="btn btn-primary btn-block">Create account</button>
        </form>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>