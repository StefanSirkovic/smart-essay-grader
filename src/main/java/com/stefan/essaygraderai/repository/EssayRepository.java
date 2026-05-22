package com.stefan.essaygraderai.repository;

import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.EssayStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EssayRepository extends JpaRepository<Essay, Long> {

    List<Essay> findByUserId(Long userId);

    Optional<Essay> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndEssayStatus(Long userId, EssayStatus status);

    List<Essay> findTop5ByUserIdOrderBySubmittedAtDesc(Long userId);

    Long user(User user);
}
