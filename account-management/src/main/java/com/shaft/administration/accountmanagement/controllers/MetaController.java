package com.shaft.administration.accountmanagement.controllers;

import com.shaft.administration.accountmanagement.dao.MetaDAO;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class MetaController {
    MetaDAO metaDao;
    HttpHeaders headers;

    @Autowired
    public MetaController(MetaDAO metaDao) {
        this.metaDao = metaDao;
        this.headers = new HttpHeaders();
    }

    @RequestMapping(value = "/meta/fields", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getMetaFields(@RequestHeader int account,
                                                      @RequestBody Map<String,Object> request) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return metaDao.getMetaFields(account,request)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }
}
