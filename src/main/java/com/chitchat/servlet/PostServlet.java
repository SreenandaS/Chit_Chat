package com.chitchat.servlet;

import com.chitchat.dao.LikeDAO;
import com.chitchat.dao.NotificationDAO;
import com.chitchat.dao.PostDAO;
import com.chitchat.model.Post;
import com.chitchat.model.User;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/post/create", "/post/delete", "/post/like"})
public class PostServlet extends HttpServlet {

    private final PostDAO postDAO = new PostDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/post/create".equals(path)) {
            handleCreatePost(req, resp, currentUser);
        } else if ("/post/delete".equals(path)) {
            handleDeletePost(req, resp, currentUser);
        } else if ("/post/like".equals(path)) {
            handleLikePost(req, resp, currentUser);
        }
    }

    private void handleCreatePost(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws IOException {
        String content = req.getParameter("content");
        String imageUrl = req.getParameter("imageUrl");

        if (content != null && !content.trim().isEmpty()) {
            postDAO.createPost(currentUser.getId(), content.trim(), imageUrl);
        }

        resp.sendRedirect(req.getContextPath() + "/feed");
    }

    private void handleDeletePost(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws IOException {
        String postIdStr = req.getParameter("postId");
        if (postIdStr != null) {
            try {
                int postId = Integer.parseInt(postIdStr);
                postDAO.deletePost(postId, currentUser.getId(), currentUser.isAdmin());
            } catch (NumberFormatException ignored) {}
        }

        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            resp.sendRedirect(referer);
        } else {
            resp.sendRedirect(req.getContextPath() + "/feed");
        }
    }

    private void handleLikePost(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject json = new JsonObject();

        String postIdStr = req.getParameter("postId");
        if (postIdStr != null) {
            try {
                int postId = Integer.parseInt(postIdStr);
                boolean liked = likeDAO.toggleLike(postId, currentUser.getId());
                int count = likeDAO.getLikeCount(postId);

                // If liked, notify post author
                if (liked) {
                    Post post = postDAO.getPostById(postId, currentUser.getId());
                    if (post != null && post.getUserId() != currentUser.getId()) {
                        notificationDAO.createNotification(
                                post.getUserId(),
                                currentUser.getId(),
                                "LIKE",
                                postId,
                                currentUser.getFullName() + " liked your post"
                        );
                    }
                }

                json.addProperty("success", true);
                json.addProperty("liked", liked);
                json.addProperty("likeCount", count);
            } catch (Exception e) {
                json.addProperty("success", false);
                json.addProperty("error", e.getMessage());
            }
        } else {
            json.addProperty("success", false);
            json.addProperty("error", "Missing postId");
        }

        resp.getWriter().write(json.toString());
    }
}
