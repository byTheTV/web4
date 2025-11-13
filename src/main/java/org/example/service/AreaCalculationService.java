package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service слой - содержит бизнес-логику расчета попадания точки в область.
 * Не является managed bean для JSF, но является CDI бином.
 */
@ApplicationScoped
public class AreaCalculationService {
    
    /**
     * Проверяет, попадает ли точка в заданную область.
     * 
     * @param x координата X
     * @param y координата Y
     * @param r радиус области
     * @return true если точка попадает в область, false иначе
     */
    public boolean checkPoint(Integer x, Double y, Double r) {
        if (x == null || y == null || r == null || r <= 0) {
            return false;
        }
        
        // First quadrant: triangle with vertices (0,0), (R,0), (0,R)
        // Condition: x >= 0, y >= 0, x + y <= R
        if (x >= 0 && y >= 0) {
            return (x + y) <= r;
        }
        
        // Second quadrant: rectangle from (0,0) to (-R/2, R)
        // Condition: x <= 0, y >= 0, x >= -R/2, y <= R
        if (x <= 0 && y >= 0) {
            return x >= -r / 2 && y <= r;
        }
        
        // Fourth quadrant: quarter circle centered at (0,0) with radius R/2
        // Condition: x >= 0, y <= 0, x^2 + y^2 <= (R/2)^2
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r / 2) * (r / 2);
        }
        
        return false;
    }
}

