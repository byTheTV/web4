package org.example.repository;

import org.example.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserIdOrderByTimestampDesc(Long userId);
    void deleteByUserId(Long userId);
}
