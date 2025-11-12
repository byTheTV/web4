package org.example.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
public class ResultEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer x;
    
    @Column(nullable = false)
    private Double y;
    
    @Column(nullable = false)
    private Double r;
    
    @Column(nullable = false)
    private Boolean hit;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column
    private String executionTime;
    
    public ResultEntity() {
    }
    
    public ResultEntity(Integer x, Double y, Double r, Boolean hit, String executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.executionTime = executionTime;
        this.timestamp = LocalDateTime.now();
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

