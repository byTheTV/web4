package org.example.repository;

import org.example.entity.CheckLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheckLogRepository extends JpaRepository<CheckLog, Long> {
    
    @Query(value = "SELECT cl.\"KEYCLOAK_ID\", cl.\"USERNAME\", COUNT(cl.id) as check_count " +
           "FROM \"CHECK_LOGS\" cl " +
           "WHERE TRUNC(cl.\"TIMESTAMP\") = TRUNC(:date) " +
           "GROUP BY cl.\"KEYCLOAK_ID\", cl.\"USERNAME\" " +
           "ORDER BY check_count DESC", nativeQuery = true)
    List<Object[]> findDailyStatsByDate(@Param("date") java.sql.Timestamp date);
}
