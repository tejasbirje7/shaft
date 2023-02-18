package org.shaft.administration.eventprocessor.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ObjectToIngest {
  String i;
  EventDataModel e;
}
