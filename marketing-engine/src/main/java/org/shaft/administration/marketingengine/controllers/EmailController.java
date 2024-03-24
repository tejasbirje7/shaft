package org.shaft.administration.marketingengine.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.marketingengine.dao.EmailDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/marketing")
public class EmailController {
  private final EmailDao emailDao;

  public EmailController(EmailDao emailDao) {
    this.emailDao = emailDao;
  }

}
