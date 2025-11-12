package org.example.models;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
