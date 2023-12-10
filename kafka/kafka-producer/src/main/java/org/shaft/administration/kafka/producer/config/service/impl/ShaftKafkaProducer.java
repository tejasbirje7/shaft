package org.shaft.administration.kafka.producer.config.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.shaft.administration.kafka.producer.config.constants.KafkaProducerLogs;
import org.shaft.administration.kafka.producer.config.service.KafkaProducer;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PreDestroy;

@Service
@Slf4j
public class ShaftKafkaProducer implements KafkaProducer<Long, EventAvroModel> {

    private final KafkaTemplate<Long, EventAvroModel> kafkaTemplate;

    public ShaftKafkaProducer(KafkaTemplate<Long, EventAvroModel> template) {
        this.kafkaTemplate = template;
    }

    @Override
    public String send(String topicName, Long key, EventAvroModel message) {
        log.info(KafkaProducerLogs.SENDING_MESSAGE_TO_KAFKA_TOPIC, message, topicName);
        log.debug(KafkaProducerLogs.BOOTSTRAP_SERVERS_INFO,
          kafkaTemplate.getProducerFactory().getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        try {
            ListenableFuture<SendResult<Long, EventAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
            return ShaftResponseCode.EVENT_PUBLISHED_TO_KAFKA;
            //return addCallback(topicName, message, kafkaResultFuture);
        } catch (Exception ex) {
            log.error(KafkaProducerLogs.ERROR_PUBLISHING_EVENTS_KAFKA,ex);
            return ShaftResponseCode.ERROR_PUBLISHING_EVENTS_TO_KAFKA;
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            // #TODO Check if there are any incoming events here before shutting down. Shutdown should be done gracefully
            log.info(KafkaProducerLogs.CLOSING_KAFKA_PRODUCER);
            kafkaTemplate.destroy();
        }
    }

    /*
    private void addCallback(String topicName, EventAvroModel message,
                             ListenableFuture<SendResult<Long, EventAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<SendResult<Long, EventAvroModel>>() {

            @Override
            public void onFailure(Throwable throwable) {
                log.error(KafkaProducerLogs.UNABLE_TO_SEND_MESSAGE_TO_TOPIC, message.toString(), topicName, throwable);
            }

            @Override
            public void onSuccess(SendResult<Long, EventAvroModel> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info(KafkaProducerLogs.RECORD_METADATA,
                  metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp(), System.nanoTime());
            }
        });
    }*/
}
