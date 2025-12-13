package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PointCheckRequest;
import org.example.dto.ResultResponse;
import org.example.entity.User;
import org.example.service.ResultService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area")
@CrossOrigin(origins = "http://localhost:3000")
public class AreaCheckController {
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/check")
    public ResponseEntity<ResultResponse> checkPoint(
            @Valid @RequestBody PointCheckRequest request,
            JwtAuthenticationToken authentication) {

        User user = userService.resolveUser(authentication);
        
        ResultResponse result = resultService.checkPoint(
                request.getX(), request.getY(), request.getR(), user);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/results")
    public ResponseEntity<List<ResultResponse>> getResults(JwtAuthenticationToken authentication) {
        User user = userService.resolveUser(authentication);
        
        List<ResultResponse> results = resultService.getResultsByUser(user);
        return ResponseEntity.ok(results);
    }
}

