package org.shaft.administration.eventingestion.listener;


import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.eventingestion.transformer.ShaftEventToAvroTransformer;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.shaft.administration.kafka.producer.config.service.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShaftKafkaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ShaftKafkaEventListener.class);
    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, EventAvroModel> kafkaProducer;

    private final ShaftEventToAvroTransformer shaftEventToAvroTransformer;

    public ShaftKafkaEventListener(KafkaConfigData configData,
                                   KafkaProducer<Long, EventAvroModel> producer,
                                   ShaftEventToAvroTransformer transformer) {
        this.kafkaConfigData = configData;
        this.kafkaProducer = producer;
        this.shaftEventToAvroTransformer = transformer;
    }

    public void onStatus(EventAction event) {
        LOG.info("Received status text {} sending to kafka topic {}", event.getText(), kafkaConfigData.getTopicName());
        EventAvroModel eventAvroModel = shaftEventToAvroTransformer.getEventAvroModel(event);
        kafkaProducer.send(kafkaConfigData.getTopicName(), eventAvroModel.getUserId(), eventAvroModel);
    }
}
