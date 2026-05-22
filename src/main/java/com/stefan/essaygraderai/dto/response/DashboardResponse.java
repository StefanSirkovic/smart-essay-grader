package com.stefan.essaygraderai.dto.response;

import com.stefan.essaygraderai.enums.EssayStatus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record DashboardResponse(long totalEssays, Map<EssayStatus, Long> essaysByStatus,
                                Double averageScore, Double highestScore, Double lowestScore,
                                List<EssayResponse> recentEssays) implements Serializable {
}
