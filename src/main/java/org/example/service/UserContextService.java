package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для извлечения информации о пользователе из JWT токена
 */
@Service
public class UserContextService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserContextService.class);
    private static final double DEFAULT_MAX_RADIUS = 2.0;
    
    public String getCurrentKeycloakId() {
        return getCurrentJwt()
                .map(Jwt::getSubject)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
    }
    
    public String getCurrentUsername() {
        return getCurrentJwt()
                .map(jwt -> {
                    String username = jwt.getClaimAsString("preferred_username");
                    if (username == null || username.isBlank()) {
                        username = jwt.getSubject();
                    }
                    return username;
                })
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
    }

    public double getMaxRadius() {
        return getCurrentJwt()
                .flatMap(this::extractMaxRadiusFromJwt)
                .orElse(DEFAULT_MAX_RADIUS);
    }
    

    public boolean isRadiusExceeded(double radius) {
        double maxRadius = getMaxRadius();
        boolean exceeded = radius > maxRadius;
        
        if (exceeded) {
            logger.warn("User {} attempted to use radius {} which exceeds maximum allowed {}",
                    getCurrentUsername(), radius, maxRadius);
        }
        
        return exceeded;
    }
    
    /**
     * Получить текущий JWT токен
     */
    private Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return Optional.of(jwtAuth.getToken());
        }
        
        return Optional.empty();
    }
    
    /**
     * Извлечь значение maxRadius из JWT токена
     */
    private Optional<Double> extractMaxRadiusFromJwt(Jwt jwt) {
        try {
            Object maxRadiusClaim = jwt.getClaim("maxRadius");
            
            if (maxRadiusClaim == null) {
                logger.debug("maxRadius claim not found for user {}, using default value", 
                        jwt.getClaimAsString("preferred_username"));
                return Optional.empty();
            }
            
            // Обработка разных типов данных
            if (maxRadiusClaim instanceof Number) {
                return Optional.of(((Number) maxRadiusClaim).doubleValue());
            } else if (maxRadiusClaim instanceof String) {
                String strValue = (String) maxRadiusClaim;
                if (!strValue.isBlank()) {
                    return Optional.of(Double.parseDouble(strValue));
                }
            }
            
            logger.warn("Invalid maxRadius claim format for user {}: {}", 
                    jwt.getClaimAsString("preferred_username"), maxRadiusClaim);
            return Optional.empty();
            
        } catch (NumberFormatException e) {
            logger.error("Failed to parse maxRadius claim for user {}", 
                    jwt.getClaimAsString("preferred_username"), e);
            return Optional.empty();
        }
    }
}
