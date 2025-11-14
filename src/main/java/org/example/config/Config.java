package org.example.config;

import java.math.BigDecimal;
import java.util.Set;

public class Config {
    private static Set<Integer> allowedX = Set.of(-5, -4, -3, -2, -1, 0, 1, 2, 3);
    private static BigDecimal minY = BigDecimal.valueOf(-3);
    private static BigDecimal maxY = BigDecimal.valueOf(3);

    public static Set<Integer> getAllowedX() {
        return allowedX;
    }

    static BigDecimal getMinY() {
        return minY;
    }

    static BigDecimal getMaxY() {
        return maxY;
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

}


