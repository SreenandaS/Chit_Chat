package com.chitchat.dao;

import com.chitchat.model.Message;
import com.chitchat.model.User;
import com.chitchat.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public int sendMessage(int senderId, int receiverId, String text) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_text, is_read) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, text.trim());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Message> getConversation(int user1Id, int user2Id) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, "
                + "s.username AS sender_username, s.full_name AS sender_name, s.profile_pic AS sender_pic, "
                + "r.username AS receiver_username "
                + "FROM messages m "
                + "JOIN users s ON m.sender_id = s.id "
                + "JOIN users r ON m.receiver_id = r.id "
                + "WHERE (m.sender_id = ? AND m.receiver_id = ?) "
                + "   OR (m.sender_id = ? AND m.receiver_id = ?) "
                + "ORDER BY m.created_at ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user1Id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message();
                    m.setId(rs.getInt("id"));
                    m.setSenderId(rs.getInt("sender_id"));
                    m.setReceiverId(rs.getInt("receiver_id"));
                    m.setSenderUsername(rs.getString("sender_username"));
                    m.setSenderFullName(rs.getString("sender_name"));
                    m.setSenderPic(rs.getString("sender_pic"));
                    m.setReceiverUsername(rs.getString("receiver_username"));
                    m.setMessageText(rs.getString("message_text"));
                    m.setRead(rs.getInt("is_read") == 1);
                    m.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markConversationAsRead(int currentUserId, int otherUserId) {
        String sql = "UPDATE messages SET is_read = 1 WHERE sender_id = ? AND receiver_id = ? AND is_read = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, otherUserId);
            ps.setInt(2, currentUserId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> getRecentChatPartners(int currentUserId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u "
                + "JOIN messages m ON (u.id = m.sender_id AND m.receiver_id = ?) "
                + "                OR (u.id = m.receiver_id AND m.sender_id = ?) "
                + "WHERE u.id != ? "
                + "ORDER BY u.full_name ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setInt(3, currentUserId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setFullName(rs.getString("full_name"));
                    u.setBio(rs.getString("bio"));
                    u.setProfilePic(rs.getString("profile_pic"));
                    u.setAdmin(rs.getInt("is_admin") == 1);
                    users.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}
