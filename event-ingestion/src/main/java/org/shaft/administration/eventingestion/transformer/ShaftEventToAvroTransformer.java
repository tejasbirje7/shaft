package org.shaft.administration.eventingestion.transformer;

import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.springframework.stereotype.Component;

@Component
public class ShaftEventToAvroTransformer {

    public EventAvroModel getEventAvroModel(EventAction status) {
        return EventAvroModel
          .newBuilder()
          .setId(status.getId())
          .setUserId(status.getUserId())
          .setText(status.getText())
          .setCreatedAt(status.getCreatedAt())
          .build();
    }
}
