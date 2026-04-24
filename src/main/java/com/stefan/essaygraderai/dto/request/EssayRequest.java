package com.stefan.essaygraderai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EssayRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @NotBlank(message = "Text is required")
        @Size(min = 50)
        String text) {
}
