package com.chitchat.servlet;

import com.chitchat.dao.UserDAO;
import com.chitchat.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if ("/logout".equals(servletPath)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login?msg=logged_out");
            return;
        }

        // If user is already logged in, redirect to feed
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/feed");
            return;
        }

        if ("/register".equals(servletPath)) {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if ("/login".equals(servletPath)) {
            handleLogin(req, resp);
        } else if ("/register".equals(servletPath)) {
            handleRegister(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loginField = req.getParameter("usernameOrEmail");
        String passwordField = req.getParameter("password");

        if (loginField == null || loginField.trim().isEmpty() || passwordField == null || passwordField.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide both username/email and password.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.authenticate(loginField.trim(), passwordField.trim());
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            resp.sendRedirect(req.getContextPath() + "/feed");
        } else {
            req.setAttribute("errorMessage", "Invalid username/email or password.");
            req.setAttribute("prevUsername", loginField);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String bio = req.getParameter("bio");
        String profilePic = req.getParameter("profilePic");

        // Basic validation
        if (username == null || username.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().length() < 4
                || fullName == null || fullName.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please fill all required fields. Password must be at least 4 characters.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        username = username.trim().replaceAll("\\s+", "_");

        if (userDAO.isUsernameTaken(username)) {
            req.setAttribute("errorMessage", "Username '@" + username + "' is already taken. Please pick another.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (userDAO.isEmailTaken(email)) {
            req.setAttribute("errorMessage", "Email '" + email + "' is already registered. Please login.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPassword(password);
        newUser.setFullName(fullName.trim());
        newUser.setBio(bio != null ? bio.trim() : "");
        newUser.setProfilePic((profilePic != null && !profilePic.trim().isEmpty())
                ? profilePic.trim()
                : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
        newUser.setAdmin(false);

        int newId = userDAO.register(newUser);
        if (newId > 0) {
            newUser.setId(newId);
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", newUser);
            resp.sendRedirect(req.getContextPath() + "/feed");
        } else {
            req.setAttribute("errorMessage", "An unexpected error occurred while creating your account. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
