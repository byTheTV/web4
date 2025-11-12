package org.example.beans;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.entities.ResultEntity;

import java.io.Serializable;

@Named("areaCheckBean")
@ViewScoped
public class AreaCheckBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ResultsBean resultsBean;
    
    private Integer x;
    private Double y;
    private Double r = 1.0;
    
    public String checkPoint() {
        long startTime = System.nanoTime();
        
        boolean hit = calculate(x, y, r);
        
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        String executionTimeStr = String.format("%.3f мс", executionTime / 1_000_000.0);
        
        ResultEntity result = new ResultEntity(x, y, r, hit, executionTimeStr);
        resultsBean.addResult(result);
        
        return "main?faces-redirect=true";
    }
    
    
    private boolean calculate(Integer x, Double y, Double r) {
        if (x == null || y == null || r == null || r <= 0) {
            return false;
        }
        
        // First quadrant: triangle with vertices (0,0), (R,0), (0,R)
        // Condition: x >= 0, y >= 0, x + y <= R
        if (x >= 0 && y >= 0) {
            return (x + y) <= r;
        }
        
        // Second quadrant: rectangle from (0,0) to (-R/2, R)
        // Condition: x <= 0, y >= 0, x >= -R/2, y <= R
        if (x <= 0 && y >= 0) {
            return x >= -r / 2 && y <= r;
        }
        
        // Fourth quadrant: quarter circle centered at (0,0) with radius R/2
        // Condition: x >= 0, y <= 0, x^2 + y^2 <= (R/2)^2
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r / 2) * (r / 2);
        }
        
        return false;
    }
    
    // Getters and Setters
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
    
    public String setXValue(Integer x) {
        this.x = x;
        return null; // Stay on same page
    }
}

