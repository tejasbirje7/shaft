package org.shaft.administration.eventingestion.entity;

import lombok.Data;
import org.shaft.administration.kafka.avro.model.EventData;

@Data
public class EventAction {
    private EventData e;
    private String i;
}
