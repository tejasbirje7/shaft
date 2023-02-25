package org.shaft.administration.marketingengine.entity.EventRequest;

import lombok.Data;

import java.util.List;

@Data
public class EventMetadata {
  private int eid;
  private int ts;
  private int oid;
  private int totalPrice;
  private String fp;
  List<Items> items;
}
