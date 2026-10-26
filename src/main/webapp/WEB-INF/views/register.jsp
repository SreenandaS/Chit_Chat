<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Create Account - ChitChat" />
</jsp:include>

<div class="container d-flex align-items-center justify-content-center min-vh-100 py-4">
    <div class="card border-0 shadow-lg p-4 p-md-5" style="max-width: 480px; width: 100%; border-radius: 20px;">
        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/login" class="brand-logo mb-2" style="font-size: 2.2rem;">ChitChat 💬</a>
            <p class="text-muted small">Join ChitChat to connect with friends & share your moments</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger py-2 small rounded-3 d-flex align-items-center" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <div>${errorMessage}</div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="POST">
            <div class="row g-2 mb-3">
                <div class="col-md-6">
                    <label for="username" class="form-label small fw-semibold text-muted">Username *</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0">@</span>
                        <input type="text" class="form-control bg-light border-start-0" id="username" name="username"
                               placeholder="john_doe" required autofocus>
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="fullName" class="form-label small fw-semibold text-muted">Full Name *</label>
                    <input type="text" class="form-control bg-light" id="fullName" name="fullName"
                           placeholder="John Doe" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label small fw-semibold text-muted">Email Address *</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope text-muted"></i></span>
                    <input type="email" class="form-control bg-light border-start-0" id="email" name="email"
                           placeholder="john@example.com" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label small fw-semibold text-muted">Password *</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                    <input type="password" class="form-control bg-light border-start-0" id="password" name="password"
                           placeholder="At least 4 characters" minlength="4" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="bio" class="form-label small fw-semibold text-muted">Bio (Optional)</label>
                <textarea class="form-control bg-light" id="bio" name="bio" rows="2"
                          placeholder="Tell the community a little about yourself..."></textarea>
            </div>

            <div class="mb-4">
                <label for="profilePic" class="form-label small fw-semibold text-muted">Profile Picture URL (Optional)</label>
                <input type="url" class="form-control bg-light mb-2" id="profilePic" name="profilePic"
                       placeholder="https://images.unsplash.com/... or choose preset below">

                <div class="d-flex gap-2 align-items-center">
                    <span class="small text-muted">Presets:</span>
                    <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" class="rounded-circle border"
                         style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                         onclick="selectAvatar(this.src)" title="Choose avatar 1">
                    <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150" class="rounded-circle border"
                         style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                         onclick="selectAvatar(this.src)" title="Choose avatar 2">
                    <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150" class="rounded-circle border"
                         style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                         onclick="selectAvatar(this.src)" title="Choose avatar 3">
                    <img src="https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150" class="rounded-circle border"
                         style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                         onclick="selectAvatar(this.src)" title="Choose avatar 4">
                </div>
            </div>

            <button type="submit" class="brand-gradient-btn w-100 py-2 fs-6 mb-3">
                Create Account <i class="bi bi-check-lg"></i>
            </button>
        </form>

        <div class="text-center mt-2">
            <span class="text-muted small">Already have an account? </span>
            <a href="${pageContext.request.contextPath}/login" class="fw-semibold text-decoration-none" style="color: var(--accent-pink);">
                Sign In
            </a>
        </div>
    </div>
</div>

<script>
    function selectAvatar(url) {
        document.getElementById('profilePic').value = url;
    }
</script>

<jsp:include page="common/footer.jsp" />
