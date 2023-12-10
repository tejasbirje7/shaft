package org.shaft.administration.eventingestion.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventingestion.clients.CampaignRestClient;
import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.eventingestion.transformer.ShaftEventToAvroTransformer;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.kafka.producer.config.service.KafkaProducer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class EventListenerService {
  private final KafkaConfigData kafkaConfigData;
  private final ObjectMapper mapper = new ObjectMapper();
  private final CampaignRestClient campaignRestClient;
  private final KafkaProducer<Long, EventAvroModel> kafkaProducer;
  private final ShaftEventToAvroTransformer shaftEventToAvroTransformer;
  private final ObjectReader objectNodeParser;

  public EventListenerService(KafkaConfigData configData,
                              CampaignRestClient campaignRestClient, KafkaProducer<Long, EventAvroModel> producer,
                              ShaftEventToAvroTransformer transformer) {
    this.kafkaConfigData = configData;
    this.campaignRestClient = campaignRestClient;
    this.kafkaProducer = producer;
    this.objectNodeParser = new ObjectMapper().readerFor(ObjectNode.class);
    this.shaftEventToAvroTransformer = transformer;
  }

  public Mono<ObjectNode> onStatus(int account, ObjectNode request) {
    /*
    ObjectNode rts = mapper.createObjectNode();
    return Mono.just(ShaftResponseBuilder.buildResponse(publishEventRequestToBroker(request,account),rts));
    */
    return this.campaignRestClient.checkIfCampaignExists(account,request)
      .publishOn(Schedulers.boundedElastic())
      .mapNotNull(campaignResponse -> {
        ObjectNode responseToSend = mapper.createObjectNode();
        ObjectNode campaignToRender;
        try {
          campaignToRender = objectNodeParser.readValue(campaignResponse);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        if(campaignToRender.has("code") &&
          campaignToRender.get("code").asText().contains("S") &&
          campaignToRender.has("data") &&
          !campaignToRender.get("data").isEmpty()) {
          responseToSend.set("campaign",campaignToRender.get("data"));
        }
        if(checkIfChargedEvent(request)) {
          ArrayNode requestForEachItem = parseChargeRequest(request);
          requestForEachItem.forEach(eachItem -> publishEventRequestToBroker((ObjectNode) eachItem,account));
          return ShaftResponseBuilder.buildResponse(ShaftResponseCode.EVENT_PUBLISHED_TO_KAFKA,responseToSend);
        } else {
          return ShaftResponseBuilder.buildResponse(publishEventRequestToBroker(request,account),responseToSend);
        }
      });
  }

  private boolean checkIfChargedEvent(ObjectNode request) {
    // TODO Fetch charge event id by doing rest api call to accounts meta
    return request.has("e") && request.get("e").has("eid") && request.get("e").get("eid").asInt() == 3;
  }

  private ArrayNode parseChargeRequest(ObjectNode request) {
    ArrayNode items = (ArrayNode) request.get("e").get("items");
    items.forEach(item -> {
      ObjectNode i = mapper.convertValue(item, ObjectNode.class);
      i.put("ts",request.get("e").get("ts").asInt());
      i.put("eid",request.get("e").get("eid").asInt());
    });
    return items;
  }

  private String publishEventRequestToBroker(ObjectNode request, int account) {
    final EventAvroModel eventAvroModel;
    try {
      final EventAction event = mapper.convertValue(request, EventAction.class);
      log.debug("Received status text {} sending to kafka topic {}", event.getI(), kafkaConfigData.getTopicName());
      eventAvroModel = shaftEventToAvroTransformer.getEventAvroModel(event);
    } catch (Exception ex) {
      log.error("Error parsing track event request {}",ex.getMessage(),ex);
      return ShaftResponseCode.ERROR_PARSING_TRACK_EVENT_REQUEST;
    }
    // Account ID is used as a key, which acts as a header from producer to consumer.
    return kafkaProducer.send(kafkaConfigData.getTopicName(), (long) account, eventAvroModel);
  }
}
