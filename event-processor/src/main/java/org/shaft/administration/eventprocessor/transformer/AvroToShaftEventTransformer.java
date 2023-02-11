package org.shaft.administration.eventprocessor.transformer;

import org.shaft.administration.eventprocessor.consumer.impl.EventModel;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToShaftEventTransformer {


  public List<EventModel> getElasticModels(List<EventAvroModel> avroModels) {
    return avroModels.stream()
      .map(avroModel -> EventModel
        .builder()
        .e(avroModel.getE())
        .i(avroModel.getI())
        .build()
      ).collect(Collectors.toList());
  }
}
