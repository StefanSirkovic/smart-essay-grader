package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.response.GradeResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.Grade;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.EssayStatus;
import com.stefan.essaygraderai.exception.EssayAlreadyGradedException;
import com.stefan.essaygraderai.exception.EssayNotFoundException;
import com.stefan.essaygraderai.exception.GradingException;
import com.stefan.essaygraderai.repository.EssayRepository;
import com.stefan.essaygraderai.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GradingService {

    private final EssayRepository essayRepository;
    private final GradeRepository gradeRepository;
    private final AiService aiService;

    public GradingService(EssayRepository essayRepository, AiService aiService, GradeRepository gradeRepository) {
        this.essayRepository = essayRepository;
        this.aiService = aiService;
        this.gradeRepository = gradeRepository;
    }

    public GradeResponse gradeEssay(Long essayId, User currentUser) {
        Essay essay = essayRepository.findByIdAndUserId(essayId, currentUser.getId())
                .orElseThrow(() -> new EssayNotFoundException("Essay not found."));

        if (essay.getEssayStatus() != EssayStatus.SUBMITTED) {
            throw new EssayAlreadyGradedException("Essay has already been graded.");
        }

        essay.setEssayStatus(EssayStatus.GRADING);
        essayRepository.save(essay);

        GradeResponse gradeResponse;
        try {
            gradeResponse = aiService.getData(essay);
        } catch (Exception ex) {
            essay.setEssayStatus(EssayStatus.FAILED);
            essayRepository.save(essay);
            throw new GradingException("AI grading failed: " + ex.getMessage());
        }
        Grade grade = Grade.builder()
                .score(gradeResponse.score())
                .feedback(gradeResponse.feedback())
                .gradedAt(LocalDateTime.now())
                .essay(essay)
                .build();
        gradeRepository.save(grade);

        essay.setEssayStatus(EssayStatus.GRADED);
        essayRepository.save(essay);


        return new GradeResponse(grade.getId(), grade.getScore(),
                grade.getFeedback(), grade.getGradedAt(), essayId);


    }
}
