package org.shaft.administration.accountmanagement.entity;

import lombok.Data;

import java.util.List;

@Data
public class EventAndPropsMeta {
  List<EventsMeta> eventsMeta;
  List<PropsMeta> propsMeta;
}
