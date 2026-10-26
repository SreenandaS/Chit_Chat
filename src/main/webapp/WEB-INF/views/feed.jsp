<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Home Feed - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="feed" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper">
        <div class="feed-container">

            <!-- Create Post Card -->
            <div class="card border-0 shadow-sm p-3 mb-4 rounded-4">
                <form action="${pageContext.request.contextPath}/post/create" method="POST" id="createPostForm">
                    <div class="d-flex gap-3 align-items-start">
                        <img src="${sessionScope.currentUser.profilePic}" alt="User Avatar" class="avatar-img">
                        <div class="flex-grow-1">
                            <textarea name="content" class="form-control border-0 bg-light rounded-3 p-3" rows="3"
                                      placeholder="What's happening, ${sessionScope.currentUser.fullName}? ✨" required></textarea>

                            <div class="collapse mt-2" id="imageUrlCollapse">
                                <input type="url" name="imageUrl" id="postImageUrlInput" class="form-control form-control-sm bg-light"
                                       placeholder="Paste image URL (https://images.unsplash.com/...)">
                                <div class="d-flex gap-1 mt-1 align-items-center">
                                    <span class="text-muted" style="font-size: 0.72rem;">Quick Samples:</span>
                                    <button type="button" class="btn btn-sm btn-link p-0 text-decoration-none" style="font-size: 0.72rem;"
                                            onclick="setImageUrl('https://images.unsplash.com/photo-1518770660439-4636190af475?w=800')">Tech</button>
                                    <span class="text-muted">•</span>
                                    <button type="button" class="btn btn-sm btn-link p-0 text-decoration-none" style="font-size: 0.72rem;"
                                            onclick="setImageUrl('https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800')">Nature</button>
                                    <span class="text-muted">•</span>
                                    <button type="button" class="btn btn-sm btn-link p-0 text-decoration-none" style="font-size: 0.72rem;"
                                            onclick="setImageUrl('https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800')">Beach</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mt-3 pt-2 border-top">
                        <button type="button" class="btn btn-sm btn-outline-secondary rounded-pill px-3"
                                data-bs-toggle="collapse" data-bs-target="#imageUrlCollapse">
                            <i class="bi bi-image text-success me-1"></i> Add Photo
                        </button>

                        <button type="submit" class="brand-gradient-btn py-1 px-4 small">
                            Share Post <i class="bi bi-send-fill ms-1"></i>
                        </button>
                    </div>
                </form>
            </div>

            <!-- Posts Feed -->
            <c:choose>
                <c:when test="${empty posts}">
                    <div class="card border-0 shadow-sm p-5 text-center rounded-4">
                        <div class="fs-1 mb-2">📸</div>
                        <h5 class="fw-bold">No posts in the feed yet!</h5>
                        <p class="text-muted small">Be the first to share a post and start the conversation.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="post" items="${posts}">
                        <div class="card border-0 shadow-sm mb-4 rounded-4 overflow-hidden" id="post-${post.id}">
                            <!-- Post Header -->
                            <div class="p-3 d-flex align-items-center justify-content-between">
                                <a href="${pageContext.request.contextPath}/profile?id=${post.userId}"
                                   class="d-flex align-items-center gap-2 text-decoration-none text-dark">
                                    <img src="${post.authorPic}" alt="${post.authorFullName}" class="avatar-img">
                                    <div>
                                        <div class="fw-semibold small">${post.authorFullName}</div>
                                        <div class="text-muted" style="font-size: 0.75rem;">@${post.authorUsername} • ${post.formattedDate}</div>
                                    </div>
                                </a>

                                <c:if test="${post.userId == sessionScope.currentUser.id || sessionScope.currentUser.admin}">
                                    <div class="dropdown">
                                        <button class="btn btn-sm btn-link text-muted p-0" data-bs-toggle="dropdown">
                                            <i class="bi bi-three-dots"></i>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end border-0 shadow-sm">
                                            <li>
                                                <form action="${pageContext.request.contextPath}/post/delete" method="POST"
                                                      onsubmit="return confirm('Delete this post?');">
                                                    <input type="hidden" name="postId" value="${post.id}">
                                                    <button type="submit" class="dropdown-item text-danger small">
                                                        <i class="bi bi-trash me-2"></i> Delete Post
                                                    </button>
                                                </form>
                                            </li>
                                        </ul>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Post Text Content -->
                            <div class="px-3 pb-2">
                                <p class="mb-0 text-dark" style="white-space: pre-wrap; word-break: break-word;">${post.content}</p>
                            </div>

                            <!-- Post Image (if present) -->
                            <c:if test="${not empty post.imageUrl}">
                                <div class="mt-2 bg-light text-center">
                                    <img src="${post.imageUrl}" alt="Post Media" class="img-fluid w-100"
                                         style="max-height: 480px; object-fit: cover;" onerror="this.style.display='none'">
                                </div>
                            </c:if>

                            <!-- Post Actions -->
                            <div class="post-actions px-3 py-2 border-top mt-2 d-flex align-items-center justify-content-between">
                                <div class="d-flex align-items-center gap-3">
                                    <button type="button" class="btn-icon ${post.likedByCurrentUser ? 'liked' : ''}"
                                            id="like-btn-${post.id}" onclick="handleLikeClick(${post.id})">
                                        <i class="bi ${post.likedByCurrentUser ? 'bi-heart-fill' : 'bi-heart'}"></i>
                                    </button>
                                    <span class="small fw-semibold text-muted" id="like-count-${post.id}">${post.likeCount} likes</span>

                                    <a href="${pageContext.request.contextPath}/messages?userId=${post.userId}" class="btn-icon text-muted text-decoration-none">
                                        <i class="bi bi-chat"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Right Sidebar (Desktop) -->
        <div class="sidebar-right d-none d-lg-block">
            <!-- User Profile Summary Card -->
            <div class="card border-0 shadow-sm p-3 rounded-4 mb-4">
                <div class="d-flex align-items-center gap-3">
                    <div class="avatar-ring">
                        <img src="${sessionScope.currentUser.profilePic}" alt="User" class="avatar-img" style="width: 50px; height: 50px;">
                    </div>
                    <div>
                        <div class="fw-bold">${sessionScope.currentUser.fullName}</div>
                        <div class="text-muted small">@${sessionScope.currentUser.username}</div>
                    </div>
                </div>
                <div class="d-flex justify-content-around text-center mt-3 pt-3 border-top">
                    <div>
                        <div class="fw-bold fs-5">${sessionScope.currentUser.postCount}</div>
                        <div class="text-muted" style="font-size: 0.75rem;">Posts</div>
                    </div>
                    <div>
                        <div class="fw-bold fs-5">${sessionScope.currentUser.likeCount}</div>
                        <div class="text-muted" style="font-size: 0.75rem;">Likes</div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/profile" class="btn btn-sm btn-outline-secondary rounded-pill mt-3 w-100">
                    View My Profile
                </a>
            </div>

            <!-- Suggested People Card -->
            <c:if test="${not empty suggestedUsers}">
                <div class="card border-0 shadow-sm p-3 rounded-4">
                    <div class="fw-bold text-dark small mb-3">Suggested for you</div>
                    <div class="d-flex flex-direction-column gap-3 flex-column">
                        <c:forEach var="su" items="${suggestedUsers}">
                            <div class="d-flex align-items-center justify-content-between">
                                <a href="${pageContext.request.contextPath}/profile?id=${su.id}" class="d-flex align-items-center gap-2 text-decoration-none text-dark">
                                    <img src="${su.profilePic}" alt="${su.fullName}" class="avatar-img avatar-sm">
                                    <div class="overflow-hidden" style="max-width: 130px;">
                                        <div class="fw-semibold text-truncate small">${su.fullName}</div>
                                        <div class="text-muted text-truncate" style="font-size: 0.72rem;">@${su.username}</div>
                                    </div>
                                </a>
                                <a href="${pageContext.request.contextPath}/messages?userId=${su.id}" class="btn btn-sm btn-light rounded-pill py-0 px-2 small">
                                    Chat
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>
    </main>
</div>

<script>
    function setImageUrl(url) {
        document.getElementById('postImageUrlInput').value = url;
    }

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
