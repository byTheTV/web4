package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/validation")
@CrossOrigin(origins = "http://localhost:3000")
public class ValidationController {
    
    private static final List<String> ALLOWED_X = Arrays.asList("-2", "-1.5", "-1", "-0.5", "0", "0.5", "1", "1.5", "2");
    private static final List<String> ALLOWED_R = Arrays.asList("-2", "-1.5", "-1", "-0.5", "0", "0.5", "1", "1.5", "2");
    private static final double MIN_Y = -3.0;
    private static final double MAX_Y = 3.0;
    
    @GetMapping("/allowed-x")
    public ResponseEntity<List<String>> getAllowedX() {
        return ResponseEntity.ok(ALLOWED_X);
    }
    
    @GetMapping("/allowed-r")
    public ResponseEntity<List<String>> getAllowedR() {
        return ResponseEntity.ok(ALLOWED_R);
    }
    
    @GetMapping("/y-range")
    public ResponseEntity<YRange> getYRange() {
        return ResponseEntity.ok(new YRange(MIN_Y, MAX_Y));
    }
    
    public static class YRange {
        private double min;
        private double max;
        
        public YRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
        
        public double getMin() {
            return min;
        }
        
        public double getMax() {
            return max;
        }
    }
}

