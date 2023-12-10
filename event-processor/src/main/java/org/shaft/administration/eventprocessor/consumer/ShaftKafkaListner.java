package org.shaft.administration.eventprocessor.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventprocessor.entity.EventModel;
import org.shaft.administration.eventprocessor.entity.ObjectToIngest;
import org.shaft.administration.eventprocessor.transformer.AvroToShaftEventTransformer;
import org.shaft.administration.kafka.admin.client.KafkaAdminClient;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * This class is just for future reference
 * since it has implementation of kafka services
 * which can be used for streaming messages
 * as soon as producer publishes in brokers
 */
@Slf4j
@Service
public class ShaftKafkaListner implements ShaftKafkaConsumer<Long, EventAvroModel> {

  private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
  private final KafkaAdminClient kafkaAdminClient;
  private final KafkaConfigData kafkaConfigData;
  private final AvroToShaftEventTransformer avroToShaftEventTransformer;
  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  private final HttpHeaders httpHeaders;
  private final KafkaConsumer<Long,EventAvroModel> kafkaConsumer;

  public ShaftKafkaListner(KafkaAdminClient adminClient,
                           KafkaConfigData configData,
                           AvroToShaftEventTransformer avroToShaftEventTransformer,
                           RestTemplate restTemplate,
                           Properties consumerConfig,
                           KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
    this.kafkaAdminClient = adminClient;
    this.kafkaConfigData = configData;
    this.avroToShaftEventTransformer = avroToShaftEventTransformer;
    this.restTemplate = restTemplate;
    this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    this.mapper = new ObjectMapper();
    this.httpHeaders = new HttpHeaders();
//    log.info("Property 1 " + consumerConfig.getProperty("bootstrap.servers"));
//    log.info("Property 2 " + consumerConfig.getProperty("key.deserializer"));
    this.kafkaConsumer = new KafkaConsumer<>(consumerConfig);
  }

  @EventListener
  public void onAppStarted(ApplicationStartedEvent event) {
    kafkaAdminClient.checkTopicsCreated();
    log.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    this.kafkaConsumer.subscribe(Collections.singleton("track-event"));
    //kafkaListenerEndpointRegistry.getListenerContainer("trackEventListener").start();
  }
  @Override
  //@KafkaListener(id = "trackEventListener", topics = "${kafka-config.topic-name}")
  public void receive(@Payload List<EventAvroModel> messages,
                      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Long> keys,
                      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    try {
      log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
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

    // #TODO Find a better way to construct this ingestion request. If possible eliminate entirely parsing this request
    List<EventModel> eventModels = avroToShaftEventTransformer.getElasticModels(messages);

    log.info("Event Models Size : {}",eventModels.size());

    EventModel evm = eventModels.get(0);

    ObjectToIngest eventData = avroToShaftEventTransformer.formatEventToIngest(evm.getI(),avroToShaftEventTransformer.convertEventDataFromAvro(evm.getE()));
  }
}

