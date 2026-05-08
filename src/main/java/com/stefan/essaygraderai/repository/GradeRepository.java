package com.stefan.essaygraderai.repository;

import com.stefan.essaygraderai.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByEssayId(Long essayId);
}
