package com.smarthmalik.metalpulse.core.facade;

import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.service.AssistantService;
import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.AssistantResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssistantFacade {

    private final AssistantService assistantService;

    @CircuitBreaker(name = "assistant", fallbackMethod = "askFallback")
    public ApiResponse<AssistantResponse> ask(User user, String question) {
        AssistantResponse response = assistantService.ask(user, question);
        return ApiResponse.success(response);
    }

    private ApiResponse<AssistantResponse> askFallback(User user, String question, Throwable ex) {
        log.warn("Circuit breaker OPEN — assistant unavailable for userId={}: {}", user.getId(), ex.getMessage(), ex);
        return ApiResponse.success(
                "Assistant temporarily unavailable",
                new AssistantResponse("The assistant is temporarily unavailable right now. Please try again shortly."));
    }
}
