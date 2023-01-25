package org.shaft.administration.usermanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class AuthController {
    private final AuthDAO authDAO;
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    public AuthController(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> checkIdentity(@RequestHeader int account,
                                                      @RequestBody Map<String,Object> details) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return authDAO.authenticateUser(details)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }

    @RequestMapping(value = "/register", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> addIdentity(@RequestHeader int account,
                                                    @RequestBody Map<String,Object> details) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return authDAO.registerUser(account,details)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }
}
