package com.stefan.essaygraderai.dto.response;

import java.time.LocalDateTime;

public record GradeResponse(Long id, Double score, String feedback, LocalDateTime gradedAt, Long essayId) {
}
