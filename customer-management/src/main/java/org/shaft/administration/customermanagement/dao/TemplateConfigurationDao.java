package org.shaft.administration.customermanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

public interface TemplateConfigurationDao {
  Mono<ObjectNode> getTemplateConfiguration(int accountId, ObjectNode requestObject);
  Mono<ObjectNode> updateTemplateConfiguration(int accountId, Mono<MultiValueMap<String, Part>>  requestObject);
}
