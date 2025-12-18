package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"check_logs\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "check_log_seq")
    @SequenceGenerator(name = "check_log_seq", sequenceName = "\"check_log_seq\"", allocationSize = 1)
    private Long id;
    
    @Column(name = "\"keycloak_id\"", nullable = false)
    private String keycloakId;

    @Column(name = "\"username\"")
    private String username;

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
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
