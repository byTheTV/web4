package org.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.example.validator.PointValidator;
import lombok.Data;

@Data
public class PointCheckRequest {
    @NotNull(message = "X is required")
    @PointValidator(message = "X must be one of: -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2")
    private Double x;
    
    @NotNull(message = "Y is required")
    @DecimalMin(value = "-3.0", message = "Y must be at least -3.0")
    @DecimalMax(value = "3.0", message = "Y must be at most 3.0")
    private Double y;
    
    @NotNull(message = "R is required")
    @PointValidator(message = "R must be one of: -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2")
    private Double r;
}

