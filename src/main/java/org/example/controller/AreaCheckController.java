package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PointCheckRequest;
import org.example.dto.ResultResponse;
import org.example.service.ResultService;
import org.example.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/area")
@CrossOrigin(origins = "http://localhost:3000")
public class AreaCheckController {

    private static final Logger logger = LoggerFactory.getLogger(AreaCheckController.class);

    @Autowired
    private ResultService resultService;
    
    @Autowired
    private UserContextService userContextService;
    
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

        logger.info("User {} (keycloakId: {}) checking point: x={}, y={}, r={}",
                   username, keycloakId, request.getX(), request.getY(), request.getR());
        
        // Проверка maxRadius через UserContextService
        if (userContextService.isRadiusExceeded(request.getR())) {
            double maxRadius = userContextService.getMaxRadius();
            String errorMessage = String.format(
                "R value (%.1f) exceeds maximum allowed value (%.1f) for user %s",
                request.getR(), maxRadius, username
            );
            logger.warn(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorMessage);
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

