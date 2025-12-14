package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.dto.RefreshTokenRequest;
import org.example.entity.User;
import org.example.service.JwtService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.findByUsername(request.getUsername());
        
        if (userOpt.isEmpty() || !userService.validatePassword(request.getPassword(), userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        
        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, request.getUsername()));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getPassword());
            String accessToken = jwtService.generateAccessToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(401).body("Refresh token is required");
            }
            
            // Проверяем, что токен валиден и не истек
            if (jwtService.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(401).body("Refresh token has expired");
            }
            
            String username = jwtService.extractUsername(refreshToken);
            
            // Проверяем, что пользователь существует
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("User not found");
            }
            
            // Генерируем новый access token
            String newAccessToken = jwtService.generateAccessToken(username);
            
            return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken, username));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }
}

