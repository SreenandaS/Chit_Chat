package com.chitchat.dao;

import com.chitchat.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LikeDAO {

    public boolean toggleLike(int postId, int userId) {
        String checkSql = "SELECT id FROM likes WHERE post_id = ? AND user_id = ?";
        String deleteSql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        String insertSql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            boolean alreadyLiked = false;
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, postId);
                psCheck.setInt(2, userId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    alreadyLiked = rs.next();
                }
            }

            if (alreadyLiked) {
                try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                    psDel.setInt(1, postId);
                    psDel.setInt(2, userId);
                    psDel.executeUpdate();
                }
                return false; // unliked
            } else {
                try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                    psIns.setInt(1, postId);
                    psIns.setInt(2, userId);
                    psIns.executeUpdate();
                }
                return true; // liked
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
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
}
