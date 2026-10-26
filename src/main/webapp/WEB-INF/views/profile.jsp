<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="${profileUser.fullName} (@${profileUser.username}) - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="${isOwner ? 'profile' : ''}" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper">
        <div class="feed-container">

            <!-- Profile Header Card -->
            <div class="card border-0 shadow-sm p-4 rounded-4 mb-4">
                <div class="d-flex flex-column flex-sm-row align-items-center align-items-sm-start gap-4">
                    <div class="avatar-ring">
                        <img src="${profileUser.profilePic}" alt="${profileUser.fullName}" class="avatar-img avatar-lg">
                    </div>

                    <div class="flex-grow-1 text-center text-sm-start">
                        <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-sm-start gap-2 mb-1">
                            <h4 class="fw-bold mb-0">${profileUser.fullName}</h4>
                            <c:if test="${profileUser.admin}">
                                <span class="badge bg-danger rounded-pill px-2 py-1 small"><i class="bi bi-shield-check"></i> Admin</span>
                            </c:if>
                        </div>

                        <div class="text-muted small mb-3">@${profileUser.username}</div>

                        <p class="mb-3 text-dark small" style="white-space: pre-wrap;">${profileUser.bio != null && !profileUser.bio.isEmpty() ? profileUser.bio : 'No bio added yet.'}</p>

                        <!-- Stats Row -->
                        <div class="d-flex justify-content-center justify-content-sm-start gap-4 mb-3">
                            <div>
                                <span class="fw-bold">${profileUser.postCount}</span>
                                <span class="text-muted small">posts</span>
                            </div>
                            <div>
                                <span class="fw-bold">${profileUser.likeCount}</span>
                                <span class="text-muted small">likes</span>
                            </div>
                        </div>

                        <!-- Actions -->
                        <div class="d-flex gap-2 justify-content-center justify-content-sm-start">
                            <c:choose>
                                <c:when test="${isOwner}">
                                    <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-sm btn-outline-secondary rounded-pill px-3 fw-semibold">
                                        <i class="bi bi-pencil-square me-1"></i> Edit Profile
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/messages?userId=${profileUser.id}" class="brand-gradient-btn py-1 px-3 small">
                                        <i class="bi bi-chat-dots-fill me-1"></i> Message
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Profile Posts Section -->
            <div class="d-flex align-items-center justify-content-between mb-3 px-1">
                <h6 class="fw-bold text-muted text-uppercase mb-0" style="letter-spacing: 0.5px; font-size: 0.8rem;">
                    <i class="bi bi-grid-3x3 me-1"></i> Posts by ${profileUser.fullName}
                </h6>
            </div>

            <c:choose>
                <c:when test="${empty posts}">
                    <div class="card border-0 shadow-sm p-5 text-center rounded-4">
                        <div class="fs-1 mb-2">📸</div>
                        <h6 class="fw-bold">No posts shared yet</h6>
                        <p class="text-muted small">When ${profileUser.fullName} shares posts, they will show up here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="post" items="${posts}">
                        <div class="card border-0 shadow-sm mb-4 rounded-4 overflow-hidden" id="post-${post.id}">
                            <div class="p-3 d-flex align-items-center justify-content-between">
                                <div class="d-flex align-items-center gap-2">
                                    <img src="${post.authorPic}" alt="${post.authorFullName}" class="avatar-img avatar-sm">
                                    <div>
                                        <div class="fw-semibold small">${post.authorFullName}</div>
                                        <div class="text-muted" style="font-size: 0.75rem;">${post.formattedDate}</div>
                                    </div>
                                </div>

                                <c:if test="${isOwner || sessionScope.currentUser.admin}">
                                    <form action="${pageContext.request.contextPath}/post/delete" method="POST"
                                          onsubmit="return confirm('Delete this post?');">
                                        <input type="hidden" name="postId" value="${post.id}">
                                        <button type="submit" class="btn btn-sm btn-link text-danger p-0" title="Delete Post">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </c:if>
                            </div>

                            <div class="px-3 pb-2">
                                <p class="mb-0 text-dark" style="white-space: pre-wrap; word-break: break-word;">${post.content}</p>
                            </div>

                            <c:if test="${not empty post.imageUrl}">
                                <div class="mt-2 bg-light text-center">
                                    <img src="${post.imageUrl}" alt="Post Media" class="img-fluid w-100"
                                         style="max-height: 480px; object-fit: cover;" onerror="this.style.display='none'">
                                </div>
                            </c:if>

                            <div class="post-actions px-3 py-2 border-top mt-2 d-flex align-items-center justify-content-between">
                                <div class="d-flex align-items-center gap-2">
                                    <button type="button" class="btn-icon ${post.likedByCurrentUser ? 'liked' : ''}"
                                            id="like-btn-${post.id}" onclick="handleLikeClick(${post.id})">
                                        <i class="bi ${post.likedByCurrentUser ? 'bi-heart-fill' : 'bi-heart'}"></i>
                                    </button>
                                    <span class="small fw-semibold text-muted" id="like-count-${post.id}">${post.likeCount} likes</span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<script>
    function handleLikeClick(postId) {
        const btn = document.getElementById('like-btn-' + postId);
        const countSpan = document.getElementById('like-count-' + postId);

        fetch(window.APP_CONTEXT + '/post/like', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'postId=' + encodeURIComponent(postId)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const icon = btn.querySelector('i');
                if (data.liked) {
                    btn.classList.add('liked');
                    icon.classList.remove('bi-heart');
                    icon.classList.add('bi-heart-fill');
                } else {
                    btn.classList.remove('liked');
                    icon.classList.remove('bi-heart-fill');
                    icon.classList.add('bi-heart');
                }
                countSpan.textContent = data.likeCount + ' likes';
            }
        })
        .catch(err => console.error('Like error:', err));
    }
</script>

<jsp:include page="common/footer.jsp" />
