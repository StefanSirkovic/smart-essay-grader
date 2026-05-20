package com.stefan.essaygraderai.kafka;

import com.stefan.essaygraderai.dto.event.GradingEvent;
import com.stefan.essaygraderai.dto.response.GradeResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.Grade;
import com.stefan.essaygraderai.enums.EssayStatus;
import com.stefan.essaygraderai.exception.EssayNotFoundException;
import com.stefan.essaygraderai.repository.EssayRepository;
import com.stefan.essaygraderai.repository.GradeRepository;
import com.stefan.essaygraderai.service.AiService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GradingConsumer {

    private final AiService aiService;
    private final EssayRepository essayRepository;
    private final GradeRepository gradeRepository;
    private final CacheManager cacheManager;

    public GradingConsumer(AiService aiService, EssayRepository essayRepository, GradeRepository gradeRepository, CacheManager cacheManager) {
        this.aiService = aiService;
        this.essayRepository = essayRepository;
        this.gradeRepository = gradeRepository;
        this.cacheManager = cacheManager;
    }

    @KafkaListener(topics = "essay-grading", groupId = "essay-grading-group")
    public void consumeGradingRequest(GradingEvent gradingEvent) {
        Essay essay = essayRepository.findById(gradingEvent.essayId())
                .orElseThrow(() -> new EssayNotFoundException("Essay not found."));
        GradeResponse gradeResponse;
        try {
            gradeResponse = aiService.getData(essay);
        } catch (Exception ex) {
            essay.setEssayStatus(EssayStatus.FAILED);
            essayRepository.save(essay);
            return;
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

        Cache essayCache = cacheManager.getCache("essay");
        Cache essaysCache = cacheManager.getCache("essays");

        if (essayCache != null) essayCache.evict(essay.getUser().getId() + "_" + essay.getId());
        if (essaysCache != null) essaysCache.evict(essay.getUser().getId());


    }


}
