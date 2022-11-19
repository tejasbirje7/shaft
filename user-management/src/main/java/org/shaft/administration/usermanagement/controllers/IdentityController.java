package org.shaft.administration.usermanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.usermanagement.dao.IdentityDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> checkIdentity(@RequestHeader int account,
                                                @RequestBody Map<String,Object> details) {
        Map<String, Integer> response = identityDAO.checkIdentity(account,details);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",response, headers);
    }

    @RequestMapping(value = "/user/details", method = {RequestMethod.POST})
    public ResponseEntity<Object> getUserDetailsFromToken(@RequestBody Map<String,Object> token) {
        Map<String,Integer> response;
        if (token.containsKey("tk")) {
            response = identityDAO.getUserDetailsFromToken(String.valueOf(token.get("tk")));
        } else {
            response = new HashMap<>();
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",response, headers);
    }
}
