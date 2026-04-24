package com.stefan.essaygraderai.dto.response;

import com.stefan.essaygraderai.enums.EssayStatus;

import java.time.LocalDateTime;

public record EssayResponse(Long id, String title, String text, EssayStatus status, LocalDateTime submittedAt,
                            Long userId, String email) {
}
