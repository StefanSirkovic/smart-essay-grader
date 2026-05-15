package com.stefan.essaygraderai.kafka;

import com.stefan.essaygraderai.dto.event.GradingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GradingProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public GradingProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendGradingRequest(GradingEvent event) {
        kafkaTemplate.send("essay-grading", String.valueOf(event.essayId()), event);
    }
}
