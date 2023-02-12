package org.shaft.administration.eventingestion.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
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

@Component
@Slf4j
public class ShaftKafkaEventListener {
    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, EventAvroModel> kafkaProducer;
    private final ShaftEventToAvroTransformer shaftEventToAvroTransformer;
    private final ObjectMapper mapper = new ObjectMapper();

    public ShaftKafkaEventListener(KafkaConfigData configData,
                                   KafkaProducer<Long, EventAvroModel> producer,
                                   ShaftEventToAvroTransformer transformer) {
        this.kafkaConfigData = configData;
        this.kafkaProducer = producer;
        this.shaftEventToAvroTransformer = transformer;
    }

    public ObjectNode onStatus(int account, ObjectNode request) {
        final EventAvroModel eventAvroModel;
        try {
            // #TODO create generic object mapper and expose it via obligatory services
            final EventAction event = mapper.convertValue(request, EventAction.class);
            log.debug(EventIngestionLogs.SENDING_PAYLOAD_TO_KAFKA_TOPIC, event.getI(), kafkaConfigData.getTopicName());
            eventAvroModel = shaftEventToAvroTransformer.getEventAvroModel(event);
        } catch (Exception ex) {
            log.error(EventIngestionLogs.ERROR_PARSING_TRACK_EVENT_REQUEST,ex.getMessage(),ex);
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_PARSING_TRACK_EVENT_REQUEST);
        }
        return kafkaProducer.send(kafkaConfigData.getTopicName(), (long) account, eventAvroModel);
    }
}
