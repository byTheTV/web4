package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class AreaCalculationService {
    
    public boolean checkPoint(Double x, Double y, Double r) {
        if (x == null || y == null || r == null) {
            return false;
        }
        
        // Радиус области должен быть положительным
        double absR = Math.abs(r);
        if (absR <= 0) {
            return false;
        }

        // Левая область: многоугольник с вершинами
        // A(-R, -R), B(0, -R), C(0, R), D(-R, R/2)
        //
        // Условие: x ∈ [-R, 0], y ∈ [-R, y_top(x)],
        // где верхняя граница y_top(x) = x/2 + R (прямая через точки D(-R, R/2) и C(0, R))
        if (x <= 0) {
            if (x >= -absR) {
                double yTop = x / 2.0 + absR;
                return y >= -absR && y <= yTop;
            }
        }

        // Правая область: четверть круга радиуса R/2 в 4-й четверти
        // x >= 0, y <= 0, x^2 + y^2 <= (R/2)^2
        if (x >= 0 && y <= 0) {
            double radius = absR / 2.0;
            return x * x + y * y <= radius * radius + 1e-9; // небольшой допуск на погрешность
        }

        return false;
    }
}
