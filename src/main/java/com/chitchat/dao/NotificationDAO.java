package com.chitchat.dao;

import com.chitchat.model.Notification;
import com.chitchat.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void createNotification(int userId, int actorId, String type, Integer postId, String message) {
        if (userId == actorId) {
            return; // Don't notify oneself
        }
        String sql = "INSERT INTO notifications (user_id, actor_id, type, post_id, message, is_read) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, actorId);
            ps.setString(3, type);
            if (postId != null) {
                ps.setInt(4, postId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotificationsForUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.username AS actor_username, u.full_name AS actor_name, u.profile_pic AS actor_pic "
                + "FROM notifications n "
                + "JOIN users u ON n.actor_id = u.id "
                + "WHERE n.user_id = ? "
                + "ORDER BY n.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification notif = new Notification();
                    notif.setId(rs.getInt("id"));
                    notif.setUserId(rs.getInt("user_id"));
                    notif.setActorId(rs.getInt("actor_id"));
                    notif.setActorUsername(rs.getString("actor_username"));
                    notif.setActorFullName(rs.getString("actor_name"));
                    notif.setActorPic(rs.getString("actor_pic"));
                    notif.setType(rs.getString("type"));
                    int pid = rs.getInt("post_id");
                    if (!rs.wasNull()) {
                        notif.setPostId(pid);
                    }
                    notif.setMessage(rs.getString("message"));
                    notif.setRead(rs.getInt("is_read") == 1);
                    notif.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(notif);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
