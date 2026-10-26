# ChitChat – Social Networking Platform 💬📸

A complete, fully functional social networking mini project built with **Java Servlets, JSP, JDBC, MySQL, HTML5, CSS3, JavaScript, and Bootstrap 5**, deployable on **Apache Tomcat**.

---

## 🌟 Features Overview

1. **User Authentication & Session Security:**
   - Registration with input validation and uniqueness checks (username, email).
   - Secure login and logout with session state (`HttpSession`).
   - Secure password hashing using **SHA-256**.
   - `AuthFilter` access control protecting private routes and enforcing role-based permissions.

2. **User Profiles & Search:**
   - Dynamic user profiles with avatar, bio, total posts, and total likes received.
   - Profile editing (full name, bio, profile picture URL with live preview and presets).
   - User discovery and search feature to find registered members.

3. **Social Feed:**
   - Create text posts and rich image posts (via image URLs or presets).
   - Real-time like and unlike toggle with dynamic heart animation and count update (AJAX).
   - Chronological social feed displaying author avatar, username, formatted relative timestamps, and content.
   - Secure post deletion (authors can delete their own posts; administrators can moderate and delete any post).

4. **Direct Messaging (Near-Real-Time via JavaScript Polling):**
   - Private 1-to-1 conversations between registered users.
   - Full conversation history with chat bubbles (*mine* vs *theirs*).
   - Near-real-time message updates powered by automatic JavaScript polling (`/messages/fetch`) every 2.5 seconds.
   - Message delivery with automatic read-status tracking.

5. **Notifications System:**
   - Automated alerts generated when another user likes a post or sends a direct message.
   - Dynamic unread notification counter badge in the sidebar and navigation bar.
   - Dedicated Notifications page with direct links to posts or conversations.

6. **Admin Dashboard (Protected):**
   - Restricted exclusively to authorized administrator accounts (`user.isAdmin() == true`).
   - Key platform metrics: Total registered users, total posts created, and total likes recorded.
   - Content moderation queue: Review and delete inappropriate posts across the platform.
   - Registered users directory: View joined date, role badges, post count, and manage user accounts.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java (JDK 17+) |
| **Backend Architecture** | Java Servlets (Jakarta EE 6.0) & JSP (Jakarta Pages 3.1) |
| **Database** | MySQL 8.0+ / JDBC with `PreparedStatement` (Zero-config persistent fallback enabled) |
| **Frontend UI** | HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3, Bootstrap Icons |
| **Build & Packaging** | Apache Maven (`war` packaging) |
| **Server** | Apache Tomcat 10+ (Embedded launcher included + Deployable WAR) |

---

## 📁 Project Structure

```
ChitChat/
├── pom.xml                               # Maven project configuration & dependencies
├── database.sql                          # Complete MySQL schema DDL & sample data
├── README.md                             # Documentation & user instructions
├── classpath.txt                         # Generated classpath for direct Java launcher
├── test_server.ps1                       # End-to-end verification script
├── verify_chitchat.py                    # Automated test suite (13 test cases)
├── data/                                 # Persistent database storage
└── src/
    └── main/
        ├── java/
        │   └── com/chitchat/
        │       ├── Main.java             # Embedded Tomcat launcher (1-click run)
        │       ├── model/                # Java Model / Entity Beans
        │       │   ├── User.java
        │       │   ├── Post.java
        │       │   ├── Like.java
        │       │   ├── Message.java
        │       │   └── Notification.java
        │       ├── dao/                  # Data Access Objects (JDBC PreparedStatements)
        │       │   ├── UserDAO.java
        │       │   ├── PostDAO.java
        │       │   ├── LikeDAO.java
        │       │   ├── MessageDAO.java
        │       │   └── NotificationDAO.java
        │       ├── servlet/              # Jakarta Servlets (Request Handlers)
        │       │   ├── AuthServlet.java
        │       │   ├── FeedServlet.java
        │       │   ├── PostServlet.java
        │       │   ├── ProfileServlet.java
        │       │   ├── SearchServlet.java
        │       │   ├── MessageServlet.java
        │       │   ├── NotificationServlet.java
        │       │   └── AdminServlet.java
        │       └── util/                 # Database & Security Utilities
        │           ├── DBUtil.java       # Connection factory & auto-schema init
        │           ├── PasswordUtil.java # SHA-256 hashing utility
        │           └── AuthFilter.java   # Authentication & Admin route guard
        ├── resources/
        │   └── db.properties             # MySQL database credentials configuration
        └── webapp/                       # JSP presentation layer & static assets
            ├── index.jsp                 # Landing page forwarder to /feed
            ├── WEB-INF/
            │   ├── web.xml               # Servlet, filter, and session configurations
            │   └── views/
            │       ├── common/
            │       │   ├── header.jsp    # HTML head, Bootstrap & font links
            │       │   ├── sidebar.jsp   # Responsive sidebar & mobile bottom bar
            │       │   └── footer.jsp    # Scripts & footer closing tags
            │       ├── login.jsp         # User authentication form & quick credentials
            │       ├── register.jsp      # Account registration form
            │       ├── feed.jsp          # Main social feed with post creation
            │       ├── profile.jsp       # User profile & authored posts
            │       ├── edit-profile.jsp  # Profile edit form with image presets
            │       ├── search.jsp        # Member search & discovery
            │       ├── messages.jsp      # 1-to-1 messaging interface with polling
            │       ├── notifications.jsp # Alerts & activity notifications
            │       └── admin.jsp         # Administrative moderation console
            ├── css/
            │   └── chitchat.css          # Modern social media styling
            └── js/
                └── chitchat.js           # AJAX likes, polling, & client interactions
```

---

## 🗄️ Database Configuration & Setup

### 1. MySQL Setup
1. Open your MySQL client (MySQL Workbench, phpMyAdmin, or Command Line).
2. Execute the included [database.sql](file:///c:/Users/ASUS/OneDrive/Desktop/ChitChat/database.sql) script:
   ```bash
   mysql -u root -p < database.sql
   ```
3. Update [src/main/resources/db.properties](file:///c:/Users/ASUS/OneDrive/Desktop/ChitChat/src/main/resources/db.properties) with your MySQL credentials:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/chitchat_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=root
   db.password=your_mysql_password
   ```

### 2. Zero-Config Fallback (Out-of-the-Box Mode)
If MySQL is not installed or not currently running, `DBUtil.java` automatically detects this and falls back to a persistent file database (`./data/chitchat_db`) in MySQL compatibility mode. The tables and sample seed data are initialized automatically on the first run, guaranteeing that the application works immediately on any machine!

---

## 🚀 How to Run the Application

### Option A: 1-Click Run via Maven (Recommended)
You can run the application directly from the project directory:

```bash
mvn compile exec:java
```

Once started, open your browser and navigate to:
👉 **`http://localhost:8080/`** (or `http://localhost:8080/login`)

---

### Option B: Deploy to Apache Tomcat (WAR file)
1. Build the production WAR package:
   ```bash
   mvn clean package -DskipTests
   ```
2. The generated WAR file will be located at:
   ```
   target/chitchat.war
   ```
3. Copy `target/chitchat.war` into your Tomcat installation's `webapps/` folder:
   ```
   apache-tomcat-10.x.x/webapps/chitchat.war
   ```
4. Start Tomcat (`bin/startup.bat` on Windows or `bin/startup.sh` on Linux/macOS).
5. Open your browser:
   👉 **`http://localhost:8080/chitchat/`**

---

## 🔑 Demo Test Accounts

The database comes pre-seeded with sample users, posts, messages, and likes for instant evaluation:

| Account Type | Username / Email | Password | Role | Features |
|---|---|---|---|---|
| **Administrator** | `admin` / `admin@chitchat.com` | `admin123` | Administrator | Full access + Admin Dashboard & Content Moderation |
| **Regular User** | `sarah_c` / `sarah@example.com` | `password123` | Member | Feed, Posting, Likes, Messaging, Profile |
| **Regular User** | `alex_dev` / `alex@example.com` | `password123` | Member | Feed, Posting, Likes, Messaging, Profile |
| **Regular User** | `emily_w` / `emily@example.com` | `password123` | Member | Feed, Posting, Likes, Messaging, Profile |

*(You can also register any new user account directly via the "Sign Up" page).*

---

## 🧪 Automated End-to-End Test Suite

An automated verification test suite is included in `verify_chitchat.py`. It tests all 13 core requirements end-to-end:
```bash
python verify_chitchat.py
```

### Verified Test Cases:
1. `GET /login` — Renders login view with HTTP 200.
2. `POST /login` — Authenticates user credentials with SHA-256 and establishes session.
3. `GET /feed` — Verifies social feed content, posts, and authors.
4. `GET /profile` — Validates user profile stats (posts count, likes received).
5. `GET /search?q=sarah` — Validates user search by keyword.
6. `POST /post/create` — Creates a new post with text and photo URL.
7. `POST /post/like` — Toggles like status and verifies real-time like count.
8. `POST /messages/send` — Sends a private 1-to-1 direct message.
9. `GET /messages/fetch` — Verifies message polling updates for conversation.
10. `GET /notifications` — Displays alerts for likes and new messages.
11. `GET /admin` — Loads administrative moderation console and metrics.
12. `POST /register` — Registers a new user account with unique validation.
13. Route Protection — Confirms non-admin users receive `403 Forbidden` on `/admin`.
