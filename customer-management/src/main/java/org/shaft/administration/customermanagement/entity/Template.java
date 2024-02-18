package org.shaft.administration.customermanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Template {
  @Id
  private String id;
  private String logo;
  private String cg;
  private List<String> preview;
  private String name;
  private int price;

}
