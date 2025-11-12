package org.example.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("rValidator")
public class RValidator implements Validator<Double> {
    
    private static final double MIN_R = 0.1;
    private static final double MAX_R = 3.0;
    
    @Override
    public void validate(FacesContext context, UIComponent component, Double value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Validation Error", "R value cannot be empty"));
        }
        
        if (value < MIN_R || value > MAX_R) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Validation Error", 
                    String.format("R value must be between %.1f and %.1f", MIN_R, MAX_R)));
        }
    }
}

