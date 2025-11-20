package org.example.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.ResultDTO;
import org.example.entities.ResultEntity;
import org.example.mappers.ResultMapper;
import org.example.repository.ResultRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    
    /**
     * Инициализирует кеш данными из базы данных при старте приложения.
     */
    @PostConstruct
    public void init() {
        try {
            System.out.println("ResultService: Initializing cache from database...");
            cacheService.initializeFromDatabase();
        } catch (Exception e) {
            System.err.println("ResultService: Failed to initialize cache from database: " + e.getMessage());
            e.printStackTrace();
            // Не прерываем запуск приложения, продолжаем работу
        }
    }
    
    /**
     * Добавляет результат через DTO.
     * Write-Through паттерн: сохраняет одновременно в БД и кеш.
     * Сначала сохраняет в БД для получения ID, затем сохраняет в кеш с этим ID.
     * Если запись в кеш не удалась, данные остаются в БД (источник истины).
     */
    public void addResult(ResultDTO resultDTO) {
        try {
            // 1. Сохраняем в БД (получаем ID) - это источник истины
            ResultEntity entity = ResultMapper.toEntityForSave(resultDTO);
            repository.save(entity);
            
            // 2. Обновляем DTO с полученным ID
            resultDTO.setId(entity.getId());
            resultDTO.setTimestamp(entity.getTimestamp());
            
            // 3. Сохраняем в кеш с ID (не критично, если не удастся)
            // CacheService.put не бросает исключения, поэтому приложение продолжит работу
            cacheService.put(entity.getId(), resultDTO);
            
            System.out.println("ResultService: Result saved to DB and cache, ID: " + entity.getId());
        } catch (Exception e) {
            System.err.println("ResultService: Error adding result: " + e.getMessage());
            e.printStackTrace();
            // Если ошибка при сохранении в БД, бросаем исключение
            // Если ошибка только в кеше, она уже обработана в CacheService
            throw new RuntimeException("Failed to add result to database", e);
        }
    }
    
    /**
     * Получает все результаты как DTO.
     * Читает только из кеша (не из БД).
     * Если кеш недоступен, делает fallback на БД.
     */
    public List<ResultDTO> getAllResults() {
        try {
            // Пытаемся получить из кеша
            if (cacheService.isCacheAvailable()) {
                List<ResultDTO> cachedResults = cacheService.getAll();
                if (cachedResults != null && !cachedResults.isEmpty()) {
                    System.out.println("ResultService: Retrieved " + cachedResults.size() + " results from cache");
                    return cachedResults;
                }
            }
            
            // Fallback на БД, если кеш недоступен или пуст
            System.out.println("ResultService: Cache unavailable or empty, falling back to database");
            List<ResultEntity> entities = repository.findAll();
            List<ResultDTO> results = entities.stream()
                    .map(ResultMapper::toDTO)
                    .collect(Collectors.toList());
            
            // Попытка восстановить кеш из БД
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
            return List.of(); // Возвращаем пустой список при ошибке
        }
    }
    
    /**
     * Удаляет все результаты.
     * Очищает и кеш, и БД.
     */
    public void clearAllResults() {
        try {
            // Очищаем БД
            repository.deleteAll();
            
            // Очищаем кеш
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

