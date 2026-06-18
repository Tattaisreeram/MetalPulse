package com.smarthmalik.metalpulse.core.service.impl;

import com.smarthmalik.metalpulse.core.ai.PortfolioTools;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.service.AssistantService;
import com.smarthmalik.metalpulse.dto.response.AssistantResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final String SYSTEM_PROMPT = """
            You are the MetalPulse trading assistant. MetalPulse is a paper-trading
            platform for precious metals (XAU, XAG, XPT, XPD) — no real money is involved.

            Use the available tools to answer questions about the user's own balance or
            trade history; never guess or invent those numbers. Use the retrieved
            knowledge context to answer general questions about metals, spot pricing, or
            how the platform works. If you don't know the answer, say so plainly. Be
            concise.
            """;

    private final ChatClient chatClient;
    private final PortfolioTools portfolioTools;

    public AssistantServiceImpl(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            PortfolioTools portfolioTools) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
        this.portfolioTools = portfolioTools;
    }

    @Override
    public AssistantResponse ask(User user, String question) {
        String answer = chatClient.prompt()
                .user(question)
                .tools(portfolioTools)
                .toolContext(Map.of("user", user))
                .call()
                .content();
        return new AssistantResponse(answer);
    }
}
