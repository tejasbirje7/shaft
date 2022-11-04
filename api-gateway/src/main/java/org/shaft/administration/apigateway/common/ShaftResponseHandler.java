package org.shaft.administration.apigateway.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class ShaftResponseHandler {

    public static ResponseEntity<Object> generateResponse(String message, String code, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("code", code);
        map.put("data", responseObj);
        return new ResponseEntity<Object>(map,HttpStatus.OK);
    }
    public static ResponseEntity<Object> generateResponse(String message, String code, Object responseObj,HttpStatus status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("code", code);
        map.put("data", responseObj);
        return new ResponseEntity<Object>(map,status);
    }
}
