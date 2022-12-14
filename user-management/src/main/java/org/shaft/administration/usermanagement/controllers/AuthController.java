package org.shaft.administration.usermanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthDAO authDAO;
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    public AuthController(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> checkIdentity(@RequestHeader int account,
                                                @RequestBody Map<String,Object> details) {
        authDAO.authenticateUser(details);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }
}
