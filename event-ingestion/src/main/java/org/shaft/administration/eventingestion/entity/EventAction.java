package org.shaft.administration.eventingestion.entity;

import lombok.Data;
import org.shaft.administration.kafka.avro.model.EventData;

import java.util.List;

@Data
public class EventAction {
    private String i;
    private List<EventData> e;
}
