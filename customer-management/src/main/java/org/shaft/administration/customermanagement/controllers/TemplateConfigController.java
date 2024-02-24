package org.shaft.administration.customermanagement.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.customermanagement.dao.TemplateConfigurationDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/customer")
public class TemplateConfigController {
  private final TemplateConfigurationDao templateConfigurationDao;
  HttpHeaders headers;

  @Autowired
  public TemplateConfigController(TemplateConfigurationDao templateConfigurationDao) {
    this.templateConfigurationDao = templateConfigurationDao;
    this.headers = new HttpHeaders();
  }

  // #TODO Replace get() call with only header as accountID and don't accept any request body in all controllers
  @RequestMapping(value = "/template/get/config", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> getTemplateConfiguration(@RequestHeader(value="account") int account,
                                                               @RequestBody() ObjectNode eventRequest) {
    return templateConfigurationDao.getTemplateConfiguration(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }

  @RequestMapping(value = "/template/update/config", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> updateTemplateConfig(@RequestHeader(value="account") int account,
                                                           @RequestBody() Mono<MultiValueMap<String, Part>> request) {
    return templateConfigurationDao.updateTemplateConfiguration(account,request).map(ShaftResponseHandler::generateResponse);
  }

  @RequestMapping(value = "/template/save/config", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> saveTemplateConfig(@RequestBody() ObjectNode eventRequest) {
    return templateConfigurationDao.saveTemplateConfig(eventRequest).map(ShaftResponseHandler::generateResponse);
  }

}
