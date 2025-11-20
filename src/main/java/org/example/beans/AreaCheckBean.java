package org.example.beans;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.dto.ResultDTO;
import org.example.service.AreaCalculationService;

import java.io.Serializable;
import java.util.Random;


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
    private String type = "SPIDER"; // "SPIDER" or "ANT"
    private Integer legsQuantity; // для паука (опционально, если null - рандомное)
    private String bodyColor; // для муравья (опционально, если null - рандомное)
    
    private static final Random random = new Random();
    private static final String[] COLORS = {"black", "brown", "red", "orange", "yellow"};
    
    /**
     * Обрабатывает запрос на проверку точки.
     * Делегирует расчет в Service, сохраняет результат через Service.
     */
    public String checkPoint() {
        // Логирование для отладки
        System.out.println("=== checkPoint() вызван ===");
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("R: " + r);
        
        if (x == null || y == null || r == null) {
            System.out.println("ОШИБКА: Одно из значений null!");
            return null;
        }
        
        long startTime = System.nanoTime();
        
        boolean hit = calculationService.checkPoint(x, y, r);
        
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        String executionTimeStr = String.format("%.3f мс", executionTime / 1_000_000.0);
        
        System.out.println("Результат: hit = " + hit + ", время = " + executionTimeStr);
        
        // Генерация значений для специфичных полей
        Integer finalLegsQuantity = legsQuantity;
        String finalBodyColor = bodyColor;
        
        if ("SPIDER".equals(type)) {
            if (finalLegsQuantity == null) {
                finalLegsQuantity = random.nextInt(100) + 1; // от 1 до 100
            }
        } else if ("ANT".equals(type)) {
            if (finalBodyColor == null || finalBodyColor.trim().isEmpty()) {
                finalBodyColor = COLORS[random.nextInt(COLORS.length)];
            }
        }
        
        ResultDTO result = new ResultDTO(x, y, r, hit, executionTimeStr, type, finalLegsQuantity, finalBodyColor);
        resultsBean.addResult(result);
        
        System.out.println("Результат добавлен в resultsBean");
        System.out.println("=== checkPoint() завершен ===");
        
        return null; 
    }
    
    public Integer getX() {
        return x;
    }
    
    public void setX(Integer x) {
        System.out.println("setX() вызван с значением: " + x);
        this.x = x;
    }
    
    public Double getY() {
        return y;
    }
    
    public void setY(Double y) {
        System.out.println("setY() вызван с значением: " + y);
        this.y = y;
    }
    
    public Double getR() {
        return r;
    }
    
    public void setR(Double r) {
        System.out.println("setR() вызван с значением: " + r);
        this.r = r;
    }
    
    public String setXValue(Integer x) {
        this.x = x;
        return null; 
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

