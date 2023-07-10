package org.shaft.administration.kafka.producer.config.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
    String send(String topicName, K key, V message);
}
