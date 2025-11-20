package org.example.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ant_results")
public class AntResultEntity extends ResultEntity {
    
    @Column(name = "body_color")
    private String bodyColor;
    
    public AntResultEntity() {
        super();
    }
    
    public AntResultEntity(Integer x, Double y, Double r, Boolean hit, String executionTime, String bodyColor) {
        super(x, y, r, hit, executionTime);
        this.bodyColor = bodyColor;
    }
    
    public String getBodyColor() {
        return bodyColor;
    }
    
    public void setBodyColor(String bodyColor) {
        this.bodyColor = bodyColor;
    }
}

