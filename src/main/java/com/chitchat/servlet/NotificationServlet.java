package com.chitchat.servlet;

import com.chitchat.dao.NotificationDAO;
import com.chitchat.model.Notification;
import com.chitchat.model.User;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/notifications", "/notifications/count"})
public class NotificationServlet extends HttpServlet {

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

        if ("/notifications/count".equals(path)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            int count = notificationDAO.getUnreadCount(currentUser.getId());
            JsonObject json = new JsonObject();
            json.addProperty("unreadCount", count);
            resp.getWriter().write(json.toString());
            return;
        }

        List<Notification> notifications = notificationDAO.getNotificationsForUser(currentUser.getId());
        notificationDAO.markAllAsRead(currentUser.getId());

        req.setAttribute("notifications", notifications);
        req.setAttribute("unreadCount", 0);

        req.getRequestDispatcher("/WEB-INF/views/notifications.jsp").forward(req, resp);
    }
}
