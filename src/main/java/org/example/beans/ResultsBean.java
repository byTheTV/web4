package org.example.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.dto.ResultDTO;
import org.example.service.ResultService;

import java.io.Serializable;
import java.util.List;

/**
 * Managed Bean - контроллер для работы с результатами.
 * Отвечает только за связь с UI (JSF), вся бизнес-логика делегируется в Service.
 */
@Named("resultsBean")
@SessionScoped
public class ResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ResultService resultService;
    
    private List<ResultDTO> results;
    
    @PostConstruct
    public void init() {
        refreshResults(); // Загружаем результаты при создании бина
    }
    
    /**
     * Добавляет результат через Service слой.
     */
    public void addResult(ResultDTO resultDTO) {
        resultService.addResult(resultDTO);
        refreshResults(); // Обновляем кэш результатов
    }
    
    /**
     * Возвращает список результатов для отображения в UI.
     */
    public List<ResultDTO> getResults() {
        if (results == null) {
            refreshResults();
        }
        return results;
    }
    
    /**
     * Обновляет кэш результатов из Service.
     */
    private void refreshResults() {
        results = resultService.getAllResults();
    }
    
    /**
     * Очищает все результаты через Service.
     */
    public void clearResults() {
        resultService.clearAllResults();
        refreshResults();
    }
}

