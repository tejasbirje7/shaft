package org.shaft.administration.marketingengine.entity.EventRequest;

import lombok.Data;

@Data
public class Items {
  private int id;
  private String name;
  private String category;
  private boolean onSale;
  private String costPrice;
  private String salePrice;
  private int quantity;
  private String option;
}
