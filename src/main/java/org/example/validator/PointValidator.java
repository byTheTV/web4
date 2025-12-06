package org.example.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PointValidator.XRValidator.class)
@Documented
public @interface PointValidator {
    String message() default "Invalid value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    class XRValidator implements ConstraintValidator<PointValidator, Double> {
        private static final List<Double> ALLOWED_VALUES = Arrays.asList(-2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0);
        
        @Override
        public void initialize(PointValidator constraintAnnotation) {
        }
        
        @Override
        public boolean isValid(Double value, ConstraintValidatorContext context) {
            if (value == null) {
                return false;
            }
            return ALLOWED_VALUES.stream()
                    .anyMatch(allowed -> Math.abs(value - allowed) < 0.001);
        }
    }
}

