package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PointCheckRequest;
import org.example.dto.ResultResponse;
import org.example.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area")
@CrossOrigin(origins = "http://localhost:3000")
public class AreaCheckController {
    
    @Autowired
    private ResultService resultService;
    
    @PostMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkPoint(
            @Valid @RequestBody PointCheckRequest request,
            JwtAuthenticationToken authentication) {

        Jwt jwt = authentication.getToken();
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null || username.isBlank()) {
            username = authentication.getName();
        }
        
        // Проверка maxRadius из токена
        Double maxRadius = null;
        Object maxRadiusClaim = jwt.getClaim("maxRadius");
        if (maxRadiusClaim != null) {
            if (maxRadiusClaim instanceof Number) {
                maxRadius = ((Number) maxRadiusClaim).doubleValue();
            } else if (maxRadiusClaim instanceof String) {
                try {
                    maxRadius = Double.parseDouble((String) maxRadiusClaim);
                } catch (NumberFormatException e) {
                    // Игнорируем некорректное значение
                }
            }
        }
        
        if (maxRadius != null && request.getR() > maxRadius) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("R value (" + request.getR() + ") exceeds maximum allowed value (" + maxRadius + ")");
        }

        ResultResponse result = resultService.checkPoint(
                request.getX(), request.getY(), request.getR(), keycloakId, username);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/results")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ResultResponse>> getResults(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        String keycloakId = jwt.getSubject();
        
        List<ResultResponse> results = resultService.getResultsByKeycloakId(keycloakId);
        return ResponseEntity.ok(results);
    }
}

