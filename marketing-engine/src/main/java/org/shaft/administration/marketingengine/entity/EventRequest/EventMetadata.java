package org.shaft.administration.marketingengine.entity.EventRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMetadata {
  private int eid;
  private int ts;
  private int oid;
  private int totalPrice;
  private String name;
  private boolean inStock;
  private String category;
  private long costPrice;
  private String fp;
  List<Items> items;
}
