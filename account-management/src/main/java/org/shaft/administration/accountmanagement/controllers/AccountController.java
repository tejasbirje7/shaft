package org.shaft.administration.accountmanagement.controllers;

import org.shaft.administration.accountmanagement.dao.AccountWorkerDao;
import org.shaft.administration.accountmanagement.dao.MetaDAO;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/account") // #TODO change this name to account and modify MetaController request mapping
public class AccountController {

  AccountWorkerDao accountWorkerDao;
  HttpHeaders headers;

  @Autowired
  public AccountController(AccountWorkerDao accountWorkerDao) {
    this.accountWorkerDao = accountWorkerDao;
    this.headers = new HttpHeaders();
  }

  @RequestMapping(value = "/bootstrap", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> bootstrapAccount(@RequestBody Map<String,Object> request) {
    return accountWorkerDao.bootstrapAccount(request).map(ShaftResponseHandler::generateResponse);
  }

}
