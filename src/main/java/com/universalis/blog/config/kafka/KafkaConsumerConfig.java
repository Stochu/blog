package com.universalis.blog.config.kafka;

import com.universalis.blog.messaging.PostCreatedMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Kafka consumer configuration for the blog application.
 *
 * <p>
 * This configuration defines how the application consumes integration messages
 * from Kafka (e.g. {@link com.universalis.blog.messaging.PostCreatedMessage}).
 * It specifies:
 * </p>
 *
 * <ul>
 *   <li>which Kafka cluster to connect to (bootstrap servers)</li>
 *   <li>how messages are grouped and consumed (consumer group id)</li>
 *   <li>how message keys and values are deserialized (UUID + JSON)</li>
 * </ul>
 *
 * <p>
 * The {@link org.springframework.kafka.annotation.KafkaListener} mechanism
 * relies on these beans to create background listener containers that poll
 * Kafka and deliver messages to application code.
 * </p>
 *
 * <p>
 * This class exists to keep Kafka infrastructure concerns separate from
 * domain and business logic.
 * </p>
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<UUID, PostCreatedMessage> consumerFactory() {
        HashMap<String, Object> props = new HashMap<>();
        // where
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // who
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "post-notification-group");
        // how
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // required for JSON deserialization
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.universalis.blog.messaging");
        return new DefaultKafkaConsumerFactory<>(props, new UUIDDeserializer(), new JsonDeserializer<>(PostCreatedMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<UUID, PostCreatedMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<UUID, PostCreatedMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
