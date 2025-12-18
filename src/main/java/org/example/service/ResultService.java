package org.example.service;

import org.example.dto.ResultResponse;
import org.example.entity.CheckLog;
import org.example.entity.Result;
import org.example.repository.CheckLogRepository;
import org.example.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

    @Autowired
    private ResultRepository resultRepository;
    
    @Autowired
    private CheckLogRepository checkLogRepository;

    @Autowired
    private AreaCalculationService areaCalculationService;
    
    @Transactional
    public ResultResponse checkPoint(Double x, Double y, Double r, String keycloakId, String username) {
        logger.info("Processing point check for user {} (keycloakId: {}): x={}, y={}, r={}",
                   username, keycloakId, x, y, r);

        long startTime = System.nanoTime();
        boolean hit = areaCalculationService.checkPoint(x, y, r);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        String executionTimeStr = String.format("%.3f мс", executionTime / 1_000_000.0);

        logger.debug("Point check result: hit={}, executionTime={}", hit, executionTimeStr);

        Result result = new Result();
        result.setX(x);
        result.setY(y);
        result.setR(r);
        result.setHit(hit);
        result.setExecutionTime(executionTimeStr);
        result.setKeycloakId(keycloakId);

        result = resultRepository.save(result);
        logger.debug("Saved result with id: {}", result.getId());

        // Логирование в админ-таблицу для статистики
        CheckLog log = new CheckLog();
        log.setKeycloakId(keycloakId);
        log.setUsername(username);
        log.setX(x);
        log.setY(y);
        log.setR(r);
        log.setHit(hit);
        CheckLog savedLog = checkLogRepository.save(log);

        logger.info("Check logged for user {} (keycloakId: {}): logId={}, result={}", 
                   username, keycloakId, savedLog.getId(), hit ? "HIT" : "MISS");

        return convertToResponse(result);
    }
    
    public List<ResultResponse> getResultsByKeycloakId(String keycloakId) {
        return resultRepository.findByKeycloakIdOrderByTimestampDesc(keycloakId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private ResultResponse convertToResponse(Result result) {
        return new ResultResponse(
                result.getId(),
                result.getX(),
                result.getY(),
                result.getR(),
                result.getHit(),
                result.getTimestamp(),
                result.getExecutionTime()
        );
    }
}
