package org.example.dto;

import java.io.Serializable;
import java.time.LocalDateTime;


public class ResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Integer x;
    private Double y;
    private Double r;
    private Boolean hit;
    private LocalDateTime timestamp;
    private String executionTime;
    private String type; // "SPIDER" or "ANT"
    private Integer legsQuantity; // для паука
    private String bodyColor; // для муравья
    
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
    
    public ResultDTO(Integer x, Double y, Double r, Boolean hit, String executionTime, String type, Integer legsQuantity, String bodyColor) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.executionTime = executionTime;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.legsQuantity = legsQuantity;
        this.bodyColor = bodyColor;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getLegsQuantity() {
        return legsQuantity;
    }
    
    public void setLegsQuantity(Integer legsQuantity) {
        this.legsQuantity = legsQuantity;
    }
    
    public String getBodyColor() {
        return bodyColor;
    }
    
    public void setBodyColor(String bodyColor) {
        this.bodyColor = bodyColor;
    }
}

