package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.response.DashboardResponse;
import com.stefan.essaygraderai.dto.response.EssayResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.EssayStatus;
import com.stefan.essaygraderai.repository.EssayRepository;
import com.stefan.essaygraderai.repository.GradeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final EssayRepository essayRepository;
    private final GradeRepository gradeRepository;

    public DashboardService(EssayRepository essayRepository, GradeRepository gradeRepository) {
        this.essayRepository = essayRepository;
        this.gradeRepository = gradeRepository;
    }

    @Cacheable(value = "dashboard", key = "#currentUser.id")
    public DashboardResponse getDashboard(User currentUser) {
        Long userId = currentUser.getId();

        long totalEssays = essayRepository.countByUserId(userId);

        Map<EssayStatus, Long> essaysByStatus = new HashMap<>();
        for (EssayStatus status : EssayStatus.values()) {
            essaysByStatus.put(status, essayRepository.countByUserIdAndEssayStatus(userId, status));
        }

        Double avg = gradeRepository.findAverageScoreByUserId(userId);
        Double max = gradeRepository.findMaximumScoreByUserId(userId);
        Double min = gradeRepository.findMinimumScoreByUserId(userId);

        List<EssayResponse> recentEssays = essayRepository.findTop5ByUserIdOrderBySubmittedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new DashboardResponse(totalEssays, essaysByStatus, avg, max, min, recentEssays);
    }

    private EssayResponse mapToResponse(Essay essay) {
        return new EssayResponse(
                essay.getId(),
                essay.getTitle(),
                essay.getText(),
                essay.getEssayStatus(),
                essay.getSubmittedAt(),
                essay.getUser().getId(),
                essay.getUser().getEmail(),
                essay.getGrade() != null ? essay.getGrade().getScore() : null,
                essay.getGrade() != null ? essay.getGrade().getFeedback() : null
        );
    }

}
