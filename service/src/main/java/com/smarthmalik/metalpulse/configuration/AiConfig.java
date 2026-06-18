package com.smarthmalik.metalpulse.configuration;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// In-memory vector store for the unstructured metals knowledge corpus only.
// Structured per-user data (balance, trades) is never embedded here — see PortfolioTools.
@Configuration
public class AiConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
