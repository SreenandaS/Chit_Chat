<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Direct Messages - ChitChat" />
</jsp:include>

<div class="app-layout">
    <!-- Sidebar -->
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="active" value="messages" />
    </jsp:include>

    <!-- Main Content Area -->
    <main class="main-wrapper" style="padding: 20px;">
        <div class="container-fluid" style="max-width: 980px;">
            <div class="chat-window">

                <!-- Left Sidebar: Conversations & Contacts -->
                <div class="chat-sidebar">
                    <div class="chat-sidebar-header d-flex justify-content-between align-items-center">
                        <span class="fw-bold fs-6">Messages</span>
                        <a href="${pageContext.request.contextPath}/search" class="text-muted text-decoration-none small" title="Find new people">
                            <i class="bi bi-person-plus fs-5"></i>
                        </a>
                    </div>

                    <div class="conversation-list">
                        <c:choose>
                            <c:when test="${empty allUsers}">
                                <div class="p-4 text-center text-muted small">No other registered users yet.</div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="u" items="${allUsers}">
                                    <a href="${pageContext.request.contextPath}/messages?userId=${u.id}"
                                       class="conversation-item ${activeUser != null && activeUser.id == u.id ? 'active' : ''}">
                                        <div class="position-relative">
                                            <img src="${u.profilePic}" alt="${u.fullName}" class="avatar-img avatar-sm">
                                            <span class="online-dot" style="left: 22px; bottom: 0;"></span>
                                        </div>
                                        <div class="overflow-hidden flex-grow-1">
                                            <div class="fw-semibold text-truncate small">${u.fullName}</div>
                                            <div class="text-muted text-truncate" style="font-size: 0.72rem;">@${u.username}</div>
                                        </div>
                                    </a>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Right Pane: Active Chat Conversation -->
                <div class="chat-main">
                    <c:choose>
                        <c:when test="${activeUser == null}">
                            <div class="d-flex flex-column align-items-center justify-content-center h-100 p-4 text-center">
                                <div class="fs-1 mb-2">💬</div>
                                <h6 class="fw-bold">Your Direct Messages</h6>
                                <p class="text-muted small">Select a conversation from the left to start chatting.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Chat Header -->
                            <div class="chat-main-header justify-content-between">
                                <div class="d-flex align-items-center gap-3">
                                    <div class="avatar-ring">
                                        <img src="${activeUser.profilePic}" alt="${activeUser.fullName}" class="avatar-img avatar-sm">
                                    </div>
                                    <div>
                                        <div class="fw-bold small">${activeUser.fullName}</div>
                                        <div class="text-muted" style="font-size: 0.72rem;">@${activeUser.username} • Active now</div>
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/profile?id=${activeUser.id}"
                                   class="btn btn-sm btn-outline-secondary rounded-pill px-3 py-1" style="font-size: 0.75rem;">
                                    View Profile
                                </a>
                            </div>

                            <!-- Messages History Container -->
                            <div class="chat-messages-container" id="chatMessagesContainer">
                                <c:choose>
                                    <c:when test="${empty conversation}">
                                        <div class="text-center text-muted my-auto py-5" id="noMessagesPlaceholder">
                                            <div class="fs-3 mb-1">👋</div>
                                            <p class="small mb-0">Say hi to ${activeUser.fullName}! Send a message below.</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="msg" items="${conversation}">
                                            <div class="message-bubble ${msg.senderId == sessionScope.currentUser.id ? 'mine' : 'theirs'}"
                                                 data-msg-id="${msg.id}">
                                                <div>${msg.messageText}</div>
                                                <div class="text-end" style="font-size: 0.65rem; opacity: 0.8; margin-top: 3px;">
                                                    ${msg.formattedTime}
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Message Input Form -->
                            <div class="p-3 border-top bg-white">
                                <form id="chatForm" class="d-flex gap-2 align-items-center">
                                    <input type="hidden" id="receiverId" name="receiverId" value="${activeUser.id}">
                                    <input type="text" id="messageInput" name="messageText" class="form-control bg-light rounded-pill px-3"
                                           placeholder="Type a message to ${activeUser.fullName}..." autocomplete="off" required>
                                    <button type="submit" class="brand-gradient-btn px-4 py-2 rounded-pill flex-shrink-0">
                                        <i class="bi bi-send-fill"></i>
                                    </button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </div>
    </main>
</div>

<c:if test="${activeUser != null}">
<script>
    const activeUserId = ${activeUser.id};
    const currentUserId = ${sessionScope.currentUser.id};
    const container = document.getElementById('chatMessagesContainer');
    const chatForm = document.getElementById('chatForm');
    const messageInput = document.getElementById('messageInput');

    // Scroll to bottom on initial load
    function scrollToBottom() {
        if (container) {
            container.scrollTop = container.scrollHeight;
        }
    }
    scrollToBottom();

    // Send message via AJAX
    if (chatForm) {
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const text = messageInput.value.trim();
            if (!text) return;

            // Clear input immediately for snappy UX
            messageInput.value = '';

            const params = new URLSearchParams();
            params.append('receiverId', activeUserId);
            params.append('messageText', text);

            fetch(window.APP_CONTEXT + '/messages/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: params.toString()
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    fetchNewMessages();
                }
            })
            .catch(err => console.error('Send error:', err));
        });
    }

    // Polling function for near-real-time updates
    function fetchNewMessages() {
        if (!activeUserId) return;

        fetch(window.APP_CONTEXT + '/messages/fetch?userId=' + encodeURIComponent(activeUserId))
        .then(res => res.json())
        .then(messages => {
            if (!container) return;

            // Remove placeholder if exists
            const placeholder = document.getElementById('noMessagesPlaceholder');
            if (placeholder && messages.length > 0) {
                placeholder.remove();
            }

            // Check if count or content changed
            const existingBubbles = container.querySelectorAll('.message-bubble');
            if (existingBubbles.length !== messages.length) {
                let html = '';
                messages.forEach(m => {
                    const bubbleClass = m.isMine ? 'mine' : 'theirs';
                    html += '<div class="message-bubble ' + bubbleClass + '" data-msg-id="' + m.id + '">';
                    html += '<div>' + escapeHtml(m.messageText) + '</div>';
                    html += '<div class="text-end" style="font-size: 0.65rem; opacity: 0.8; margin-top: 3px;">' + m.formattedTime + '</div>';
                    html += '</div>';
                });
                container.innerHTML = html;
                scrollToBottom();
            }
        })
        .catch(err => console.error('Polling error:', err));
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Poll every 2.5 seconds
    const pollingInterval = setInterval(fetchNewMessages, 2500);

    // Clean up interval if navigating away
    window.addEventListener('beforeunload', function() {
        clearInterval(pollingInterval);
    });
</script>
</c:if>

<jsp:include page="common/footer.jsp" />
