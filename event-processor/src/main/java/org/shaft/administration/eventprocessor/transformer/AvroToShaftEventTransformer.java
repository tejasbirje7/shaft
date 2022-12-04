package org.shaft.administration.eventprocessor.transformer;

import org.shaft.administration.eventprocessor.consumer.impl.EventModel;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToShaftEventTransformer {


    public List<EventModel> getElasticModels(List<EventAvroModel> avroModels) {
        return avroModels.stream()
                .map(avroModel -> EventModel
                        .builder()
                        .userId(avroModel.getUserId())
                        .id(String.valueOf(avroModel.getId()))
                        .text(avroModel.getText())
                        .createdAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(avroModel.getCreatedAt()),
                                ZoneId.systemDefault()))
                        .build()
                ).collect(Collectors.toList());
    }
}
