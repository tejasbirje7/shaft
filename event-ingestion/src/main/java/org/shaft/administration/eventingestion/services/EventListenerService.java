package org.shaft.administration.eventingestion.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventingestion.constants.EventIngestionLogs;
import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.eventingestion.transformer.ShaftEventToAvroTransformer;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.shaft.administration.kafka.producer.config.service.KafkaProducer;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventListenerService {
  private final KafkaConfigData kafkaConfigData;
  private final KafkaProducer<Long, EventAvroModel> kafkaProducer;
  private final ShaftEventToAvroTransformer shaftEventToAvroTransformer;
  private final ObjectMapper mapper = new ObjectMapper();

  public EventListenerService(KafkaConfigData configData,
                              KafkaProducer<Long, EventAvroModel> producer,
                              ShaftEventToAvroTransformer transformer) {
    this.kafkaConfigData = configData;
    this.kafkaProducer = producer;
    this.shaftEventToAvroTransformer = transformer;
  }

  public ObjectNode onStatus(int account, ObjectNode request) {
    if(checkIfChargedEvent(request)) {
      ArrayNode requestForEachItem = parseChargeRequest(request);
      requestForEachItem.forEach(eachItem -> publishEventRequestToBroker((ObjectNode) eachItem,account));
      return ShaftResponseBuilder.buildResponse(ShaftResponseCode.EVENT_PUBLISHED_TO_KAFKA);
    } else {
      return publishEventRequestToBroker(request,account);
    }
  }

  private boolean checkIfChargedEvent(ObjectNode request) {
    // TODO Check if charge event by doing rest api call to accounts meta
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

  private ObjectNode publishEventRequestToBroker(ObjectNode request, int account) {
    final EventAvroModel eventAvroModel;
    try {
      final EventAction event = mapper.convertValue(request, EventAction.class);
      log.debug(EventIngestionLogs.SENDING_PAYLOAD_TO_KAFKA_TOPIC, event.getI(), kafkaConfigData.getTopicName());
      eventAvroModel = shaftEventToAvroTransformer.getEventAvroModel(event);
    } catch (Exception ex) {
      log.error(EventIngestionLogs.ERROR_PARSING_TRACK_EVENT_REQUEST,ex.getMessage(),ex);
      return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_PARSING_TRACK_EVENT_REQUEST);
    }
    // Account ID is used as a key, which acts as a header from producer to consumer.
    return kafkaProducer.send(kafkaConfigData.getTopicName(), (long) account, eventAvroModel);
  }
}
