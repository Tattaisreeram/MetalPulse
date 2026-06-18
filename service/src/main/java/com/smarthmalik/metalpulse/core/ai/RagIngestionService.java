package com.smarthmalik.metalpulse.core.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Ingests the unstructured metals knowledge corpus into the in-memory VectorStore at
// startup. Structured per-user data is never ingested here — see PortfolioTools.
@Slf4j
@Component
@RequiredArgsConstructor
public class RagIngestionService {

    private static final String KNOWLEDGE_BASE_LOCATION = "classpath:/knowledge/*";

    private final VectorStore vectorStore;

    @EventListener(ApplicationReadyEvent.class)
    public void ingestKnowledgeBase() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(KNOWLEDGE_BASE_LOCATION);
            TokenTextSplitter splitter = TokenTextSplitter.builder().build();
            List<Document> chunks = new ArrayList<>();

            for (Resource resource : resources) {
                List<Document> documents = new TextReader(resource).get();
                chunks.addAll(splitter.split(documents));
            }

            if (!chunks.isEmpty()) {
                vectorStore.add(chunks);
            }
            log.info("RAG knowledge base ingested: files={} chunks={}", resources.length, chunks.size());
        } catch (Exception ex) {
            log.error("Failed to ingest RAG knowledge base", ex);
        }
    }
}
