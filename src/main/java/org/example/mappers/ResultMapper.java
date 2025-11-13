package org.example.mappers;

import org.example.dto.ResultDTO;
import org.example.entities.ResultEntity;

/**
 * Mapper для конвертации между Entity (модель) и DTO (слой контроллера).
 */
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
        
        return new ResultDTO(
            entity.getId(),
            entity.getX(),
            entity.getY(),
            entity.getR(),
            entity.getHit(),
            entity.getTimestamp(),
            entity.getExecutionTime()
        );
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
        
        ResultEntity entity = new ResultEntity();
        entity.setId(dto.getId());
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setR(dto.getR());
        entity.setHit(dto.getHit());
        entity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now());
        entity.setExecutionTime(dto.getExecutionTime());
        
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
        
        return new ResultEntity(
            dto.getX(),
            dto.getY(),
            dto.getR(),
            dto.getHit(),
            dto.getExecutionTime()
        );
    }
}

