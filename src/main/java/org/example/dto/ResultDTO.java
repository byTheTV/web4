package org.example.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для передачи данных результата проверки точки
 * между слоями приложения без привязки к JPA сущности.
 */
public class ResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Integer x;
    private Double y;
    private Double r;
    private Boolean hit;
    private LocalDateTime timestamp;
    private String executionTime;
    
    public ResultDTO() {
    }
    
    public ResultDTO(Integer x, Double y, Double r, Boolean hit, String executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.executionTime = executionTime;
        this.timestamp = LocalDateTime.now();
    }
    
    public ResultDTO(Long id, Integer x, Double y, Double r, Boolean hit, LocalDateTime timestamp, String executionTime) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timestamp = timestamp;
        this.executionTime = executionTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getX() {
        return x;
    }
    
    public void setX(Integer x) {
        this.x = x;
    }
    
    public Double getY() {
        return y;
    }
    
    public void setY(Double y) {
        this.y = y;
    }
    
    public Double getR() {
        return r;
    }
    
    public void setR(Double r) {
        this.r = r;
    }
    
    public Boolean getHit() {
        return hit;
    }
    
    public void setHit(Boolean hit) {
        this.hit = hit;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }
}

