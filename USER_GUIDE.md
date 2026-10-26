# ChitChat — User & Testing Guide 📖✨

Welcome to ChitChat! This guide walks you through using all major features of the platform, testing interactions, and reviewing administrative capabilities.

---

## 1. Quick Start
1. Ensure the app is running:
   ```bash
   mvn spring-boot:run
   ```
2. Open your browser at:
   **http://localhost:8080**

---

## 2. Authentication & Exploring Feeds
### 2.1 Logging In
- Use one of the pre-seeded demo accounts:
  - **`maya_designs` / `password123`** (Active creator)
  - **`leo_travels` / `password123`** (Photographer & traveler)
  - **`admin` / `Admin@123`** (Platform Moderator)
- Or click **"Join ChitChat, it's free"** to register a new account with your own username and email.

### 2.2 Exploring the Home Feed
- The feed displays photos in an Instagram-style centered single column.
- **Multi-Photo Posts:** Swipe or click the left/right arrow buttons to glide between photos in multi-photo carousels; indicator dots track the current photo.
- **Double-Tap Like:** Quickly double-click or tap anywhere on a photo to trigger the animated heart pop effect.
- **Comments:** Click the comment icon or type into the **"Say something nice..."** box to post an instant comment. Authors can delete their own comments at any time.

---

## 3. Profiles & Social Graph
### 3.1 Viewing Profiles
- Click on any creator's avatar or username (e.g. `/u/leo_travels`).
- The profile header displays their avatar, full name, bio, location, website, and statistics:
  - Posts count
  - Followers count
  - Following count
- **Popup Modals:** Click on **followers** or **following** to view the list of users in a modal dialog.
- **3-Column Photo Grid:** Browse the user's photos in a responsive 3-column square grid. Hovering over a tile displays engagement counts (likes & comments).
- **Tab Switching:** Toggle between **"POSTS"** and **"LIKED"** tabs to see the creator's posts or posts they have liked.

### 3.2 Editing Profile & Uploading Avatar
- When viewing your own profile, click **"Edit profile"** (or navigate to `/settings/profile`).
- Update your full name, bio, location, or website.
- Click **"Change profile photo"** to select a new JPG, PNG, or WebP image (up to 5 MB). The file name is randomized and securely stored on disk in `./uploads/avatars/`.

---

## 4. Creating Posts
1. Click the **"New post" (+)** icon on the left sidebar (or the gradient center button on mobile).
2. Click **"Choose Files"** to select up to 4 photos.
3. Interactive thumbnails appear with a remove button (`×`) to adjust selections.
4. Add a warm caption with hashtags (e.g. `Golden hour walk by the bay 🌅 #sunset #travel #chitchat`).
5. Click **"Share"**. The post is saved to `./uploads/posts/` and immediately featured in the feed.

---

## 5. Real-Time Chat & Direct Messaging
1. Click **Messages** in the sidebar (or visit `/chat`).
2. **Left Panel:** Displays active conversations with the other user's avatar, last message preview, unread badges, and green online presence indicator dots.
3. **Right Panel:**
   - Real-time chat bubbles (yours in warm coral-purple gradient on the right, theirs in soft gray on the left).
   - Live **"user is typing..."** indicator appears when the recipient is actively typing.
   - **Delivered & Seen Receipts:** When the recipient views the conversation, messages show **"• Seen"**.
4. To start a chat with any user, either click the **"Message"** button on their profile or use the pencil icon in the messages tab.

---

## 6. Live Notifications
- Real-time notifications arrive when someone:
  - Starts following you
  - Likes your photo
  - Comments on your post
  - Sends you a message
  - Issues a moderator warning
- Online users receive a floating toast popup in the lower-right corner with the actor's avatar and notification message.
- The heart icon on the sidebar displays a live red unread counter badge.
- Clicking **Alerts** opens the full notifications center (`/notifications`) where you can click through to any post/profile or click **"Mark all as read"**.

---

## 7. Administrative Moderation & Analytics
1. Log in as an administrator:
   - Username: **`admin`**
   - Password: **`Admin@123`**
2. Click the **Admin** link in the sidebar (route `/admin`).
3. **Analytics Dashboard:**
   - 4 metric cards: Total Users, Total Posts, Pending Reports, Resolved Reports.
   - 2 interactive Chart.js graphs displaying 7-day user registrations and post creations.
   - Most active creators and trending hashtags leaderboards.
4. **User Management (`/admin/users`):**
   - Search users by username or full name.
   - One-click actions to **Ban**, **Unban**, **Promote to Admin**, or **Delete Account**.
5. **Moderation Queue (`/admin/reports`):**
   - Any user can flag inappropriate content via the `...` menu on any post.
   - Moderators can review the reported post or comment, click **"Remove & Warn"** to soft-delete the violating content and send a warning alert to the author, or click **"Dismiss"**.
