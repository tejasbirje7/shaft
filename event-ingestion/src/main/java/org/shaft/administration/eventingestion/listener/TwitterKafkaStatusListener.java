package org.shaft.administration.eventingestion.listener;


import org.shaft.administration.appconfigdata.KafkaConfigData;
import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.eventingestion.transformer.TwitterStatusToAvroTransformer;
import org.shaft.administration.kafka.avro.model.TwitterAvroModel;
import org.shaft.administration.kafka.producer.config.service.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TwitterKafkaStatusListener {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);
    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

    public TwitterKafkaStatusListener(KafkaConfigData configData,
                                      KafkaProducer<Long, TwitterAvroModel> producer,
                                      TwitterStatusToAvroTransformer transformer) {
        this.kafkaConfigData = configData;
        this.kafkaProducer = producer;
        this.twitterStatusToAvroTransformer = transformer;
    }

    public void onStatus(EventAction event) {
        LOG.info("Received status text {} sending to kafka topic {}", event.getText(), kafkaConfigData.getTopicName());
        TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(event);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
