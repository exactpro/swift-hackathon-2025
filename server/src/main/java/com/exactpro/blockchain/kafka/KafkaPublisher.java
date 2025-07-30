package com.exactpro.blockchain.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

@Component
public class KafkaPublisher {
    private static final Logger logger = LogManager.getLogger(KafkaPublisher.class);

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    private final KafkaSender<String, String> sender = createSender();

    private KafkaSender<String, String> createSender() {
        final Map<String, Object> producerProps = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
            ProducerConfig.ACKS_CONFIG, "all",
            ProducerConfig.RETRIES_CONFIG, 3
        );

        SenderOptions<String, String> senderOptions = SenderOptions.create(producerProps);
        return KafkaSender.create(senderOptions);
    }

    public Mono<Void> publishMessage(String topic, String message, String key) {
        topic = topic.toUpperCase() + "_IN";
        SenderRecord<String, String, String> senderRecord =
            SenderRecord.create(new ProducerRecord<>(topic, key, message), key);

        return sender.send(Mono.just(senderRecord))
            .doOnNext(result -> {
                logger.info("Message sent successfully to topic: {}, partition: {}, offset: {}, key: {}",
                    result.recordMetadata().topic(),
                    result.recordMetadata().partition(),
                    result.recordMetadata().offset(),
                    result.correlationMetadata());
            })
            .doOnError(e -> {
                logger.error("Failed to send message to Kafka. Key: {}. Error: {}", key, e.getMessage(), e);
            })
            .then();
    }

    public Mono<Void> publishMessage(String topic, String xmlMessage) {
        return publishMessage(topic, xmlMessage, null); // Отправляем без ключа
    }
}
