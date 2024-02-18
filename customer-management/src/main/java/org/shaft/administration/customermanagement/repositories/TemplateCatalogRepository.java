package org.shaft.administration.customermanagement.repositories;

import org.shaft.administration.customermanagement.entity.Template;
import org.shaft.administration.customermanagement.repositories.TemplateRepository.CustomTemplateRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TemplateCatalogRepository extends ReactiveCrudRepository<Template,String>, CustomTemplateRepository {
}
