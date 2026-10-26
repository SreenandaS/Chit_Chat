<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Desktop Sidebar -->
<aside class="sidebar d-none d-md-flex">
    <a href="${pageContext.request.contextPath}/feed" class="brand-logo mb-3">
        ChitChat 💬
    </a>

    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/feed" class="nav-link-item ${param.active == 'feed' ? 'active' : ''}">
            <i class="bi bi-house-door-fill"></i>
            <span>Home</span>
        </a>

        <a href="${pageContext.request.contextPath}/search" class="nav-link-item ${param.active == 'search' ? 'active' : ''}">
            <i class="bi bi-search"></i>
            <span>Search</span>
        </a>

        <a href="${pageContext.request.contextPath}/messages" class="nav-link-item ${param.active == 'messages' ? 'active' : ''}">
            <i class="bi bi-chat-dots-fill"></i>
            <span>Messages</span>
        </a>

        <a href="${pageContext.request.contextPath}/notifications" class="nav-link-item ${param.active == 'notifications' ? 'active' : ''}">
            <i class="bi bi-bell-fill"></i>
            <span>Notifications</span>
            <c:if test="${unreadCount > 0}">
                <span class="nav-badge" id="navNotifBadge">${unreadCount}</span>
            </c:if>
        </a>

        <a href="${pageContext.request.contextPath}/profile" class="nav-link-item ${param.active == 'profile' ? 'active' : ''}">
            <i class="bi bi-person-fill"></i>
            <span>Profile</span>
        </a>

        <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.admin}">
            <a href="${pageContext.request.contextPath}/admin" class="nav-link-item ${param.active == 'admin' ? 'active' : ''}">
                <i class="bi bi-shield-lock-fill text-danger"></i>
                <span class="text-danger fw-bold">Admin Panel</span>
            </a>
        </c:if>

        <a href="${pageContext.request.contextPath}/logout" class="nav-link-item mt-auto text-muted">
            <i class="bi bi-box-arrow-right"></i>
            <span>Logout</span>
        </a>
    </nav>

    <!-- User Profile Snippet in Sidebar -->
    <c:if test="${sessionScope.currentUser != null}">
        <div class="sidebar-user-card pt-3 border-top mt-3">
            <a href="${pageContext.request.contextPath}/profile" class="d-flex align-items-center text-decoration-none text-dark gap-2">
                <div class="avatar-ring">
                    <img src="${sessionScope.currentUser.profilePic}" alt="Avatar" class="avatar-img avatar-sm">
                </div>
                <div class="overflow-hidden">
                    <div class="fw-semibold text-truncate small">${sessionScope.currentUser.fullName}</div>
                    <div class="text-muted text-truncate" style="font-size: 0.75rem;">@${sessionScope.currentUser.username}</div>
                </div>
            </a>
        </div>
    </c:if>
</aside>

<!-- Mobile Top Bar -->
<div class="d-md-none fixed-top bg-white border-bottom px-3 py-2 d-flex justify-content-between align-items-center z-3">
    <a href="${pageContext.request.contextPath}/feed" class="brand-logo" style="font-size: 1.3rem;">
        ChitChat 💬
    </a>
    <div class="d-flex gap-3 align-items-center">
        <a href="${pageContext.request.contextPath}/notifications" class="text-dark position-relative">
            <i class="bi bi-bell fs-5"></i>
            <c:if test="${unreadCount > 0}">
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.6rem;">${unreadCount}</span>
            </c:if>
        </a>
        <a href="${pageContext.request.contextPath}/messages" class="text-dark">
            <i class="bi bi-chat-dots fs-5"></i>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-muted">
            <i class="bi bi-box-arrow-right fs-5"></i>
        </a>
    </div>
</div>

<!-- Mobile Bottom Bar -->
<div class="mobile-bottom-bar d-md-none fixed-bottom bg-white border-top d-flex justify-content-around py-2 z-3">
    <a href="${pageContext.request.contextPath}/feed" class="text-dark fs-4 ${param.active == 'feed' ? 'text-primary' : ''}"><i class="bi bi-house-door-fill"></i></a>
    <a href="${pageContext.request.contextPath}/search" class="text-dark fs-4 ${param.active == 'search' ? 'text-primary' : ''}"><i class="bi bi-search"></i></a>
    <a href="${pageContext.request.contextPath}/messages" class="text-dark fs-4 ${param.active == 'messages' ? 'text-primary' : ''}"><i class="bi bi-chat-dots-fill"></i></a>
    <a href="${pageContext.request.contextPath}/profile" class="text-dark fs-4 ${param.active == 'profile' ? 'text-primary' : ''}"><i class="bi bi-person-fill"></i></a>
</div>
