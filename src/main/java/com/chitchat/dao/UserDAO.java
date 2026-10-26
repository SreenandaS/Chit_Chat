package com.chitchat.dao;

import com.chitchat.model.User;
import com.chitchat.util.DBUtil;
import com.chitchat.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmailTaken(String email) {
        String sql = "SELECT id FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int register(User user) {
        String sql = "INSERT INTO users (username, email, password, full_name, bio, profile_pic, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getEmail().trim());
            ps.setString(3, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(4, user.getFullName().trim());
            ps.setString(5, user.getBio() != null ? user.getBio().trim() : "");
            ps.setString(6, user.getProfilePic());
            ps.setInt(7, user.isAdmin() ? 1 : 0);

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

    public User authenticate(String usernameOrEmail, String plainPassword) {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail.trim());
            ps.setString(2, usernameOrEmail.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                        return mapRowToUser(rs, conn);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs, conn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs, conn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(int userId, String fullName, String bio, String profilePic) {
        String sql = "UPDATE users SET full_name = ?, bio = ?, profile_pic = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, bio != null ? bio.trim() : "");
            ps.setString(3, (profilePic != null && !profilePic.trim().isEmpty()) ? profilePic.trim() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> searchUsers(String query) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE LOWER(username) LIKE LOWER(?) OR LOWER(full_name) LIKE LOWER(?) ORDER BY username ASC LIMIT 30";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + (query != null ? query.trim() : "") + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs, conn));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToUser(rs, conn));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapRowToUser(ResultSet rs, Connection conn) throws Exception {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setBio(rs.getString("bio"));
        u.setProfilePic(rs.getString("profile_pic"));
        u.setAdmin(rs.getInt("is_admin") == 1);
        u.setCreatedAt(rs.getTimestamp("created_at"));

        // Fetch user stats: posts count & total likes received
        try (PreparedStatement psCount = conn.prepareStatement("SELECT COUNT(*) FROM posts WHERE user_id = ?")) {
            psCount.setInt(1, u.getId());
            try (ResultSet rsCount = psCount.executeQuery()) {
                if (rsCount.next()) {
                    u.setPostCount(rsCount.getInt(1));
                }
            }
        }

        try (PreparedStatement psLikes = conn.prepareStatement("SELECT COUNT(*) FROM likes l JOIN posts p ON l.post_id = p.id WHERE p.user_id = ?")) {
            psLikes.setInt(1, u.getId());
            try (ResultSet rsLikes = psLikes.executeQuery()) {
                if (rsLikes.next()) {
                    u.setLikeCount(rsLikes.getInt(1));
                }
            }
        }

        return u;
    }
}
