package org.example.repository;

import org.example.entity.CheckLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckLogRepository extends JpaRepository<CheckLog, Long> {
    
    // Статистика проверок по пользователям за период
    @Query(value = "SELECT cl.\"keycloak_id\", cl.\"username\", COUNT(cl.\"id\") as check_count " +
           "FROM \"check_logs\" cl " +
           "WHERE cl.\"timestamp\" >= :startDate AND cl.\"timestamp\" < :endDate " +
           "GROUP BY cl.\"keycloak_id\", cl.\"username\" " +
           "ORDER BY check_count DESC", nativeQuery = true)
    List<Object[]> findDailyStatsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
