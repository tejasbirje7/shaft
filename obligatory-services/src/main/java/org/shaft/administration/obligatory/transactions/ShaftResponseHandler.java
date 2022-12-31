package org.shaft.administration.obligatory.transactions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

// #TODO Currently this is exposed directly. We should expose this via interface which will achieve polymorphism
public final class ShaftResponseHandler {

    private String message;
    private String code;
    private Object data;
    private HttpHeaders httpHeaders;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
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
}
