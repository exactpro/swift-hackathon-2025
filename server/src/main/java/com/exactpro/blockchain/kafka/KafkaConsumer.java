package com.exactpro.blockchain.kafka;

import com.exactpro.blockchain.CustomerCreditTransferConverter;
import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.entity.TransferStatus;
import com.exactpro.blockchain.repository.TransferRepository;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.XmlCodec;
import jakarta.xml.bind.JAXBException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaConsumer {
    private static final Logger logger = LogManager.getLogger(KafkaConsumer.class);

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${client.bic}")
    private String bic;

    private String getTopic() {
        return bic.toUpperCase() + "_IN";
    }

    private final XmlCodec xmlCodec;
    private final CustomerCreditTransferConverter converter;
    private final TransferRepository transferRepository;

    public KafkaConsumer(XmlCodec xmlCodec,
                         CustomerCreditTransferConverter converter,
                         TransferRepository transferRepository) {
        this.xmlCodec = xmlCodec;
        this.converter = converter;
        this.transferRepository = transferRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        // This method will be called when the application context is refreshed
        // which is a good place to start the Kafka consumer.
        logger.info("Starting Kafka consumer");
        try {
            createTopicIfNeeded();
            startConsumer();
        } catch (Exception e) {
            logger.error("Failed to start Kafka consumer", e);
        }
    }

    private void createTopicIfNeeded() throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(props)) {
            String topic = getTopic();
            if (!adminClient.listTopics().names().get().contains(topic)) {
                logger.info("Topic '{}' does not exist, creating it.", topic);
                adminClient.createTopics(Collections.singleton(new NewTopic(topic, 1, (short) 1))).all().get();
            }
        }
    }

    private void startConsumer() {
        final Map<String, Object> consumerProps = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG, "blockchain-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer",
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer"
        );

        String topic = getTopic();
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.<String, String>create(consumerProps)
            .subscription(Collections.singleton(topic));

        KafkaReceiver.create(receiverOptions)
            .receive()
            .doOnNext(this::processMessage)
            .subscribe();
    }

    private void processMessage(@NonNull ReceiverRecord<String, String> record) {
        logger.info("Received message: {}", record.value());

        Mono.just(record.value())
            .flatMapMany(message -> {
                try {
                    CustomerCreditTransfer creditTransfer = xmlCodec.decode(message);
                    List<Transfer> transferEntities = converter.convertToTransfer(creditTransfer, TransferStatus.COMPLETED);

                    if (transferEntities.isEmpty()) {
                        return Flux.empty();
                    }

                    return Flux.fromIterable(transferEntities).flatMap(transferRepository::save);
                } catch (IOException | TransformerException | SAXException | JAXBException e) {
                    throw new RuntimeException(e);
                }
            })
            .then()
            .subscribe(null,
                error -> {},
                () -> record.receiverOffset().acknowledge()
            );
    }
}
