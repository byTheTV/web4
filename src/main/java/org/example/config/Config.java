package org.example.config;

import java.math.BigDecimal;
import java.util.Set;

public class Config {
    private static Set<Integer> allowedX = Set.of(-5, -4, -3, -2, -1, 0, 1, 2, 3);
    private static BigDecimal minY = BigDecimal.valueOf(-5);
    private static BigDecimal maxY = BigDecimal.valueOf(5);
    private static Set<BigDecimal> allowedR = Set.of(
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(5)
    );

    static Set<Integer> getAllowedX() {
        return allowedX;
    }

    static BigDecimal getMinY() {
        return minY;
    }

    static BigDecimal getMaxY() {
        return maxY;
    }

    static Set<BigDecimal> getAllowedR() {
        return allowedR;
    }

    static void setAllowedX(Set<Integer> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("allowedX must be non-empty");
        }
        allowedX = Set.copyOf(values);
    }

    static void setYRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null || min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Invalid Y range");
        }
        minY = min;
        maxY = max;
    }

    static void setAllowedR(Set<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("allowedR must be non-empty");
        }
        allowedR = Set.copyOf(values);
    }
}


