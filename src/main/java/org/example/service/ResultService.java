package org.example.service;

import org.example.dto.ResultResponse;
import org.example.entity.CheckLog;
import org.example.entity.Result;
import org.example.repository.CheckLogRepository;
import org.example.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {
    
    @Autowired
    private ResultRepository resultRepository;
    
    @Autowired
    private CheckLogRepository checkLogRepository;
    
    @Autowired
    private AreaCalculationService areaCalculationService;
    
    @Transactional
    public ResultResponse checkPoint(Double x, Double y, Double r, String keycloakId, String username) {
        long startTime = System.nanoTime();
        boolean hit = areaCalculationService.checkPoint(x, y, r);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        String executionTimeStr = String.format("%.3f мс", executionTime / 1_000_000.0);
        
        Result result = new Result();
        result.setX(x);
        result.setY(y);
        result.setR(r);
        result.setHit(hit);
        result.setExecutionTime(executionTimeStr);
        result.setKeycloakId(keycloakId);
        
        result = resultRepository.save(result);
        
        // Логирование информации о пользователе
        CheckLog log = new CheckLog();
        log.setKeycloakId(keycloakId);
        log.setUsername(username);
        log.setX(x);
        log.setY(y);
        log.setR(r);
        log.setHit(hit);
        checkLogRepository.save(log);
        
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
