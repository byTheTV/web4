package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"results\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_seq")
    @SequenceGenerator(name = "result_seq", sequenceName = "\"result_seq\"", allocationSize = 1)
    private Long id;
    
    @Column(name = "\"x\"", nullable = false)
    private Double x;

    @Column(name = "\"y\"", nullable = false)
    private Double y;

    @Column(name = "\"r\"", nullable = false)
    private Double r;

    @Column(name = "\"hit\"", nullable = false)
    private Boolean hit;

    @Column(name = "\"timestamp\"", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "\"execution_time\"")
    private String executionTime;
    
    @Column(name = "\"keycloak_id\"", nullable = false)
    private String keycloakId;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

