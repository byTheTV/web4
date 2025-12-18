package org.example.exception;

public class MaxRadiusExceededException extends RuntimeException {
    
    private final double requestedRadius;
    private final double maxAllowedRadius;
    
    public MaxRadiusExceededException(double requestedRadius, double maxAllowedRadius) {
        super(String.format("Requested radius %.2f exceeds maximum allowed radius %.2f", 
                requestedRadius, maxAllowedRadius));
        this.requestedRadius = requestedRadius;
        this.maxAllowedRadius = maxAllowedRadius;
    }
    
    public double getRequestedRadius() {
        return requestedRadius;
    }
    
    public double getMaxAllowedRadius() {
        return maxAllowedRadius;
    }
}
