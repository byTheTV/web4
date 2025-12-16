package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"RESULTS\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_seq")
    @SequenceGenerator(name = "result_seq", sequenceName = "\"RESULT_SEQ\"", allocationSize = 1)
    private Long id;
    
    @Column(name = "\"X\"", nullable = false)
    private Double x;

    @Column(name = "\"Y\"", nullable = false)
    private Double y;

    @Column(name = "\"R\"", nullable = false)
    private Double r;

    @Column(name = "\"HIT\"", nullable = false)
    private Boolean hit;

    @Column(name = "\"TIMESTAMP\"", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "\"EXECUTION_TIME\"")
    private String executionTime;
    
    @Column(name = "\"KEYCLOAK_ID\"", nullable = false)
    private String keycloakId;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

