/**
 * ChitChat - Social Networking Platform
 * Client-side JavaScript enhancements (AJAX Likes, Polling, UI Helpers)
 */

document.addEventListener('DOMContentLoaded', () => {
    initNotificationBadgePolling();
});

/**
 * Polls for new unread notifications periodically (every 10 seconds)
 */
function initNotificationBadgePolling() {
    if (!window.APP_CONTEXT) return;

    function checkUnreadCount() {
        fetch(window.APP_CONTEXT + '/notifications/count')
            .then(res => {
                if (res.ok) return res.json();
                throw new Error('Not logged in or network error');
            })
            .then(data => {
                const badge = document.getElementById('navNotifBadge');
                if (badge) {
                    if (data.unreadCount > 0) {
                        badge.textContent = data.unreadCount;
                        badge.style.display = 'inline-block';
                    } else {
                        badge.style.display = 'none';
                    }
                }
            })
            .catch(() => {});
    }

    // Check every 10 seconds
    setInterval(checkUnreadCount, 10000);
}

/**
 * Global helper to show a toast message
 */
function showToast(message, isError = false) {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-white ${isError ? 'bg-danger' : 'bg-dark'} border-0 shadow-lg`;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');
    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    container.appendChild(toastEl);
    if (window.bootstrap && window.bootstrap.Toast) {
        const bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
        bsToast.show();
    }
}
