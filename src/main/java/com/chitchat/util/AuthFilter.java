package com.chitchat.util;

import com.chitchat.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow static resources and public endpoints
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.equals("/login") || path.equals("/register") || path.equals("/logout")
                || path.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // If not logged in, redirect to login
        if (currentUser == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Admin-only protection
        if (path.startsWith("/admin")) {
            if (!currentUser.isAdmin()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator account required.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
