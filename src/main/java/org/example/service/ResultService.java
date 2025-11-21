package org.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.dto.ResultDTO;
import org.example.entities.ResultEntity;
import org.example.mappers.ResultMapper;
import org.example.repository.ResultRepository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service слой - содержит бизнес-логику работы с результатами.
 * Использует паттерн Write-Through: запись в кеш и БД, чтение только из кеша.
 * Не является managed bean для JSF, но является CDI бином.
 */
@ApplicationScoped
public class ResultService {
    
    @Inject
    private ResultRepository repository;
    
    @Inject
    private CacheService cacheService;
    

    @PostConstruct
    public void init() {
        try {
            System.out.println("ResultService: Initializing cache from database...");
            cacheService.initializeFromDatabase();
        } catch (Exception e) {
            System.err.println("ResultService: Failed to initialize cache from database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void addResult(ResultDTO resultDTO) {
        try {
            ResultEntity entity = ResultMapper.toEntityForSave(resultDTO);
            repository.save(entity);
            
            resultDTO.setId(entity.getId());
            resultDTO.setTimestamp(entity.getTimestamp());
            
            cacheService.put(entity.getId(), resultDTO);
            
            System.out.println("ResultService: Result saved to DB and cache, ID: " + entity.getId());
        } catch (Exception e) {
            System.err.println("ResultService: Error adding result: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add result to database", e);
        }
    }
    

    public List<ResultDTO> getAllResults() {
        try {
            if (cacheService.isCacheAvailable()) {
                List<ResultDTO> cachedResults = cacheService.getAll();
                if (cachedResults != null && !cachedResults.isEmpty()) {
                    System.out.println("ResultService: Retrieved " + cachedResults.size() + " results from cache");
                    return cachedResults;
                }
            }
            
            System.out.println("ResultService: Cache unavailable or empty, falling back to database");
            List<ResultEntity> entities = repository.findAll();
            List<ResultDTO> results = entities.stream()
                    .map(ResultMapper::toDTO)
                    .collect(Collectors.toList());
            
            if (cacheService.isCacheAvailable() && !results.isEmpty()) {
                try {
                    cacheService.clear();
                    for (ResultDTO dto : results) {
                        if (dto.getId() != null) {
                            cacheService.put(dto.getId(), dto);
                        }
                    }
                    System.out.println("ResultService: Cache restored from database");
                } catch (Exception e) {
                    System.err.println("ResultService: Failed to restore cache: " + e.getMessage());
                }
            }
            
            return results;
        } catch (Exception e) {
            System.err.println("ResultService: Error getting all results: " + e.getMessage());
            e.printStackTrace();
            return List.of(); 
        }
    }
    
    public void clearAllResults() {
        try {
            repository.deleteAll();
            
            if (cacheService.isCacheAvailable()) {
                cacheService.clear();
            }
            
            System.out.println("ResultService: All results cleared from DB and cache");
        } catch (Exception e) {
            System.err.println("ResultService: Error clearing all results: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to clear all results", e);
        }
    }
}

