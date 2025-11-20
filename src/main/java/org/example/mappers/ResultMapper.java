package org.example.mappers;

import org.example.dto.ResultDTO;
import org.example.entities.ResultEntity;
import org.example.entities.SpiderResultEntity;
import org.example.entities.AntResultEntity;


public class ResultMapper {
    
    /**
     * Конвертирует Entity в DTO.
     * 
     * @param entity JPA сущность
     * @return DTO объект
     */
    public static ResultDTO toDTO(ResultEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ResultDTO dto = new ResultDTO(
            entity.getId(),
            entity.getX(),
            entity.getY(),
            entity.getR(),
            entity.getHit(),
            entity.getTimestamp(),
            entity.getExecutionTime()
        );
        
        // Определяем тип и заполняем специфичные поля
        if (entity instanceof SpiderResultEntity) {
            SpiderResultEntity spiderEntity = (SpiderResultEntity) entity;
            dto.setType("SPIDER");
            dto.setLegsQuantity(spiderEntity.getLegsQuantity());
        } else if (entity instanceof AntResultEntity) {
            AntResultEntity antEntity = (AntResultEntity) entity;
            dto.setType("ANT");
            dto.setBodyColor(antEntity.getBodyColor());
        }
        
        return dto;
    }
    
    /**
     * Конвертирует DTO в Entity.
     * 
     * @param dto DTO объект
     * @return JPA сущность
     */
    public static ResultEntity toEntity(ResultDTO dto) {
        if (dto == null) {
            return null;
        }
        
        String type = dto.getType();
        if (type == null || type.isEmpty()) {
            type = "SPIDER"; // значение по умолчанию
        }
        
        ResultEntity entity;
        if ("SPIDER".equals(type)) {
            SpiderResultEntity spiderEntity = new SpiderResultEntity(
                dto.getX(),
                dto.getY(),
                dto.getR(),
                dto.getHit(),
                dto.getExecutionTime(),
                dto.getLegsQuantity()
            );
            spiderEntity.setId(dto.getId());
            spiderEntity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now());
            entity = spiderEntity;
        } else if ("ANT".equals(type)) {
            AntResultEntity antEntity = new AntResultEntity(
                dto.getX(),
                dto.getY(),
                dto.getR(),
                dto.getHit(),
                dto.getExecutionTime(),
                dto.getBodyColor()
            );
            antEntity.setId(dto.getId());
            antEntity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now());
            entity = antEntity;
        } else {
            // Fallback на SpiderResultEntity
            SpiderResultEntity spiderEntity = new SpiderResultEntity(
                dto.getX(),
                dto.getY(),
                dto.getR(),
                dto.getHit(),
                dto.getExecutionTime(),
                dto.getLegsQuantity()
            );
            spiderEntity.setId(dto.getId());
            spiderEntity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now());
            entity = spiderEntity;
        }
        
        return entity;
    }
    
    /**
     * Создает Entity из DTO для сохранения в БД (без ID, так как он генерируется автоматически).
     * 
     * @param dto DTO объект
     * @return JPA сущность для сохранения
     */
    public static ResultEntity toEntityForSave(ResultDTO dto) {
        if (dto == null) {
            return null;
        }
        
        String type = dto.getType();
        if (type == null || type.isEmpty()) {
            type = "SPIDER"; // значение по умолчанию
        }
        
        if ("SPIDER".equals(type)) {
            return new SpiderResultEntity(
                dto.getX(),
                dto.getY(),
                dto.getR(),
                dto.getHit(),
                dto.getExecutionTime(),
                dto.getLegsQuantity()
            );
        } else if ("ANT".equals(type)) {
            return new AntResultEntity(
                dto.getX(),
                dto.getY(),
                dto.getR(),
                dto.getHit(),
                dto.getExecutionTime(),
                dto.getBodyColor()
            );
        } else {
            // Fallback на SpiderResultEntity
            return new SpiderResultEntity(
                dto.getX(),
                dto.getY(),
                dto.getR(),
                dto.getHit(),
                dto.getExecutionTime(),
                dto.getLegsQuantity()
            );
        }
    }
}

