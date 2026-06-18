package com.smarthmalik.metalpulse.core.service;

import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.dto.response.AssistantResponse;

public interface AssistantService {

    AssistantResponse ask(User user, String question);
}
