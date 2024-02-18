package org.shaft.administration.customermanagement.entity;

import lombok.Data;

import java.util.List;

@Data
public class TemplateOptions {
  String groupName;
  List<TemplateGroupOptions> children;

}
