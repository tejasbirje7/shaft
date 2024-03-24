package org.shaft.administration.obligatory.transactions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

// #TODO Currently this is exposed directly. We should expose this via interface which will achieve polymorphism
@Data
public final class ShaftResponseHandler {

    private String message;
    private String code;
    private Object data;
    private static HttpHeaders httpHeaders;

    ShaftResponseHandler() {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public static ResponseEntity<Object> generateResponse(String message, String code, Object data, HttpHeaders headers) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("code", code);
        map.put("data", data);
        return new ResponseEntity<>(map, headers, HttpStatus.OK);
    }
    public static ResponseEntity<Object> generateResponse(String message, String code, Object data,HttpStatus status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("code", code);
        map.put("data", data);
        return new ResponseEntity<>(map, status);
    }

    public static ResponseEntity<Object> generateResponse(ObjectNode response) {
        return new ResponseEntity<>(response,httpHeaders, HttpStatus.OK);
    }

    public static ResponseEntity<Object> generateResponse(String s) {
        return new ResponseEntity<>(s,httpHeaders, HttpStatus.OK);
    }
}
