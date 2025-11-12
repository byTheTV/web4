package org.example.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Named("startPageBean")
@RequestScoped
public class StartPageBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    
    public String getStudentName() {
        return "Тарасов Владислав Павлович";
    }
    
    public String getGroupNumber() {
        return "P3219";
    }
    
    public String getVariantNumber() {
        return "Вариант 6633";
    }
    
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(FORMATTER);
    }
}

