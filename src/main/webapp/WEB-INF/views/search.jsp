<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Search Users - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="search" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper">
        <div class="feed-container" style="max-width: 620px;">

            <!-- Search Form Card -->
            <div class="card border-0 shadow-sm p-4 rounded-4 mb-4">
                <h5 class="fw-bold mb-3">Discover People</h5>
                <form action="${pageContext.request.contextPath}/search" method="GET" class="d-flex gap-2">
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
                        <input type="text" name="q" class="form-control bg-light border-start-0"
                               placeholder="Search by name or @username..." value="${searchQuery}" autofocus>
                    </div>
                    <button type="submit" class="brand-gradient-btn px-4 py-2">
                        Search
                    </button>
                </form>
            </div>

            <!-- Search Results List -->
            <c:choose>
                <c:when test="${empty searchResults}">
                    <div class="card border-0 shadow-sm p-5 text-center rounded-4">
                        <div class="fs-1 mb-2">🔍</div>
                        <h6 class="fw-bold">No users found</h6>
                        <p class="text-muted small">Try searching for a different name or username.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                        <div class="p-3 border-bottom bg-light">
                            <span class="small fw-semibold text-muted text-uppercase" style="letter-spacing: 0.5px;">
                                Found ${searchResults.size()} user${searchResults.size() == 1 ? '' : 's'}
                            </span>
                        </div>

                        <div class="list-group list-group-flush">
                            <c:forEach var="u" items="${searchResults}">
                                <div class="list-group-item p-3 d-flex align-items-center justify-content-between">
                                    <a href="${pageContext.request.contextPath}/profile?id=${u.id}"
                                       class="d-flex align-items-center gap-3 text-decoration-none text-dark flex-grow-1">
                                        <div class="avatar-ring">
                                            <img src="${u.profilePic}" alt="${u.fullName}" class="avatar-img" style="width: 48px; height: 48px;">
                                        </div>
                                        <div class="overflow-hidden">
                                            <div class="fw-bold small d-flex align-items-center gap-1">
                                                ${u.fullName}
                                                <c:if test="${u.admin}">
                                                    <span class="badge bg-danger rounded-pill" style="font-size: 0.65rem;">Admin</span>
                                                </c:if>
                                            </div>
                                            <div class="text-muted" style="font-size: 0.78rem;">@${u.username}</div>
                                            <c:if test="${not empty u.bio}">
                                                <div class="text-secondary text-truncate small mt-1" style="max-width: 320px;">${u.bio}</div>
                                            </c:if>
                                        </div>
                                    </a>

                                    <div class="d-flex gap-2 ms-2">
                                        <c:if test="${u.id != sessionScope.currentUser.id}">
                                            <a href="${pageContext.request.contextPath}/messages?userId=${u.id}"
                                               class="btn btn-sm btn-outline-secondary rounded-pill px-3">
                                                <i class="bi bi-chat-dots-fill me-1"></i> Chat
                                            </a>
                                        </c:if>
                                        <a href="${pageContext.request.contextPath}/profile?id=${u.id}"
                                           class="btn btn-sm btn-light rounded-pill px-3">
                                            Profile
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<jsp:include page="common/footer.jsp" />
