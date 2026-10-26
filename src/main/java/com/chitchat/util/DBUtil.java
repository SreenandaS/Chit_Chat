package com.chitchat.util;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database utility class managing JDBC connections and schema initialization.
 * Prioritizes MySQL as specified; if MySQL server is not running, falls back
 * gracefully to local persistent database (./data/chitchat_db) in MySQL mode
 * so the app runs out-of-the-box.
 */
public class DBUtil {

    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/chitchat_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String username = "root";
    private static String password = "root";
    private static boolean useFallback = true;
    private static boolean isFallbackActive = false;

    static {
        loadProperties();
        initDatabase();
    }

    private static void loadProperties() {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                driver = props.getProperty("db.driver", driver);
                url = props.getProperty("db.url", url);
                username = props.getProperty("db.username", username);
                password = props.getProperty("db.password", password);
                useFallback = Boolean.parseBoolean(props.getProperty("db.useFallback", "true"));
            }
        } catch (Exception e) {
            System.err.println("[DBUtil] Could not load db.properties, using defaults: " + e.getMessage());
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (!isFallbackActive) {
            try {
                Class.forName(driver);
                return DriverManager.getConnection(url, username, password);
            } catch (Exception e) {
                if (useFallback) {
                    System.out.println("[DBUtil] MySQL connection failed (" + e.getMessage() + "). Switching to persistent file database (./data/chitchat_db in MySQL mode)...");
                    isFallbackActive = true;
                } else {
                    throw new SQLException("Could not connect to MySQL at " + url, e);
                }
            }
        }

        // Persistent file database with MySQL compatibility
        try {
            Class.forName("org.h2.Driver");
            new File("data").mkdirs();
            return DriverManager.getConnection("jdbc:h2:./data/chitchat_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH", "sa", "");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Fallback database driver not found", e);
        }
    }

    public static void initDatabase() {
        try (Connection conn = getConnection()) {
            System.out.println("[DBUtil] Connected to database: " + conn.getMetaData().getDatabaseProductName());

            try (Statement stmt = conn.createStatement()) {
                // 1. Users table
                stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(50) NOT NULL UNIQUE, "
                        + "email VARCHAR(100) NOT NULL UNIQUE, "
                        + "password VARCHAR(255) NOT NULL, "
                        + "full_name VARCHAR(100) NOT NULL, "
                        + "bio TEXT, "
                        + "profile_pic VARCHAR(500) DEFAULT 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', "
                        + "is_admin BOOLEAN DEFAULT FALSE, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ");");

                // 2. Posts table
                stmt.execute("CREATE TABLE IF NOT EXISTS posts ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "user_id INT NOT NULL, "
                        + "content TEXT NOT NULL, "
                        + "image_url VARCHAR(500), "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ");");

                // 3. Likes table
                stmt.execute("CREATE TABLE IF NOT EXISTS likes ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "post_id INT NOT NULL, "
                        + "user_id INT NOT NULL, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "UNIQUE (post_id, user_id), "
                        + "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ");");

                // 4. Messages table
                stmt.execute("CREATE TABLE IF NOT EXISTS messages ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "sender_id INT NOT NULL, "
                        + "receiver_id INT NOT NULL, "
                        + "message_text TEXT NOT NULL, "
                        + "is_read BOOLEAN DEFAULT FALSE, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ");");

                // 5. Notifications table
                stmt.execute("CREATE TABLE IF NOT EXISTS notifications ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "user_id INT NOT NULL, "
                        + "actor_id INT NOT NULL, "
                        + "type VARCHAR(50) NOT NULL, "
                        + "post_id INT DEFAULT NULL, "
                        + "message VARCHAR(255) NOT NULL, "
                        + "is_read BOOLEAN DEFAULT FALSE, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ");");
            }

            // Seed initial sample data if table is empty
            seedSampleData(conn);

        } catch (Exception e) {
            System.err.println("[DBUtil] Database initialization warning: " + e.getMessage());
        }
    }

    private static void seedSampleData(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("[DBUtil] Seeding default sample users and content...");

                String adminPass = PasswordUtil.hashPassword("admin123");
                String userPass = PasswordUtil.hashPassword("password123");

                // Insert users
                String insertUser = "INSERT INTO users (username, email, password, full_name, bio, profile_pic, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                    // Admin
                    ps.setString(1, "admin");
                    ps.setString(2, "admin@chitchat.com");
                    ps.setString(3, adminPass);
                    ps.setString(4, "Administrator");
                    ps.setString(5, "System Administrator of ChitChat 🛡️");
                    ps.setString(6, "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150");
                    ps.setBoolean(7, true);
                    ps.executeUpdate();

                    // Sarah
                    ps.setString(1, "sarah_c");
                    ps.setString(2, "sarah@example.com");
                    ps.setString(3, userPass);
                    ps.setString(4, "Sarah Connor");
                    ps.setString(5, "Photographer & coffee lover ☕📸 Exploring golden hour.");
                    ps.setString(6, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150");
                    ps.setBoolean(7, false);
                    ps.executeUpdate();

                    // Alex
                    ps.setString(1, "alex_dev");
                    ps.setString(2, "alex@example.com");
                    ps.setString(3, userPass);
                    ps.setString(4, "Alex Rivera");
                    ps.setString(5, "Full-stack builder & open source fanatic 🚀💻");
                    ps.setString(6, "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150");
                    ps.setBoolean(7, false);
                    ps.executeUpdate();

                    // Emily
                    ps.setString(1, "emily_w");
                    ps.setString(2, "emily@example.com");
                    ps.setString(3, userPass);
                    ps.setString(4, "Emily Watson");
                    ps.setString(5, "Travel writer & hiker 🌲 Living for sunset horizons.");
                    ps.setString(6, "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150");
                    ps.setBoolean(7, false);
                    ps.executeUpdate();
                }

                // Insert posts
                String insertPost = "INSERT INTO posts (user_id, content, image_url) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertPost)) {
                    ps.setInt(1, 2);
                    ps.setString(2, "Golden hour in the mountains! The lighting was absolutely magical this evening ✨🏔️");
                    ps.setString(3, "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800");
                    ps.executeUpdate();

                    ps.setInt(1, 3);
                    ps.setString(2, "Just finished deploying the new ChitChat release! Super clean Java Servlets & JSP architecture ☕🚀");
                    ps.setString(3, "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800");
                    ps.executeUpdate();

                    ps.setInt(1, 4);
                    ps.setString(2, "Morning espresso with a side of calm before the week kicks off ☕🌿 Hope everyone has a productive week!");
                    ps.setString(3, "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800");
                    ps.executeUpdate();

                    ps.setInt(1, 1);
                    ps.setString(2, "Welcome to ChitChat! Please keep conversations friendly, respectful, and genuine ✨💬");
                    ps.setString(3, null);
                    ps.executeUpdate();
                }

                // Insert likes
                String insertLike = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertLike)) {
                    ps.setInt(1, 1); ps.setInt(2, 1); ps.executeUpdate();
                    ps.setInt(1, 1); ps.setInt(2, 3); ps.executeUpdate();
                    ps.setInt(1, 2); ps.setInt(2, 2); ps.executeUpdate();
                    ps.setInt(1, 2); ps.setInt(2, 4); ps.executeUpdate();
                    ps.setInt(1, 3); ps.setInt(2, 1); ps.executeUpdate();
                    ps.setInt(1, 3); ps.setInt(2, 2); ps.executeUpdate();
                }

                // Insert messages
                String insertMsg = "INSERT INTO messages (sender_id, receiver_id, message_text, is_read) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertMsg)) {
                    ps.setInt(1, 2); ps.setInt(2, 3);
                    ps.setString(3, "Hey Alex! Loved your new code post. Are you free to collaborate later?");
                    ps.setBoolean(4, true); ps.executeUpdate();

                    ps.setInt(1, 3); ps.setInt(2, 2);
                    ps.setString(3, "Hi Sarah! Absolutely, let me know what you have in mind!");
                    ps.setBoolean(4, true); ps.executeUpdate();

                    ps.setInt(1, 4); ps.setInt(2, 2);
                    ps.setString(3, "Hey Sarah, which camera did you use for that mountain shot?");
                    ps.setBoolean(4, false); ps.executeUpdate();
                }

                // Insert notifications
                String insertNotif = "INSERT INTO notifications (user_id, actor_id, type, post_id, message, is_read) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertNotif)) {
                    ps.setInt(1, 2); ps.setInt(2, 1); ps.setString(3, "LIKE"); ps.setInt(4, 1);
                    ps.setString(5, "Administrator liked your post"); ps.setBoolean(6, false); ps.executeUpdate();

                    ps.setInt(1, 2); ps.setInt(2, 3); ps.setString(3, "LIKE"); ps.setInt(4, 1);
                    ps.setString(5, "Alex Rivera liked your post"); ps.setBoolean(6, false); ps.executeUpdate();

                    ps.setInt(1, 3); ps.setInt(2, 2); ps.setString(3, "LIKE"); ps.setInt(4, 2);
                    ps.setString(5, "Sarah Connor liked your post"); ps.setBoolean(6, false); ps.executeUpdate();

                    ps.setInt(1, 2); ps.setInt(2, 4); ps.setString(3, "MESSAGE"); ps.setNull(4, java.sql.Types.INTEGER);
                    ps.setString(5, "Emily Watson sent you a message"); ps.setBoolean(6, false); ps.executeUpdate();
                }

                System.out.println("[DBUtil] Seed sample data initialized successfully!");
            }
        } catch (SQLException e) {
            System.err.println("[DBUtil] Error while checking/seeding data: " + e.getMessage());
        }
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }
}
