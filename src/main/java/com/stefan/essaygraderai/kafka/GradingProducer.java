package com.stefan.essaygraderai.kafka;

import com.stefan.essaygraderai.dto.event.GradingEvent;
import org.springframework.kafka.core.KafkaTemplate;

public class GradingProducer {
    private final KafkaTemplate<String, GradingEvent> kafkaTemplate;

    public GradingProducer(KafkaTemplate<String, GradingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendGradingRequest(GradingEvent event) {
        kafkaTemplate.send("essay-grading", String.valueOf(event.essayId()), event);
    }
}
