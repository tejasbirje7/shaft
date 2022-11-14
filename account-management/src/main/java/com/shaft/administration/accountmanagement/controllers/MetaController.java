package com.shaft.administration.accountmanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class MetaController {

    HttpHeaders headers = new HttpHeaders();

    @RequestMapping(value = "/meta/fields", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getMetaFields(@RequestHeader int account,
                                                @RequestBody Map<String,Object> request) {
        Map<String,Object> fields = new HashMap<>();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",fields, headers);
    }
}
