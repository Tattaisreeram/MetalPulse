package com.smarthmalik.metalpulse.configuration;

import com.smarthmalik.metalpulse.event.TradeExecutedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    public static final String TRADE_EVENTS_TOPIC            = "trade-events";
    public static final String TRADE_EVENTS_LISTENER_FACTORY = "tradeEventListenerFactory";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public org.apache.kafka.clients.admin.NewTopic tradeEventsTopic() {
        return TopicBuilder.name(TRADE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, TradeExecutedEvent> tradeProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, TradeExecutedEvent> tradeKafkaTemplate(
            ProducerFactory<String, TradeExecutedEvent> tradeProducerFactory) {
        return new KafkaTemplate<>(tradeProducerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TradeExecutedEvent> tradeEventListenerFactory() {
        JsonDeserializer<TradeExecutedEvent> valueDeserializer = new JsonDeserializer<>(TradeExecutedEvent.class);
        valueDeserializer.addTrustedPackages("com.smarthmalik.metalpulse.event");

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrapServers);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,  "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        DefaultKafkaConsumerFactory<String, TradeExecutedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), valueDeserializer);

        ConcurrentKafkaListenerContainerFactory<String, TradeExecutedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
