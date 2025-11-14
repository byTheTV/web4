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
        System.out.println("XValidator.validate() вызван, value = " + value + " (type: " + (value != null ? value.getClass().getName() : "null") + ")");
        if (value == null) {
            System.out.println("XValidator: value is null, throwing exception");
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
            System.out.println("XValidator: значение " + intValue + " не в списке разрешенных: " + Config.getAllowedX());
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", 
                    String.format("X должен быть одним из допустимых значений: %s", Config.getAllowedX())));
        }
        System.out.println("XValidator: валидация прошла успешно для значения " + intValue);
    }
}

