package org.shaft.administration.eventingestion.transformer;

import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;

@Component
public class TwitterStatusToAvroTransformer {

    public TwitterAvroModel getTwitterAvroModelFromStatus(EventAction status) {
        return TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUserId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt())
                .build();
    }
}
