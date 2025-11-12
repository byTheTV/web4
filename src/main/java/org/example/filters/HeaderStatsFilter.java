package org.example.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class HeaderStatsFilter implements Filter {
    
    private static final Map<String, Integer> headerStats = new ConcurrentHashMap<>();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerStats.merge(headerName, 1, Integer::sum);
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
    
    public static Map<String, Integer> getHeaderStats() {
        return new ConcurrentHashMap<>(headerStats);
    }
    
    public static void clearHeaderStats() {
        headerStats.clear();
    }
}
