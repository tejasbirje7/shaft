package org.shaft.administration.customermanagement.repositories.TemplateConfigRepository;

import org.shaft.administration.customermanagement.entity.TemplateConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CustomTemplateConfigRepository {
  Flux<TemplateConfiguration> getTemplateConfiguration(int accountId);
  Mono<TemplateConfiguration> save(int accountId, TemplateConfiguration config);
  Mono<Long> update(int accountId, Map<String,Object> config);
}
