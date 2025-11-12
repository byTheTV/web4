package org.example.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ParameterCheckFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        if ("POST".equals(req.getMethod()) && req.getRequestURI().endsWith("/app/area-check")) {
            
            String x = req.getParameter("x");
            String y = req.getParameter("y");
            String r = req.getParameter("r");
            
            if (x == null || x.trim().isEmpty() || 
                y == null || y.trim().isEmpty() || 
                r == null || r.trim().isEmpty()) {
                
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("text/html; charset=UTF-8");
                resp.getWriter().println("""
                    <html>
                    <head><title>Ошибка параметров</title></head>
                    <body>
                        <h2>Ошибка: Отсутствуют обязательные параметры</h2>
                        <p>Необходимо указать все параметры: x, y, r</p>
                        <p><a href="/labwork2/app/">Вернуться к форме</a></p>
                    </body>
                    </html>
                    """);
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
}
