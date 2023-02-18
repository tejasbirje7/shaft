package org.shaft.administration.eventprocessor.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.util.List;

public interface ShaftKafkaConsumer<K extends Serializable, V extends SpecificRecordBase> {
    void receive(List<V> messages, List<Long> keys, List<Integer> partitions, List<Long> offsets);
}
