package org.shaft.administration.customermanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class TemplateConfiguration {
  @Id
  String templateId;
  String templateName;
  List<TemplateOptions> templateOptions;
}
