<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Login to ChitChat" />
</jsp:include>

<div class="container d-flex align-items-center justify-content-center min-vh-100 py-4">
    <div class="card border-0 shadow-lg p-4 p-md-5" style="max-width: 440px; width: 100%; border-radius: 20px;">
        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/login" class="brand-logo mb-2" style="font-size: 2.2rem;">ChitChat 💬</a>
            <p class="text-muted small">Connect, share, and chitchat with friends</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger py-2 small rounded-3 d-flex align-items-center" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <div>${errorMessage}</div>
            </div>
        </c:if>

        <c:if test="${param.msg == 'logged_out'}">
            <div class="alert alert-info py-2 small rounded-3 d-flex align-items-center" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                <div>You have been successfully logged out.</div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="POST">
            <div class="mb-3">
                <label for="usernameOrEmail" class="form-label small fw-semibold text-muted">Username or Email</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-person text-muted"></i></span>
                    <input type="text" class="form-control bg-light border-start-0" id="usernameOrEmail" name="usernameOrEmail"
                           value="${prevUsername}" placeholder="e.g. sarah_c or admin" required autofocus>
                </div>
            </div>

            <div class="mb-4">
                <label for="password" class="form-label small fw-semibold text-muted">Password</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                    <input type="password" class="form-control bg-light border-start-0" id="password" name="password"
                           placeholder="••••••••" required>
                </div>
            </div>

            <button type="submit" class="brand-gradient-btn w-100 py-2 fs-6 mb-3">
                Sign In <i class="bi bi-arrow-right"></i>
            </button>
        </form>

        <!-- Quick Demo Account Fill -->
        <div class="bg-light p-3 rounded-3 mb-3 small border">
            <div class="fw-bold text-dark mb-1 d-flex align-items-center gap-1">
                <i class="bi bi-lightning-charge-fill text-warning"></i> Demo Test Accounts:
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2">
                <span>👤 Regular User:</span>
                <button type="button" class="btn btn-sm btn-outline-secondary py-0 px-2"
                        onclick="fillCreds('sarah_c', 'password123')">
                    sarah_c / password123
                </button>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2">
                <span>🛡️ Admin:</span>
                <button type="button" class="btn btn-sm btn-outline-danger py-0 px-2"
                        onclick="fillCreds('admin', 'admin123')">
                    admin / admin123
                </button>
            </div>
        </div>

        <div class="text-center mt-2">
            <span class="text-muted small">Don't have an account? </span>
            <a href="${pageContext.request.contextPath}/register" class="fw-semibold text-decoration-none" style="color: var(--accent-pink);">
                Sign Up
            </a>
        </div>
    </div>
</div>

<script>
    function fillCreds(u, p) {
        document.getElementById('usernameOrEmail').value = u;
        document.getElementById('password').value = p;
    }
</script>

<jsp:include page="common/footer.jsp" />
