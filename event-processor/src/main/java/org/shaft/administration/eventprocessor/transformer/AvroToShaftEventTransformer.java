package org.shaft.administration.eventprocessor.transformer;

import org.shaft.administration.eventprocessor.entity.EventDataModel;
import org.shaft.administration.eventprocessor.entity.EventModel;
import org.shaft.administration.eventprocessor.entity.ObjectToIngest;
import org.shaft.administration.kafka.avro.model.EventAvroModel;
import org.shaft.administration.kafka.avro.model.EventData;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToShaftEventTransformer {

  public List<EventModel> getElasticModels(List<EventAvroModel> avroModels) {
    return avroModels.stream()
      .map(avroModel -> EventModel
        .builder()
        .i(avroModel.getI())
        .e(avroModel.getE())
        .build()
      ).collect(Collectors.toList());
  }

  public EventModel parseEventAvroModel(EventAvroModel eventAvroModel) {
    return EventModel
      .builder()
      .i(eventAvroModel.getI())
      .e(eventAvroModel.getE())
      .build();
  }

  public EventDataModel convertEventDataFromAvro(EventData e) {
    return EventDataModel.builder()
      .eid(e.getEid())
      .quantity(e.getQuantity())
      .name(e.getName())
      .costPrice(e.getCostPrice())
      .onSale(e.getOnSale())
      .inStock(e.getInStock())
      .id(e.getId())
      .category(e.getCategory())
      .fp(e.getFp())
      .option(e.getOption())
      .ts(e.getTs())
      .build();
  }

  public ObjectToIngest formatEventToIngest(String i,EventDataModel e) {
    return ObjectToIngest.builder().i(i).e(e).build();
  }
}
