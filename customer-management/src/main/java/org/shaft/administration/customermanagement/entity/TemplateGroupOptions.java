package org.shaft.administration.customermanagement.entity;

import lombok.Data;

import java.util.List;

@Data
public class TemplateGroupOptions {
  String title;
  List<Object> content;
  Fields fields;

}
