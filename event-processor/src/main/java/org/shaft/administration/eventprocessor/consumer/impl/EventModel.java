package org.shaft.administration.eventprocessor.consumer.impl;

import lombok.Builder;
import lombok.Data;
import org.shaft.administration.kafka.avro.model.EventData;

@Data
@Builder
public class EventModel {
    private EventData e;
    private String i;

}
