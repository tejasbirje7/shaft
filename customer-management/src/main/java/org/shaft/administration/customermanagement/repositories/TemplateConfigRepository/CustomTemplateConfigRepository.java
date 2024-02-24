package org.shaft.administration.customermanagement.repositories.TemplateConfigRepository;

import org.shaft.administration.customermanagement.entity.TemplateConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CustomTemplateConfigRepository {
  Flux<TemplateConfiguration> getTemplateConfigurationByAccount(int accountId);
  Mono<TemplateConfiguration> getTemplateConfigurationById(String templateId);
  Mono<TemplateConfiguration> saveTemplateConfiguration(int accountId, TemplateConfiguration config);
  Mono<Long> update(int accountId, Map<String,Object> config);
}
