package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class AreaCalculationService {
    
    public boolean checkPoint(Integer x, Double y, Double r) {
        if (x == null || y == null || r == null || r <= 0) {
            return false;
        }
        
        // Condition: x >= 0, y >= 0, x + y <= R
        if (x >= 0 && y >= 0) {
            return (x + y) <= r;
        }
        
        // Condition: x <= 0, y >= 0, x >= -R/2, y <= R
        if (x <= 0 && y >= 0) {
            return x >= -r / 2 && y <= r;
        }
        
        // Condition: x >= 0, y <= 0, x^2 + y^2 <= (R/2)^2
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r / 2) * (r / 2);
        }
        
        return false;
    }
}

