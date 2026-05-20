package com.stefan.essaygraderai.dto.response;

import com.stefan.essaygraderai.enums.EssayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record EssayResponse(Long id, String title, String text, EssayStatus status, LocalDateTime submittedAt,
                            Long userId, String email, Double score, String feedback) implements Serializable {
}
