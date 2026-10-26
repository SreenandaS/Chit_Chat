package com.chitchat.servlet;

import com.chitchat.dao.NotificationDAO;
import com.chitchat.dao.PostDAO;
import com.chitchat.dao.UserDAO;
import com.chitchat.model.Post;
import com.chitchat.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin", "/admin/delete-post", "/admin/delete-user"})
public class AdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin privileges required.");
            return;
        }

        List<User> users = userDAO.getAllUsers();
        List<Post> posts = postDAO.getAllPostsForAdmin();
        int unreadCount = notificationDAO.getUnreadCount(currentUser.getId());

        int totalLikes = 0;
        for (Post p : posts) {
            totalLikes += p.getLikeCount();
        }

        req.setAttribute("users", users);
        req.setAttribute("posts", posts);
        req.setAttribute("totalUsers", users.size());
        req.setAttribute("totalPosts", posts.size());
        req.setAttribute("totalLikes", totalLikes);
        req.setAttribute("unreadCount", unreadCount);

        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin privileges required.");
            return;
        }

        String path = req.getServletPath();

        if ("/admin/delete-post".equals(path)) {
            String postIdStr = req.getParameter("postId");
            if (postIdStr != null) {
                try {
                    int postId = Integer.parseInt(postIdStr);
                    postDAO.deletePost(postId, currentUser.getId(), true);
                } catch (NumberFormatException ignored) {}
            }
        } else if ("/admin/delete-user".equals(path)) {
            String userIdStr = req.getParameter("userId");
            if (userIdStr != null) {
                try {
                    int targetUserId = Integer.parseInt(userIdStr);
                    if (targetUserId != currentUser.getId()) {
                        userDAO.deleteUser(targetUserId);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}
