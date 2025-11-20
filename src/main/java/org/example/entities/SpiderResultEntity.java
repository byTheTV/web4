package org.example.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "spider_results")
public class SpiderResultEntity extends ResultEntity {
    
    @Column(name = "legs_quantity")
    private Integer legsQuantity;
    
    public SpiderResultEntity() {
        super();
    }
    
    public SpiderResultEntity(Integer x, Double y, Double r, Boolean hit, String executionTime, Integer legsQuantity) {
        super(x, y, r, hit, executionTime);
        this.legsQuantity = legsQuantity;
    }
    
    public Integer getLegsQuantity() {
        return legsQuantity;
    }
    
    public void setLegsQuantity(Integer legsQuantity) {
        this.legsQuantity = legsQuantity;
    }
}

