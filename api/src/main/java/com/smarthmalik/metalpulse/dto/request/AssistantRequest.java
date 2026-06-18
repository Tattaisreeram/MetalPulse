package com.smarthmalik.metalpulse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "A question for the AI trading assistant")
public record AssistantRequest(

        @NotBlank(message = "Question is required")
        @Size(max = 1000, message = "Question must be at most 1000 characters")
        @Schema(example = "What is the spot price of gold and how is it calculated?")
        String question
) {}
