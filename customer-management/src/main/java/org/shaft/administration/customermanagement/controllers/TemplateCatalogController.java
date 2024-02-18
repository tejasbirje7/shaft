package org.shaft.administration.customermanagement.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.customermanagement.dao.TemplateCatalogDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/customer")
public class TemplateCatalogController {

  private final TemplateCatalogDao templateCatalogDao;
  HttpHeaders headers;

  @Autowired
  public TemplateCatalogController(TemplateCatalogDao templateCatalogDao) {
    this.templateCatalogDao = templateCatalogDao;
    this.headers = new HttpHeaders();
  }

  @RequestMapping(value = "/templates/get/catalog", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> getTemplates(@RequestHeader(value="account") int account,
                                                   @RequestBody() ObjectNode eventRequest) {
    return templateCatalogDao.getTemplates(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }

}
