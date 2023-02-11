package org.shaft.administration.eventingestion.transformer;

import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.springframework.stereotype.Component;

@Component
public class ShaftEventToAvroTransformer {

    public EventAvroModel getEventAvroModel(EventAction status) {
        return EventAvroModel
          .newBuilder()
          .setI(status.getI())
          .setE(status.getE())
          .build();
    }
}
