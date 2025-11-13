package org.example.beans;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.dto.ResultDTO;
import org.example.service.AreaCalculationService;

import java.io.Serializable;

/**
 * Managed Bean - контроллер для формы проверки точки.
 * Отвечает только за связь с UI (JSF), вся бизнес-логика делегируется в Service.
 */
@Named("areaCheckBean")
@ViewScoped
public class AreaCheckBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private AreaCalculationService calculationService;
    
    @Inject
    private ResultsBean resultsBean;
    
    private Integer x;
    private Double y;
    private Double r = 1.0;
    
    /**
     * Обрабатывает запрос на проверку точки.
     * Делегирует расчет в Service, сохраняет результат через Service.
     */
    public String checkPoint() {
        long startTime = System.nanoTime();
        
        // Делегируем расчет попадания в Service
        boolean hit = calculationService.checkPoint(x, y, r);
        
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        String executionTimeStr = String.format("%.3f мс", executionTime / 1_000_000.0);
        
        // Создаем DTO и сохраняем через ResultsBean (который обновит кэш)
        ResultDTO result = new ResultDTO(x, y, r, hit, executionTimeStr);
        resultsBean.addResult(result);
        
        return null; // Stay on same page to preserve form values
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

