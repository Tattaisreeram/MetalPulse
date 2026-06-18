package com.smarthmalik.metalpulse.core.controller;

import com.smarthmalik.metalpulse.dto.request.AssistantRequest;
import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.AssistantResponse;
import com.smarthmalik.metalpulse.core.constant.URLConstants;
import com.smarthmalik.metalpulse.core.facade.AssistantFacade;
import com.smarthmalik.metalpulse.core.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Assistant", description = "Ask the AI assistant about your portfolio or precious metals")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantFacade assistantFacade;

    @Operation(summary = "Ask the AI assistant a question about your account or precious metals")
    @PostMapping(URLConstants.ASSISTANT_ASK)
    public ApiResponse<AssistantResponse> ask(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AssistantRequest request) {
        return assistantFacade.ask(principal.getUser(), request.question());
    }
}
