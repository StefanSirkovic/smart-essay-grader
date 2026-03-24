package com.stefan.essaygraderai.dto.response;

import com.stefan.essaygraderai.enums.Role;

public record AuthResponse(String token, Long userId, String email, Role role) {
}
