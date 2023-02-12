package org.shaft.administration.eventprocessor.consumer.impl;

import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventprocessor.consumer.KafkaConsumer;
import org.shaft.administration.eventprocessor.transformer.AvroToShaftEventTransformer;
import org.shaft.administration.kafka.admin.client.KafkaAdminClient;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
@Service
public class ShaftEventKafkaConsumer implements KafkaConsumer<Long, EventAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ShaftEventKafkaConsumer.class);
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final AvroToShaftEventTransformer avroToShaftEventTransformer;


    public ShaftEventKafkaConsumer(KafkaListenerEndpointRegistry listenerEndpointRegistry,
                                   KafkaAdminClient adminClient,
                                   KafkaConfigData configData, AvroToShaftEventTransformer avroToShaftEventTransformer) {
        this.kafkaListenerEndpointRegistry = listenerEndpointRegistry;
        this.kafkaAdminClient = adminClient;
        this.kafkaConfigData = configData;
        this.avroToShaftEventTransformer = avroToShaftEventTransformer;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("trackEventListener").start();
    }

    @Override
    @KafkaListener(id = "trackEventListener", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<EventAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Long> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        try {
            LOG.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                            "sending it to elastic: Thread id {}",
                    messages.size(),
                    keys.toString(),
                    partitions.toString(),
                    offsets.toString(),
                    Thread.currentThread().getId());
            messages.forEach(System.out::println);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
        List<EventModel> eventModels = avroToShaftEventTransformer.getElasticModels(messages);
        eventModels.forEach(System.out::println);
        // #TODO Invoke NIFI for ingestion here
        /*
        List<EventModel> eventModels = avroToElasticModelTransformer.getElasticModels(messages);
        List<String> documentIds = elasticIndexClient.save(eventModels);
        LOG.info("Documents saved to elasticsearch with ids {}", documentIds.toArray());*/
    }
}
