package org.shaft.administration.eventprocessor.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class EventDataModel {

  private long eid;
  private long quantity;
  private long costPrice;
  private boolean onSale;
  private boolean inStock;
  private String id;
  private String category;
  private String fp;
  private String option;
  private String name;
  private long ts;
}
