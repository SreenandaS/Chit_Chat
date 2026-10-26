package com.chitchat.servlet;

import com.chitchat.dao.MessageDAO;
import com.chitchat.dao.NotificationDAO;
import com.chitchat.dao.UserDAO;
import com.chitchat.model.Message;
import com.chitchat.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/messages", "/messages/send", "/messages/fetch"})
public class MessageServlet extends HttpServlet {

    private final MessageDAO messageDAO = new MessageDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/messages/fetch".equals(path)) {
            handleFetchMessages(req, resp, currentUser);
            return;
        }

        // Standard page load: /messages
        List<User> partners = messageDAO.getRecentChatPartners(currentUser.getId());

        // Also include all users in directory so user can start a conversation with anyone
        List<User> allUsers = userDAO.getAllUsers();
        allUsers.removeIf(u -> u.getId() == currentUser.getId());

        String userIdParam = req.getParameter("userId");
        User activeUser = null;

        if (userIdParam != null && !userIdParam.trim().isEmpty()) {
            try {
                int targetId = Integer.parseInt(userIdParam.trim());
                activeUser = userDAO.getUserById(targetId);
            } catch (NumberFormatException ignored) {}
        } else if (!partners.isEmpty()) {
            activeUser = partners.get(0);
        } else if (!allUsers.isEmpty()) {
            activeUser = allUsers.get(0);
        }

        List<Message> conversation = null;
        if (activeUser != null) {
            conversation = messageDAO.getConversation(currentUser.getId(), activeUser.getId());
            messageDAO.markConversationAsRead(currentUser.getId(), activeUser.getId());
        }

        int unreadCount = notificationDAO.getUnreadCount(currentUser.getId());

        req.setAttribute("partners", partners);
        req.setAttribute("allUsers", allUsers);
        req.setAttribute("activeUser", activeUser);
        req.setAttribute("conversation", conversation);
        req.setAttribute("unreadCount", unreadCount);

        req.getRequestDispatcher("/WEB-INF/views/messages.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String receiverIdStr = req.getParameter("receiverId");
        String messageText = req.getParameter("messageText");

        boolean success = false;
        int receiverId = -1;

        if (receiverIdStr != null && messageText != null && !messageText.trim().isEmpty()) {
            try {
                receiverId = Integer.parseInt(receiverIdStr);
                int msgId = messageDAO.sendMessage(currentUser.getId(), receiverId, messageText.trim());
                if (msgId > 0) {
                    success = true;
                    notificationDAO.createNotification(
                            receiverId,
                            currentUser.getId(),
                            "MESSAGE",
                            null,
                            currentUser.getFullName() + " sent you a message"
                    );
                }
            } catch (NumberFormatException ignored) {}
        }

        String isAjax = req.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(isAjax) || req.getContentType() != null && req.getContentType().contains("json")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            JsonObject json = new JsonObject();
            json.addProperty("success", success);
            resp.getWriter().write(json.toString());
        } else {
            resp.sendRedirect(req.getContextPath() + "/messages" + (receiverId > 0 ? "?userId=" + receiverId : ""));
        }
    }

    private void handleFetchMessages(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String userIdParam = req.getParameter("userId");
        JsonArray array = new JsonArray();

        if (userIdParam != null) {
            try {
                int otherUserId = Integer.parseInt(userIdParam.trim());
                List<Message> messages = messageDAO.getConversation(currentUser.getId(), otherUserId);
                messageDAO.markConversationAsRead(currentUser.getId(), otherUserId);

                for (Message m : messages) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", m.getId());
                    item.addProperty("senderId", m.getSenderId());
                    item.addProperty("receiverId", m.getReceiverId());
                    item.addProperty("senderName", m.getSenderFullName());
                    item.addProperty("senderPic", m.getSenderPic());
                    item.addProperty("messageText", m.getMessageText());
                    item.addProperty("formattedTime", m.getFormattedTime());
                    item.addProperty("isMine", m.getSenderId() == currentUser.getId());
                    array.add(item);
                }
            } catch (NumberFormatException ignored) {}
        }

        resp.getWriter().write(array.toString());
    }
}
