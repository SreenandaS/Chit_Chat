<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Edit Profile - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="profile" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper">
        <div class="feed-container" style="max-width: 520px;">
            <div class="card border-0 shadow-sm p-4 rounded-4">
                <div class="d-flex align-items-center justify-content-between mb-4 pb-2 border-bottom">
                    <h5 class="fw-bold mb-0">Edit Profile</h5>
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-sm btn-light rounded-pill">
                        Cancel
                    </a>
                </div>

                <form action="${pageContext.request.contextPath}/profile/edit" method="POST">
                    <!-- Current Avatar Preview -->
                    <div class="text-center mb-4">
                        <div class="avatar-ring mb-2">
                            <img src="${user.profilePic}" alt="Avatar Preview" id="avatarPreview"
                                 class="avatar-img avatar-lg">
                        </div>
                        <div class="text-muted small">@${user.username}</div>
                    </div>

                    <div class="mb-3">
                        <label for="fullName" class="form-label small fw-semibold text-muted">Full Name</label>
                        <input type="text" class="form-control bg-light" id="fullName" name="fullName"
                               value="${user.fullName}" required>
                    </div>

                    <div class="mb-3">
                        <label for="bio" class="form-label small fw-semibold text-muted">Bio</label>
                        <textarea class="form-control bg-light" id="bio" name="bio" rows="3"
                                  placeholder="Write a few lines about yourself...">${user.bio}</textarea>
                    </div>

                    <div class="mb-4">
                        <label for="profilePic" class="form-label small fw-semibold text-muted">Profile Picture URL</label>
                        <input type="url" class="form-control bg-light mb-2" id="profilePic" name="profilePic"
                               value="${user.profilePic}" oninput="updateAvatarPreview(this.value)">

                        <div class="d-flex gap-2 align-items-center mt-2">
                            <span class="small text-muted">Presets:</span>
                            <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" class="rounded-circle border"
                                 style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                                 onclick="choosePreset(this.src)">
                            <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150" class="rounded-circle border"
                                 style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                                 onclick="choosePreset(this.src)">
                            <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150" class="rounded-circle border"
                                 style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                                 onclick="choosePreset(this.src)">
                            <img src="https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150" class="rounded-circle border"
                                 style="width: 32px; height: 32px; cursor: pointer; object-fit: cover;"
                                 onclick="choosePreset(this.src)">
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="brand-gradient-btn flex-grow-1 py-2">
                            Save Changes <i class="bi bi-check-lg ms-1"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<script>
    function updateAvatarPreview(url) {
        if (url && url.trim().length > 0) {
            document.getElementById('avatarPreview').src = url;
        }
    }

    function choosePreset(url) {
        document.getElementById('profilePic').value = url;
        updateAvatarPreview(url);
    }
</script>

<jsp:include page="common/footer.jsp" />
