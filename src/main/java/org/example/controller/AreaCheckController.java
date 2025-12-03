package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PointCheckRequest;
import org.example.dto.ResultResponse;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area")
@CrossOrigin(origins = "http://localhost:3000")
public class AreaCheckController {
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/check")
    public ResponseEntity<ResultResponse> checkPoint(
            @Valid @RequestBody PointCheckRequest request,
            Authentication authentication) {
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ResultResponse result = resultService.checkPoint(
                request.getX(), request.getY(), request.getR(), user);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/results")
    public ResponseEntity<List<ResultResponse>> getResults(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<ResultResponse> results = resultService.getResultsByUser(user);
        return ResponseEntity.ok(results);
    }
}

