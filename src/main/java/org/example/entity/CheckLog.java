package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"CHECK_LOGS\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "check_log_seq")
    @SequenceGenerator(name = "check_log_seq", sequenceName = "\"CHECK_LOG_SEQ\"", allocationSize = 1)
    private Long id;
    
    @Column(name = "\"KEYCLOAK_ID\"", nullable = false)
    private String keycloakId;

    @Column(name = "\"USERNAME\"")
    private String username;

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
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
