package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class AreaCalculationService {
    
    public boolean checkPoint(Double x, Double y, Double r) {
        if (x == null || y == null || r == null) {
            return false;
        }
        

        double absR = Math.abs(r);
        if (absR <= 0) {
            return false;
        }


        if (x <= 0) {
            if (x >= -absR) {
                double yTop = x / 2.0 + absR;
                return y >= -absR && y <= yTop;
            }
        }

        // x >= 0, y <= 0, x^2 + y^2 <= (R/2)^2
        if (x >= 0 && y <= 0) {
            double radius = absR / 2.0;
            return x * x + y * y <= radius * radius + 1e-9; 
        }

        return false;
    }
}
