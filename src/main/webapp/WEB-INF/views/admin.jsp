<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="admin" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper" style="padding: 24px;">
        <div class="container-fluid" style="max-width: 1050px;">

            <!-- Header & Badge -->
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
                <div>
                    <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
                        <i class="bi bi-shield-lock-fill text-danger"></i> ChitChat Admin Console
                    </h4>
                    <p class="text-muted small mb-0">Platform overview, moderation, and user management</p>
                </div>
                <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-3 py-2 rounded-pill fw-semibold">
                    Logged in as Administrator (${sessionScope.currentUser.username})
                </span>
            </div>

            <!-- Stats KPI Cards -->
            <div class="row g-3 mb-4">
                <div class="col-sm-4">
                    <div class="card border-0 shadow-sm p-3 rounded-4 bg-white">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <div class="text-muted small fw-semibold text-uppercase">Total Users</div>
                                <h3 class="fw-bold mb-0 mt-1">${totalUsers}</h3>
                            </div>
                            <div class="rounded-circle bg-primary-subtle text-primary p-3 fs-4 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                                <i class="bi bi-people-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-4">
                    <div class="card border-0 shadow-sm p-3 rounded-4 bg-white">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <div class="text-muted small fw-semibold text-uppercase">Total Posts</div>
                                <h3 class="fw-bold mb-0 mt-1">${totalPosts}</h3>
                            </div>
                            <div class="rounded-circle bg-success-subtle text-success p-3 fs-4 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                                <i class="bi bi-card-image"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-4">
                    <div class="card border-0 shadow-sm p-3 rounded-4 bg-white">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <div class="text-muted small fw-semibold text-uppercase">Likes Recorded</div>
                                <h3 class="fw-bold mb-0 mt-1">${totalLikes}</h3>
                            </div>
                            <div class="rounded-circle bg-danger-subtle text-danger p-3 fs-4 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                                <i class="bi bi-heart-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Navigation Tabs -->
            <ul class="nav nav-pills mb-3 gap-2" id="adminTabs" role="tablist">
                <li class="nav-item">
                    <button class="nav-link active rounded-pill px-4" id="posts-tab" data-bs-toggle="tab" data-bs-target="#postsPane">
                        <i class="bi bi-card-text me-1"></i> Content Moderation (${posts.size()} Posts)
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link rounded-pill px-4" id="users-tab" data-bs-toggle="tab" data-bs-target="#usersPane">
                        <i class="bi bi-person-lines-fill me-1"></i> Registered Users (${users.size()})
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="adminTabsContent">

                <!-- Tab 1: Posts Moderation -->
                <div class="tab-pane fade show active" id="postsPane">
                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-uppercase">
                                    <tr>
                                        <th class="ps-3" style="width: 60px;">ID</th>
                                        <th>Author</th>
                                        <th>Post Content</th>
                                        <th>Image</th>
                                        <th>Likes</th>
                                        <th>Timestamp</th>
                                        <th class="text-end pe-3">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${posts}">
                                        <tr>
                                            <td class="ps-3 fw-bold text-muted">#${p.id}</td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <img src="${p.authorPic}" alt="Author" class="avatar-img avatar-xs">
                                                    <div>
                                                        <div class="fw-semibold small">${p.authorFullName}</div>
                                                        <div class="text-muted" style="font-size: 0.72rem;">@${p.authorUsername}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="small text-truncate" style="max-width: 300px;" title="${p.content}">
                                                    ${p.content}
                                                </div>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty p.imageUrl}">
                                                        <a href="${p.imageUrl}" target="_blank" class="badge bg-secondary-subtle text-secondary text-decoration-none">
                                                            <i class="bi bi-image"></i> View
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted small">-</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><span class="badge bg-light text-dark border">${p.likeCount}</span></td>
                                            <td class="small text-muted">${p.formattedDate}</td>
                                            <td class="text-end pe-3">
                                                <form action="${pageContext.request.contextPath}/admin/delete-post" method="POST"
                                                      onsubmit="return confirm('Admin Action: Permanently delete post #${p.id}?');" class="d-inline">
                                                    <input type="hidden" name="postId" value="${p.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger py-1 px-2 rounded-pill small" title="Delete inappropriate post">
                                                        <i class="bi bi-trash"></i> Delete
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Tab 2: Registered Users -->
                <div class="tab-pane fade" id="usersPane">
                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-uppercase">
                                    <tr>
                                        <th class="ps-3" style="width: 60px;">ID</th>
                                        <th>User</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                        <th>Posts</th>
                                        <th>Likes</th>
                                        <th class="text-end pe-3">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${users}">
                                        <tr>
                                            <td class="ps-3 fw-bold text-muted">#${u.id}</td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <img src="${u.profilePic}" alt="User" class="avatar-img avatar-xs">
                                                    <div>
                                                        <div class="fw-semibold small">${u.fullName}</div>
                                                        <div class="text-muted" style="font-size: 0.72rem;">@${u.username}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="small">${u.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.admin}">
                                                        <span class="badge bg-danger rounded-pill px-2">Administrator</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-light text-secondary border rounded-pill px-2">Member</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="small fw-semibold">${u.postCount}</td>
                                            <td class="small fw-semibold">${u.likeCount}</td>
                                            <td class="text-end pe-3">
                                                <c:choose>
                                                    <c:when test="${u.id == sessionScope.currentUser.id}">
                                                        <span class="badge bg-light text-muted border">You</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form action="${pageContext.request.contextPath}/admin/delete-user" method="POST"
                                                              onsubmit="return confirm('Admin Action: Permanently delete user @${u.username}?');" class="d-inline">
                                                            <input type="hidden" name="userId" value="${u.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger py-1 px-2 rounded-pill small" title="Delete User">
                                                                <i class="bi bi-person-x"></i> Delete
                                                            </button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </main>
</div>

<jsp:include page="common/footer.jsp" />
