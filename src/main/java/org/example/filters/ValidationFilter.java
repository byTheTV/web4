package org.example.filters;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ValidationFilter implements Filter {
    
    private static final Set<Integer> ALLOWED_X = Set.of(-5, -4, -3, -2, -1, 0, 1, 2, 3);
    private static final Set<Integer> ALLOWED_R = Set.of(1, 2, 3, 4, 5);
    private static final double MIN_Y = -5.0;
    private static final double MAX_Y = 5.0;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        if ("POST".equals(req.getMethod()) && req.getRequestURI().endsWith("/app/area-check")) {
            
            try {
                String xStr = req.getParameter("x");
                if (xStr == null || !xStr.matches("^-?\\d+$")) {
                    sendValidationError(resp, "X должен быть целым числом");
                    return;
                }
                int x = Integer.parseInt(xStr);
                if (!ALLOWED_X.contains(x)) {
                    sendValidationError(resp, "X должен быть одним из: " + ALLOWED_X);
                    return;
                }
                
                String yStr = req.getParameter("y");
                if (yStr == null || yStr.trim().isEmpty()) {
                    sendValidationError(resp, "Y не может быть пустым");
                    return;
                }
                double y = Double.parseDouble(yStr.replace(',', '.'));
                if (y < MIN_Y || y > MAX_Y) {
                    sendValidationError(resp, "Y должен быть в диапазоне [" + MIN_Y + ", " + MAX_Y + "]");
                    return;
                }
                
                String rStr = req.getParameter("r");
                if (rStr == null || !rStr.matches("^\\d+$")) {
                    sendValidationError(resp, "R должен быть положительным целым числом");
                    return;
                }
                int r = Integer.parseInt(rStr);
                if (!ALLOWED_R.contains(r)) {
                    sendValidationError(resp, "R должен быть одним из: " + ALLOWED_R);
                    return;
                }
                
            } catch (NumberFormatException e) {
                sendValidationError(resp, "Некорректный формат числовых параметров");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    private void sendValidationError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("""
            <html>
            <head><title>Ошибка валидации</title></head>
            <body>
                <h2>Ошибка валидации параметров</h2>
                <p>%s</p>
                <p><a href="/labwork2/app/">Вернуться к форме</a></p>
            </body>
            </html>
            """.formatted(message));
    }
    
    @Override
    public void destroy() {
    }
}
