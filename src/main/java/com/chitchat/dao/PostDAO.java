package com.chitchat.dao;

import com.chitchat.model.Post;
import com.chitchat.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public int createPost(int userId, String content, String imageUrl) {
        String sql = "INSERT INTO posts (user_id, content, image_url) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, content.trim());
            ps.setString(3, (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl.trim() : null);

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

    public List<Post> getFeedPosts(int currentUserId) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_pic, "
                + "(SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count, "
                + "(SELECT COUNT(*) FROM likes WHERE post_id = p.id AND user_id = ?) AS user_liked "
                + "FROM posts p "
                + "JOIN users u ON p.user_id = u.id "
                + "ORDER BY p.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPost(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Post> getPostsByUserId(int userId, int currentUserId) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_pic, "
                + "(SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count, "
                + "(SELECT COUNT(*) FROM likes WHERE post_id = p.id AND user_id = ?) AS user_liked "
                + "FROM posts p "
                + "JOIN users u ON p.user_id = u.id "
                + "WHERE p.user_id = ? "
                + "ORDER BY p.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPost(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Post getPostById(int postId, int currentUserId) {
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_pic, "
                + "(SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count, "
                + "(SELECT COUNT(*) FROM likes WHERE post_id = p.id AND user_id = ?) AS user_liked "
                + "FROM posts p "
                + "JOIN users u ON p.user_id = u.id "
                + "WHERE p.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPost(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deletePost(int postId, int requestingUserId, boolean isAdmin) {
        String checkSql = "SELECT user_id FROM posts WHERE id = ?";
        String deleteSql = "DELETE FROM posts WHERE id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            int ownerId = -1;
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, postId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        ownerId = rs.getInt("user_id");
                    } else {
                        return false; // post not found
                    }
                }
            }

            // Only author or admin can delete
            if (ownerId != requestingUserId && !isAdmin) {
                return false;
            }

            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, postId);
                return psDel.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Post> getAllPostsForAdmin() {
        return getFeedPosts(0);
    }

    private Post mapRowToPost(ResultSet rs) throws Exception {
        Post p = new Post();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setAuthorUsername(rs.getString("username"));
        p.setAuthorFullName(rs.getString("full_name"));
        p.setAuthorPic(rs.getString("profile_pic"));
        p.setContent(rs.getString("content"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setLikeCount(rs.getInt("like_count"));
        p.setLikedByCurrentUser(rs.getInt("user_liked") > 0);
        return p;
    }
}
