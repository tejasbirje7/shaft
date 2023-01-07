package com.example.demo;

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
@RequestMapping("/user")
public class IdentityController {

    private final IdentityDAO identityDAO;
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    public IdentityController(IdentityDAO identityDAO) {
        this.identityDAO = identityDAO;
    }

    @RequestMapping(value = "/identity/check", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> checkIdentity(@RequestHeader int account,
                                                      @RequestBody Map<String,Object> details) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return identityDAO.checkIdentity(account,details)
                .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }

    @RequestMapping(value = "/user/details", method = {RequestMethod.POST})
    public Mono<ResponseEntity<Object>> getUserDetailsFromToken(@RequestBody Map<String,Object> token) {
        Map<String,Integer> response;
        if (token.containsKey("tk")) {
            response = identityDAO.getUserDetailsFromToken(String.valueOf(token.get("tk")));
        } else {
            response = new HashMap<>();
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return Mono.just(ShaftResponseHandler.generateResponse("Success","S12345",response,headers));
    }
}
