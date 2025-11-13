package org.example.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import org.example.config.Config;

@FacesValidator("xValidator")
public class XValidator implements Validator {
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", "Значение X обязательно для ввода"));
        }
        
        Integer intValue;
        if (value instanceof Integer) {
            intValue = (Integer) value;
        } else if (value instanceof Long) {
            intValue = ((Long) value).intValue();
        } else if (value instanceof Number) {
            intValue = ((Number) value).intValue();
        } else {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", "X должен быть целым числом"));
        }
        
        if (!Config.getAllowedX().contains(intValue)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", 
                    String.format("X должен быть одним из допустимых значений: %s", Config.getAllowedX())));
        }
    }
}

