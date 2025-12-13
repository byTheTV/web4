package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User resolveUser(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        String keycloakId = jwt.getSubject();
        String username = Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
                .orElse(authentication.getName());

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setKeycloakId(keycloakId);
                    user.setUsername(username);
                    // Existing schema expects a non-null value; store placeholder for Keycloak users
                    user.setPasswordHash("keycloak-external");
                    return userRepository.save(user);
                });
    }
}

