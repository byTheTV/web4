package org.example.controller;

import org.example.repository.CheckLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CheckLogRepository checkLogRepository;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getDailyStats(
            @RequestParam(required = false) String date) {

        String targetDate = date != null && !date.isEmpty() ? date : LocalDate.now().toString();
        logger.info("Admin requesting daily stats for date: {}", targetDate);

        LocalDate parsedDate;
            try {
            parsedDate = date != null && !date.isEmpty() 
                    ? LocalDate.parse(date) 
                    : LocalDate.now();
            } catch (Exception e) {
                logger.error("Invalid date format: {}", date);
                return ResponseEntity.badRequest().build();
            }

        // Создаем диапазон дат
        LocalDateTime startOfDay = parsedDate.atStartOfDay();
        LocalDateTime endOfDay = parsedDate.plusDays(1).atStartOfDay();
        
        logger.info("Querying check logs from {} to {}", startOfDay, endOfDay);

        // Используем CHECK_LOGS для статистики
        List<Object[]> stats = checkLogRepository.findDailyStatsByDateRange(startOfDay, endOfDay);
        logger.info("Found {} unique users for date {}", stats.size(), targetDate);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] stat : stats) {
            Map<String, Object> item = new HashMap<>();
            item.put("keycloakId", stat[0]);
            item.put("username", stat[1]);
            // COUNT возвращает BigDecimal в Oracle
            Object countObj = stat[2];
            Long count = countObj instanceof Number ? ((Number) countObj).longValue() : 0L;
            item.put("checkCount", count);
            result.add(item);
            
            logger.debug("User stats - keycloakId: {}, username: {}, count: {}",
                        stat[0], stat[1], count);
        }

        logger.info("Returning stats for {} users", result.size());
        return ResponseEntity.ok(result);
    }
}
