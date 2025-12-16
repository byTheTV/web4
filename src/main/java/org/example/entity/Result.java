package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_seq")
    @SequenceGenerator(name = "result_seq", sequenceName = "result_seq", allocationSize = 1)
    private Long id;
    
    @Column(nullable = false)
    private Double x;
    
    @Column(nullable = false)
    private Double y;
    
    @Column(nullable = false)
    private Double r;
    
    @Column(nullable = false)
    private Boolean hit;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column
    private String executionTime;
    
    @Column(name = "keycloak_id", nullable = false)
    private String keycloakId;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

