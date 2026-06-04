package com.smarthmalik.metalpulse.core.kafka.consumer;

import com.smarthmalik.metalpulse.configuration.KafkaConfig;
import com.smarthmalik.metalpulse.event.TradeExecutedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradeAuditConsumer {

    @KafkaListener(
            topics          = KafkaConfig.TRADE_EVENTS_TOPIC,
            groupId         = "audit-consumer-group",
            containerFactory = KafkaConfig.TRADE_EVENTS_LISTENER_FACTORY
    )
    public void consume(TradeExecutedEvent event) {
        log.info("[AUDIT] tradeId={} userId={} username={} type={} metal={} qty={} total={} currency={} at={}",
                event.tradeId(),
                event.userId(),
                event.username(),
                event.tradeType(),
                event.metal(),
                event.quantity(),
                event.totalAmount(),
                event.currency(),
                event.occurredAt());
    }
}
