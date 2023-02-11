package org.shaft.administration.eventprocessor.consumer.impl;

import lombok.Builder;
import lombok.Data;
import org.shaft.administration.eventprocessor.consumer.IndexModel;
import org.shaft.administration.kafka.avro.model.EventData;

import java.util.List;

@Data
@Builder
public class EventModel implements IndexModel {
    private String i;
    private List<EventData> e;

}
