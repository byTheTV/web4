package org.example.controller;

import org.example.repository.CheckLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
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
    public ResponseEntity<List<Map<String, Object>>> getDailyStats(
            @RequestParam(required = false) String date) {

        String targetDate = date != null && !date.isEmpty() ? date : LocalDate.now().toString();
        logger.info("Admin requesting daily stats for date: {}", targetDate);

        Timestamp targetTimestamp;
        if (date != null && !date.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(date);
                targetTimestamp = Timestamp.valueOf(parsedDate.atStartOfDay());
            } catch (Exception e) {
                logger.error("Invalid date format: {}", date);
                return ResponseEntity.badRequest().build();
            }
        } else {
            targetTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        }

        List<Object[]> stats = checkLogRepository.findDailyStatsByDate(targetTimestamp);
        logger.info("Found {} user records for date {}", stats.size(), targetDate);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] stat : stats) {
            Map<String, Object> item = new HashMap<>();
            item.put("keycloakId", stat[0]);
            item.put("username", stat[1]);
            // Oracle возвращает BigDecimal для COUNT, нужно преобразовать
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
