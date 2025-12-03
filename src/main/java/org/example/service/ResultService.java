package org.example.service;

import org.example.dto.ResultResponse;
import org.example.entity.Result;
import org.example.entity.User;
import org.example.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {
    
    @Autowired
    private ResultRepository resultRepository;
    
    @Autowired
    private AreaCalculationService areaCalculationService;
    
    public ResultResponse checkPoint(Double x, Double y, Double r, User user) {
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
        result.setUser(user);
        
        result = resultRepository.save(result);
        
        return convertToResponse(result);
    }
    
    public List<ResultResponse> getResultsByUser(User user) {
        return resultRepository.findByUserIdOrderByTimestampDesc(user.getId())
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
