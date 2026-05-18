package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.event.GradingEvent;
import com.stefan.essaygraderai.dto.response.EssayResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.EssayStatus;
import com.stefan.essaygraderai.exception.EssayAlreadyGradedException;
import com.stefan.essaygraderai.exception.EssayNotFoundException;
import com.stefan.essaygraderai.exception.GradingException;
import com.stefan.essaygraderai.kafka.GradingProducer;
import com.stefan.essaygraderai.repository.EssayRepository;
import org.springframework.stereotype.Service;

@Service
public class GradingService {

    private final EssayRepository essayRepository;
    private final GradingProducer gradingProducer;

    public GradingService(EssayRepository essayRepository, GradingProducer gradingProducer) {
        this.essayRepository = essayRepository;
        this.gradingProducer = gradingProducer;
    }

    public EssayResponse gradeEssay(Long essayId, User currentUser) {
        Essay essay = essayRepository.findByIdAndUserId(essayId, currentUser.getId())
                .orElseThrow(() -> new EssayNotFoundException("Essay not found."));

        if (essay.getEssayStatus() != EssayStatus.SUBMITTED) {
            throw new EssayAlreadyGradedException("Essay has already been graded.");
        }

        essay.setEssayStatus(EssayStatus.GRADING);
        essayRepository.save(essay);

        try {
            gradingProducer.sendGradingRequest(new GradingEvent(essayId, currentUser.getId()));
        } catch (Exception ex) {
            essay.setEssayStatus(EssayStatus.FAILED);
            essayRepository.save(essay);
            throw new GradingException("AI grading failed: " + ex.getMessage());
        }

        return new EssayResponse(
                essay.getId(),
                essay.getTitle(),
                essay.getText(),
                essay.getEssayStatus(),
                essay.getSubmittedAt(),
                essay.getUser().getId(),
                essay.getUser().getEmail(),
                null,
                null
        );


    }
}
