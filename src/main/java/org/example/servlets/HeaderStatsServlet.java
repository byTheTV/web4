package org.example.servlets;

import java.io.IOException;
import java.util.Map;

import org.example.filters.HeaderStatsFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HeaderStatsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        Map<String, Integer> headerStats = HeaderStatsFilter.getHeaderStats();
        
        resp.setContentType("application/json; charset=UTF-8");
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"headerStats\": {\n");
        
        boolean first = true;
        for (Map.Entry<String, Integer> entry : headerStats.entrySet()) {
            if (!first) {
                json.append(",\n");
            }
            json.append("    \"").append(entry.getKey()).append("\": ").append(entry.getValue());
            first = false;
        }
        
        json.append("\n  },\n");
        json.append("  \"totalHeaders\": ").append(headerStats.size()).append(",\n");
        json.append("  \"totalRequests\": ").append(headerStats.values().stream().mapToInt(Integer::intValue).sum()).append("\n");
        json.append("}");
        
        resp.getWriter().println(json.toString());
    }
}
