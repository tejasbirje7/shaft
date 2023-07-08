package org.shaft.administration.eventprocessor.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventprocessor.entity.EventModel;
import org.shaft.administration.eventprocessor.entity.ObjectToIngest;
import org.shaft.administration.eventprocessor.transformer.AvroToShaftEventTransformer;
import org.shaft.administration.kafka.admin.client.KafkaAdminClient;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.time.Duration.ofSeconds;

@Slf4j
@EnableScheduling
@Service
public class ShaftKafkaPoller  {
  private final KafkaAdminClient kafkaAdminClient;
  private final KafkaConfigData kafkaConfigData;
  private final AvroToShaftEventTransformer avroToShaftEventTransformer;
  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  private final HttpHeaders httpHeaders;
  private final KafkaConsumer<Long, EventAvroModel> kafkaConsumer;

  public ShaftKafkaPoller(KafkaAdminClient adminClient,
                          KafkaConfigData configData,
                          AvroToShaftEventTransformer avroToShaftEventTransformer,
                          RestTemplate restTemplate,
                          Properties consumerConfig) {
    this.kafkaAdminClient = adminClient;
    this.kafkaConfigData = configData;
    this.avroToShaftEventTransformer = avroToShaftEventTransformer;
    this.restTemplate = restTemplate;
    this.mapper = new ObjectMapper();
    this.httpHeaders = new HttpHeaders();
    this.kafkaConsumer = new KafkaConsumer<>(consumerConfig);
  }

  @EventListener
  public void onAppStarted(ApplicationStartedEvent event) {
    kafkaAdminClient.checkTopicsCreated();
    log.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    this.kafkaConsumer.subscribe(Collections.singleton("track-event"));
  }

  @Bean
  public Runnable pollingBrokers() {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 10000)
      // #TODO Check if the above delay mess up with below poll method of kafka. Keep only one delay / interval from either of both
      public void run() {
        ConsumerRecords<Long, EventAvroModel> records = kafkaConsumer.poll(ofSeconds(20));
        List<String> createdTopics = kafkaConfigData.getTopicNamesToCreate();
        createdTopics.forEach(t -> {
          try {
            log.debug("No. of partitions {} - for topic {}",kafkaAdminClient.checkNumberOfPartitions(t),t);
            int noOfPartitionsForTopic = kafkaAdminClient.checkNumberOfPartitions(t);
            for (int i=0; i < noOfPartitionsForTopic; i++) {
              List<ConsumerRecord<Long, EventAvroModel>> eventsInTopic = records.records(new TopicPartition("track-event", i));
              if(eventsInTopic.size() > 0) {
                log.debug("{} messages found for topic {}, in partition {}",eventsInTopic.size(),t,i);
                eventsInTopic.forEach(eachRecord -> {
                  EventModel eventModel = avroToShaftEventTransformer.parseEventAvroModel(eachRecord.value());
                  ObjectToIngest eventData = avroToShaftEventTransformer.formatEventToIngest(eventModel.getI(),
                    avroToShaftEventTransformer.convertEventDataFromAvro(eventModel.getE()));
                  log.debug("Event Data {}",eventData);
                  Map<String,Object> request = mapper.convertValue(eventData, new TypeReference<Map<String, Object>>(){});
                  httpHeaders.set("account", String.valueOf(eachRecord.key()));
                  httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                  HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request, httpHeaders);
                  ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "http://localhost:8002/track",
                    HttpMethod.POST,entity,JsonNode.class);
                  log.info("Response {}",response.getStatusCode());
                  log.info("Data : {}", Objects.requireNonNull(response.getBody()).get("i").asText());
                  // Handle failure case
                });
              }
            }
          } catch (ExecutionException | InterruptedException e) {
            // Handle failure case since in case of exception for some reason message is not cleared from broker and then all 3 consumers polls 3 times4
            // Save message in log file somewhere
            // Avoid throwing exception here
            throw new RuntimeException(e);
          }
        });
      }
    };
  }
}
