package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
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
        
        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, request.getUsername()));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getPassword());
            String token = jwtService.generateToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(token, user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

