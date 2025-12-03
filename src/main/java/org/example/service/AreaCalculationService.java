package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class AreaCalculationService {
    
    public boolean checkPoint(Double x, Double y, Double r) {
        if (x == null || y == null || r == null) {
            return false;
        }
        
        // Use absolute value of R for calculations (since R can be negative in input but represents radius)
        double absR = Math.abs(r);
        
        // Condition: x >= 0, y >= 0, x + y <= R
        if (x >= 0 && y >= 0) {
            return (x + y) <= absR;
        }
        
        // Condition: x <= 0, y >= 0, x >= -R/2, y <= R
        if (x <= 0 && y >= 0) {
            return x >= -absR / 2 && y <= absR;
        }
        
        // Condition: x >= 0, y <= 0, x^2 + y^2 <= (R/2)^2
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (absR / 2) * (absR / 2);
        }
        
        return false;
    }
}
