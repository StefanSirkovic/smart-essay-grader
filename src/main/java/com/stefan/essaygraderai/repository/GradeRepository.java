package com.stefan.essaygraderai.repository;

import com.stefan.essaygraderai.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByEssayId(Long essayId);

    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.essay.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(g.score) FROM Grade g WHERE g.essay.user.id = :userId")
    Double findMaximumScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT MIN(g.score) FROM Grade g WHERE g.essay.user.id = :userId")
    Double findMinimumScoreByUserId(@Param("userId") Long userId);
}
