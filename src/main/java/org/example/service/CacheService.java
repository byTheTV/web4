package org.example.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.ResultDTO;
import org.example.entities.ResultEntity;
import org.example.mappers.ResultMapper;
import org.example.repository.ResultRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с Hazelcast кешем.
 * Обеспечивает кеширование результатов проверки точек.
 */
@ApplicationScoped
public class CacheService {
    
    private static final String CACHE_MAP_NAME = "results-cache";
    
    @Inject
    private HazelcastInstance hazelcastInstance;
    
    @Inject
    private ResultRepository resultRepository;
    
    private IMap<String, ResultDTO> cacheMap;
    
    @PostConstruct
    public void init() {
        if (hazelcastInstance != null) {
            cacheMap = hazelcastInstance.getMap(CACHE_MAP_NAME);
            System.out.println("CacheService initialized, map name: " + CACHE_MAP_NAME);
        } else {
            System.err.println("HazelcastInstance is null, cache operations will fail");
        }
    }
    
    /**
     * Сохраняет результат в кеш.
     * Не бросает исключения, чтобы не прерывать работу приложения.
     * 
     * @param resultDTO результат для сохранения
     */
    public void put(ResultDTO resultDTO) {
        if (cacheMap == null) {
            System.err.println("Cache map is not initialized");
            return;
        }
        
        try {
            String key = resultDTO.getId() != null ? String.valueOf(resultDTO.getId()) : null;
            if (key == null) {
                // Если ID еще не установлен, генерируем временный ключ
                key = "temp_" + System.currentTimeMillis() + "_" + resultDTO.hashCode();
            }
            cacheMap.put(key, resultDTO);
            System.out.println("Result cached with key: " + key);
        } catch (Exception e) {
            System.err.println("Error putting result into cache: " + e.getMessage());
            e.printStackTrace();
            // Не бросаем исключение, чтобы не прерывать работу приложения
            // Данные останутся в БД, кеш можно будет восстановить позже
        }
    }
    
    /**
     * Сохраняет результат в кеш с указанным ключом (ID).
     * Не бросает исключения, чтобы не прерывать работу приложения.
     * 
     * @param id ID результата
     * @param resultDTO результат для сохранения
     */
    public void put(Long id, ResultDTO resultDTO) {
        if (cacheMap == null) {
            System.err.println("Cache map is not initialized");
            return;
        }
        
        try {
            String key = String.valueOf(id);
            resultDTO.setId(id);
            cacheMap.put(key, resultDTO);
            System.out.println("Result cached with key: " + key);
        } catch (Exception e) {
            System.err.println("Error putting result into cache: " + e.getMessage());
            e.printStackTrace();
            // Не бросаем исключение, чтобы не прерывать работу приложения
            // Данные останутся в БД, кеш можно будет восстановить позже
        }
    }
    
    /**
     * Получает все результаты из кеша.
     * 
     * @return список всех результатов, отсортированных по времени (новые первыми)
     */
    public List<ResultDTO> getAll() {
        if (cacheMap == null) {
            System.err.println("Cache map is not initialized, returning empty list");
            return new ArrayList<>();
        }
        
        try {
            List<ResultDTO> results = new ArrayList<>(cacheMap.values());
            // Сортируем по timestamp (новые первыми)
            results.sort((a, b) -> {
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
            return results;
        } catch (Exception e) {
            System.err.println("Error getting all results from cache: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Очищает весь кеш.
     * Не бросает исключения, чтобы не прерывать работу приложения.
     */
    public void clear() {
        if (cacheMap == null) {
            System.err.println("Cache map is not initialized");
            return;
        }
        
        try {
            cacheMap.clear();
            System.out.println("Cache cleared");
        } catch (Exception e) {
            System.err.println("Error clearing cache: " + e.getMessage());
            e.printStackTrace();
            // Не бросаем исключение, чтобы не прерывать работу приложения
        }
    }
    
    /**
     * Инициализирует кеш данными из базы данных.
     * Загружает все результаты из БД и помещает их в кеш.
     */
    public void initializeFromDatabase() {
        if (cacheMap == null) {
            System.err.println("Cache map is not initialized, cannot initialize from database");
            return;
        }
        
        try {
            System.out.println("Initializing cache from database...");
            List<ResultEntity> entities = resultRepository.findAll();
            
            // Очищаем кеш перед загрузкой
            cacheMap.clear();
            
            // Загружаем все данные из БД в кеш
            for (ResultEntity entity : entities) {
                ResultDTO dto = ResultMapper.toDTO(entity);
                if (dto != null && dto.getId() != null) {
                    String key = String.valueOf(dto.getId());
                    cacheMap.put(key, dto);
                }
            }
            
            System.out.println("Cache initialized with " + entities.size() + " results from database");
        } catch (Exception e) {
            System.err.println("Error initializing cache from database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize cache from database", e);
        }
    }
    
    /**
     * Проверяет, доступен ли кеш.
     * 
     * @return true если кеш доступен, false в противном случае
     */
    public boolean isCacheAvailable() {
        return cacheMap != null && hazelcastInstance != null;
    }
}

