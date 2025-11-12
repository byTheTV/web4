package org.example.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("yValidator")
public class YValidator implements Validator<Double> {
    
    private static final double MIN_Y = -3.0;
    private static final double MAX_Y = 3.0;
    
    @Override
    public void validate(FacesContext context, UIComponent component, Double value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Validation Error", "Y value cannot be empty"));
        }
        
        if (value < MIN_Y || value > MAX_Y) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Validation Error", 
                    String.format("Y value must be between %.1f and %.1f", MIN_Y, MAX_Y)));
        }
    }
}

