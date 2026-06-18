package com.smarthmalik.metalpulse.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The AI assistant's answer to a user's question")
public record AssistantResponse(
        String answer
) {}
