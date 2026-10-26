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

@WebServlet(urlPatterns = {"/feed", "/home"})
public class FeedServlet extends HttpServlet {

    private final PostDAO postDAO = new PostDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Refresh user info
        User refreshed = userDAO.getUserById(currentUser.getId());
        if (refreshed != null) {
            session.setAttribute("currentUser", refreshed);
            currentUser = refreshed;
        }

        final int currentUserId = currentUser.getId();
        List<Post> posts = postDAO.getFeedPosts(currentUserId);
        int unreadCount = notificationDAO.getUnreadCount(currentUserId);

        // Suggested users to follow/chat
        List<User> suggestedUsers = userDAO.searchUsers("");
        suggestedUsers.removeIf(u -> u.getId() == currentUserId);
        if (suggestedUsers.size() > 5) {
            suggestedUsers = suggestedUsers.subList(0, 5);
        }

        req.setAttribute("posts", posts);
        req.setAttribute("unreadCount", unreadCount);
        req.setAttribute("suggestedUsers", suggestedUsers);

        req.getRequestDispatcher("/WEB-INF/views/feed.jsp").forward(req, resp);
    }
}
