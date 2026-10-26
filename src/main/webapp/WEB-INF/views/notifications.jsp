<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Notifications - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="notifications" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper">
        <div class="feed-container" style="max-width: 600px;">
            <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-4">
                <div class="p-4 border-bottom d-flex align-items-center justify-content-between">
                    <div>
                        <h5 class="fw-bold mb-0">Notifications</h5>
                        <span class="text-muted small">Stay updated on likes and messages</span>
                    </div>
                    <span class="badge bg-light text-dark border rounded-pill px-3 py-2 small">
                        ${notifications.size()} Total
                    </span>
                </div>

                <div class="list-group list-group-flush">
                    <c:choose>
                        <c:when test="${empty notifications}">
                            <div class="p-5 text-center text-muted">
                                <div class="fs-1 mb-2">🔔</div>
                                <h6 class="fw-bold">No notifications yet</h6>
                                <p class="small mb-0">When other people like your posts or send you messages, they will appear here.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="notif" items="${notifications}">
                                <div class="list-group-item p-3 d-flex align-items-center justify-content-between ${not notif.read ? 'bg-light' : ''}">
                                    <div class="d-flex align-items-center gap-3">
                                        <!-- Actor Avatar & Type Icon Badge -->
                                        <div class="position-relative">
                                            <img src="${notif.actorPic}" alt="${notif.actorFullName}" class="avatar-img" style="width: 44px; height: 44px;">
                                            <span class="position-absolute bottom-0 end-0 rounded-circle d-flex align-items-center justify-content-center text-white"
                                                  style="width: 18px; height: 18px; font-size: 0.65rem; background: ${notif.type == 'LIKE' ? 'var(--accent-pink)' : 'var(--accent-purple)'};">
                                                <i class="bi ${notif.type == 'LIKE' ? 'bi-heart-fill' : 'bi-chat-dots-fill'}"></i>
                                            </span>
                                        </div>

                                        <div>
                                            <div class="small">
                                                <a href="${pageContext.request.contextPath}/profile?id=${notif.actorId}" class="fw-bold text-dark text-decoration-none">
                                                    ${notif.actorFullName}
                                                </a>
                                                <span class="text-secondary">${notif.message}</span>
                                            </div>
                                            <div class="text-muted" style="font-size: 0.72rem;">${notif.formattedDate}</div>
                                        </div>
                                    </div>

                                    <div>
                                        <c:choose>
                                            <c:when test="${notif.type == 'LIKE'}">
                                                <a href="${pageContext.request.contextPath}/feed#post-${notif.postId}" class="btn btn-sm btn-light rounded-pill px-3 small">
                                                    View Post
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/messages?userId=${notif.actorId}" class="brand-gradient-btn py-1 px-3 small">
                                                    Reply
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="common/footer.jsp" />
