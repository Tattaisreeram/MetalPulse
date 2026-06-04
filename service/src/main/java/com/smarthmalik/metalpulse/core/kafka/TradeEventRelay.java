package com.smarthmalik.metalpulse.core.kafka;

import com.smarthmalik.metalpulse.configuration.KafkaConfig;
import com.smarthmalik.metalpulse.event.TradeExecutedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// Bridges the Spring transaction context to Kafka.
// AFTER_COMMIT guarantees no message is sent if the DB transaction rolls back (avoids dual-write).
@Slf4j
@Component
@RequiredArgsConstructor
public class TradeEventRelay {

    private final KafkaTemplate<String, TradeExecutedEvent> tradeKafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void relay(TradeExecutedEvent event) {
        String partitionKey = event.userId().toString();
        tradeKafkaTemplate.send(KafkaConfig.TRADE_EVENTS_TOPIC, partitionKey, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish TradeExecutedEvent tradeId={}: {}",
                                event.tradeId(), ex.getMessage());
                    } else {
                        log.debug("TradeExecutedEvent published: tradeId={} type={} partition={}",
                                event.tradeId(), event.tradeType(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
