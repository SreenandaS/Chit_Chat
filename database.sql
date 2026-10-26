-- ============================================================
-- ChitChat - Social Networking Platform Database Schema
-- Database: MySQL 8.0+
-- ============================================================

DROP DATABASE IF EXISTS chitchat_db;
CREATE DATABASE chitchat_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE chitchat_db;

-- ------------------------------------------------------------
-- 1. Table: users
-- ------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    bio TEXT,
    profile_pic VARCHAR(500) DEFAULT 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150',
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_email (email)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. Table: posts
-- ------------------------------------------------------------
CREATE TABLE posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post_user (user_id),
    INDEX idx_post_created (created_at)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3. Table: likes
-- ------------------------------------------------------------
CREATE TABLE likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_post_user_like (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_like_post (post_id),
    INDEX idx_like_user (user_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4. Table: messages
-- ------------------------------------------------------------
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_text TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_msg_sender (sender_id),
    INDEX idx_msg_receiver (receiver_id),
    INDEX idx_msg_conv (sender_id, receiver_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 5. Table: notifications
-- ------------------------------------------------------------
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    actor_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    post_id INT DEFAULT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    INDEX idx_notif_user (user_id),
    INDEX idx_notif_unread (user_id, is_read)
) ENGINE=InnoDB;

-- ============================================================
-- Sample Data for Demonstration
-- Default Passwords:
--   admin -> admin123 (SHA-256: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9)
--   regular users -> password123 (SHA-256: ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f)
-- ============================================================

INSERT INTO users (username, email, password, full_name, bio, profile_pic, is_admin) VALUES
('admin', 'admin@chitchat.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator', 'System Administrator of ChitChat 🛡️', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150', TRUE),
('sarah_c', 'sarah@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Sarah Connor', 'Photographer & coffee lover ☕📸 Exploring golden hour.', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', FALSE),
('alex_dev', 'alex@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Alex Rivera', 'Full-stack builder & open source fanatic 🚀💻', 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150', FALSE),
('emily_w', 'emily@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Emily Watson', 'Travel writer & hiker 🌲 Living for sunset horizons.', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150', FALSE);

INSERT INTO posts (user_id, content, image_url) VALUES
(2, 'Golden hour in the mountains! The lighting was absolutely magical this evening ✨🏔️', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800'),
(3, 'Just finished deploying the new ChitChat release! Super clean Java Servlets & JSP architecture ☕🚀', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800'),
(4, 'Morning espresso with a side of calm before the week kicks off ☕🌿 Hope everyone has a productive week!', 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800'),
(1, 'Welcome to ChitChat! Please keep conversations friendly, respectful, and genuine ✨💬', NULL);

INSERT INTO likes (post_id, user_id) VALUES
(1, 1),
(1, 3),
(2, 2),
(2, 4),
(3, 1),
(3, 2),
(4, 2);

INSERT INTO messages (sender_id, receiver_id, message_text, is_read) VALUES
(2, 3, 'Hey Alex! Loved your new code post. Are you free to collaborate later?', TRUE),
(3, 2, 'Hi Sarah! Absolutely, let me know what you have in mind!', TRUE),
(4, 2, 'Hey Sarah, which camera did you use for that mountain shot?', FALSE);

INSERT INTO notifications (user_id, actor_id, type, post_id, message, is_read) VALUES
(2, 1, 'LIKE', 1, 'Administrator liked your post', FALSE),
(2, 3, 'LIKE', 1, 'Alex Rivera liked your post', FALSE),
(3, 2, 'LIKE', 2, 'Sarah Connor liked your post', FALSE),
(2, 4, 'MESSAGE', NULL, 'Emily Watson sent you a message', FALSE);
