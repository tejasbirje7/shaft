package org.shaft.administration.customermanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.customermanagement.dao.TemplateCatalogDao;
import org.shaft.administration.customermanagement.repositories.TemplateCatalogRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.usermanagement.clients.AccountRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TemplateCatalogService implements TemplateCatalogDao {
  private final TemplateCatalogRepository templateCatalogRepository;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public ObjectMapper mapper;

  @Autowired
  public TemplateCatalogService(TemplateCatalogRepository templateCatalogRepository) {
    this.templateCatalogRepository = templateCatalogRepository;
    this.mapper = new ObjectMapper();
  }

  @Override
  public Mono<ObjectNode> getTemplates(int accountId, ObjectNode requestObject) {
    return templateCatalogRepository.getTemplatesCatalog()
      .collectList()
      .map(templates -> {
        log.info("Templates : {}",templates);
        return ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.TEMPLATES_FETCHED,mapper.valueToTree(templates));
      })
      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(
        ShaftResponseCode.FAILED_TO_FETCH_TEMPLATES)));
  }
}
