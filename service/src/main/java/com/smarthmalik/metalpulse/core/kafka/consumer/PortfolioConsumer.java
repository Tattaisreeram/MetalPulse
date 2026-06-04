package com.smarthmalik.metalpulse.core.kafka.consumer;

import com.smarthmalik.metalpulse.configuration.KafkaConfig;
import com.smarthmalik.metalpulse.event.TradeExecutedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// Maintains a live Redis hash of each user's metal holdings.
// Key: portfolio:{userId}  Field: metal (e.g. GOLD)  Value: net quantity
// BUY increments, SELL decrements. Redis is the read model; Kafka is the source of truth.
@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioConsumer {

    private static final String PORTFOLIO_KEY_PREFIX = "portfolio:";

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(
            topics           = KafkaConfig.TRADE_EVENTS_TOPIC,
            groupId          = "portfolio-consumer-group",
            containerFactory = KafkaConfig.TRADE_EVENTS_LISTENER_FACTORY
    )
    public void consume(TradeExecutedEvent event) {
        if (event.metal() == null || event.quantity() == null) return;

        String hashKey = PORTFOLIO_KEY_PREFIX + event.userId();
        double delta = switch (event.tradeType()) {
            case "BUY"  ->  event.quantity().doubleValue();
            case "SELL" -> -event.quantity().doubleValue();
            default     ->  0.0;
        };

        if (delta != 0.0) {
            Double updated = redisTemplate.opsForHash().increment(hashKey, event.metal(), delta);
            log.info("[PORTFOLIO] userId={} {} {} {} → holding={}",
                    event.userId(), event.tradeType(), event.quantity(), event.metal(), updated);
        }
    }
}
