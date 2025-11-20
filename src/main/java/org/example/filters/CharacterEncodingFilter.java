package org.example.filters;

import jakarta.servlet.*;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // Логирование для отладки AJAX запросов
        if (request instanceof jakarta.servlet.http.HttpServletRequest) {
            jakarta.servlet.http.HttpServletRequest httpRequest = (jakarta.servlet.http.HttpServletRequest) request;
            if ("POST".equals(httpRequest.getMethod()) && httpRequest.getRequestURI().contains("main.xhtml")) {
                System.out.println("=== POST запрос к main.xhtml ===");
                System.out.println("Faces-Request: " + httpRequest.getHeader("Faces-Request"));
                System.out.println("X-Requested-With: " + httpRequest.getHeader("X-Requested-With"));
                System.out.println("Content-Type: " + httpRequest.getContentType());
                
                java.util.Enumeration<String> paramNames = httpRequest.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    String paramValue = httpRequest.getParameter(paramName);
                    if (paramName.contains("Value") || paramName.contains("ViewState") || paramName.contains("partial")) {
                        System.out.println("  " + paramName + " = " + paramValue);
                    }
                }
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
}

