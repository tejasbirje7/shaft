package org.shaft.administration.customermanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

public interface TemplateCatalogDao {
  Mono<ObjectNode> getTemplates(int accountId, ObjectNode requestObject);
}
