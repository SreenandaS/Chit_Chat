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

@WebServlet(urlPatterns = {"/profile", "/profile/edit"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/profile/edit".equals(path)) {
            User fresh = userDAO.getUserById(currentUser.getId());
            req.setAttribute("user", fresh != null ? fresh : currentUser);
            req.getRequestDispatcher("/WEB-INF/views/edit-profile.jsp").forward(req, resp);
            return;
        }

        // View profile
        String usernameParam = req.getParameter("username");
        String idParam = req.getParameter("id");
        User profileUser = null;

        if (usernameParam != null && !usernameParam.trim().isEmpty()) {
            profileUser = userDAO.getUserByUsername(usernameParam.trim());
        } else if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                profileUser = userDAO.getUserById(Integer.parseInt(idParam.trim()));
            } catch (NumberFormatException ignored) {}
        } else {
            profileUser = userDAO.getUserById(currentUser.getId());
        }

        if (profileUser == null) {
            resp.sendRedirect(req.getContextPath() + "/feed");
            return;
        }

        boolean isOwner = (profileUser.getId() == currentUser.getId());
        List<Post> posts = postDAO.getPostsByUserId(profileUser.getId(), currentUser.getId());
        int unreadCount = notificationDAO.getUnreadCount(currentUser.getId());

        req.setAttribute("profileUser", profileUser);
        req.setAttribute("posts", posts);
        req.setAttribute("isOwner", isOwner);
        req.setAttribute("unreadCount", unreadCount);

        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullName = req.getParameter("fullName");
        String bio = req.getParameter("bio");
        String profilePic = req.getParameter("profilePic");

        if (fullName != null && !fullName.trim().isEmpty()) {
            userDAO.updateProfile(currentUser.getId(), fullName.trim(), bio, profilePic);
            User updated = userDAO.getUserById(currentUser.getId());
            if (updated != null) {
                session.setAttribute("currentUser", updated);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
