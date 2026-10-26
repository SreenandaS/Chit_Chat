package com.chitchat.servlet;

import com.chitchat.dao.NotificationDAO;
import com.chitchat.dao.UserDAO;
import com.chitchat.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String query = req.getParameter("q");
        List<User> results = userDAO.searchUsers(query != null ? query : "");
        int unreadCount = notificationDAO.getUnreadCount(currentUser.getId());

        req.setAttribute("searchResults", results);
        req.setAttribute("searchQuery", query != null ? query : "");
        req.setAttribute("unreadCount", unreadCount);

        req.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(req, resp);
    }
}
