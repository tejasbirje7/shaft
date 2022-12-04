package org.shaft.administration.kafka.producer.config.service.impl;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.shaft.administration.kafka.producer.config.service.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;

@Service
public class ShaftKafkaProducer implements KafkaProducer<Long, EventAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ShaftKafkaProducer.class);

    private KafkaTemplate<Long, EventAvroModel> kafkaTemplate;

    public ShaftKafkaProducer(KafkaTemplate<Long, EventAvroModel> template) {
        this.kafkaTemplate = template;
    }

    @Override
    public void send(String topicName, Long key, EventAvroModel message) {
        LOG.info("Sending message='{}' to topic='{}'", message, topicName);
        LOG.info("Bootstrap servers information: {}",
                kafkaTemplate.getProducerFactory().getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        ListenableFuture<SendResult<Long, EventAvroModel>> kafkaResultFuture =
                kafkaTemplate.send(topicName, key, message);
        addCallback(topicName, message, kafkaResultFuture);
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            LOG.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }

    private void addCallback(String topicName, EventAvroModel message,
                             ListenableFuture<SendResult<Long, EventAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback() {
            @Override
            public void onSuccess(Object result) {
                LOG.info("TODO : log metada topics");
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            }

            /*
            @Override
            public void onSuccess(SendResult<Long, EventAvroModel> result) {
                    RecordMetadata metadata = result.getRecordMetadata();
                    LOG.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp(),
                            System.nanoTime());
            }*/
        });
    }
}
