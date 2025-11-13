package org.example.service;

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
 * Не является managed bean для JSF, но является CDI бином.
 */
@ApplicationScoped
public class ResultService {
    
    @Inject
    private ResultRepository repository;
    
    /**
     * Добавляет результат через DTO.
     * Конвертирует DTO в Entity и сохраняет через Repository.
     */
    public void addResult(ResultDTO resultDTO) {
        ResultEntity entity = ResultMapper.toEntityForSave(resultDTO);
        repository.save(entity);
    }
    
    /**
     * Получает все результаты как DTO.
     * Загружает Entity из Repository и конвертирует в DTO.
     */
    public List<ResultDTO> getAllResults() {
        List<ResultEntity> entities = repository.findAll();
        return entities.stream()
                .map(ResultMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Удаляет все результаты.
     */
    public void clearAllResults() {
        repository.deleteAll();
    }
}

