package org.shaft.administration.customermanagement.repositories.TemplateRepository;

import org.shaft.administration.customermanagement.entity.Template;
import reactor.core.publisher.Flux;

public interface CustomTemplateRepository {
  Flux<Template> getTemplatesCatalog();
}
