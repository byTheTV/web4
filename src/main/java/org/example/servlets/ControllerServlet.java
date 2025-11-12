package org.example.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        route(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        route(req, resp);
    }

    private void route(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String pathInfo = requestURI.substring(contextPath.length());
        
        switch (pathInfo) {
            case "/app/area-check" -> {
                boolean hasParams = req.getParameter("x") != null || req.getParameter("y") != null || req.getParameter("r") != null;
                if (hasParams) {
                    req.getRequestDispatcher("/internal/area-check").forward(req, resp);
                } else {
                    req.getRequestDispatcher("/index.jsp").forward(req, resp);
                }
            }
            case "/app/", "/app" -> req.getRequestDispatcher("/index.jsp").forward(req, resp);
            default -> {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("404 - Not Found");
            }
        }
    }
}


