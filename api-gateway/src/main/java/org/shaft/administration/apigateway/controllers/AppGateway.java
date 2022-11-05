package org.shaft.administration.apigateway.controllers;

import org.shaft.administration.apigateway.common.ShaftResponseHandler;
import org.shaft.administration.apigateway.entity.AppMapping;
import org.shaft.administration.apigateway.dao.AppMappingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/shaft")
public class AppGateway {
    private AppMappingDAO appMapping;
    private RestTemplate httpFactory;
    HttpHeaders headers;

    @Autowired
    public AppGateway(AppMappingDAO appMapping, RestTemplate httpFactory) {
        this.appMapping = appMapping;
        this.httpFactory = httpFactory;
    }

    @PostMapping("shop/v1")
    public ResponseEntity<String> handleShopRequest(
            @RequestHeader(value="operation-type") String operationType,
            @RequestHeader(value="user",required = false) String user,
            @RequestHeader(value="i",required = false) String i,
            @RequestHeader(value="account") String account,
            @RequestBody Map<String,Object> request) {

        // Get Mappings from cache
        AppMapping mapping = appMapping.getMappings();
        Map<String,Object> routes = (Map<String, Object>) mapping.getRoutes().get(operationType);

        // Configure HTTP Request
        headers = new HttpHeaders();
        headers.set("account",account);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request,headers);

        // Invoke service according to mappings
        ResponseEntity<String> resp = httpFactory.exchange("http://localhost:8081/catalog/category", HttpMethod.POST,entity,String.class);

        // Return response from microservice
        return resp;
    }

    @PostMapping("track/v1")
    public ShaftResponseHandler handleTrackRequest(@Valid @RequestBody Map<String,Object> request) {

        ShaftResponseHandler response = new ShaftResponseHandler();

        return response;
    }


//
//    public static void main(String[] args) {
//        String route = "getItems";
//        Map<String,Object> routes = new HashMap<>();
//        routes.put("port",9200);
//        Map<String,Object> o = new HashMap<>();
//        o.put(route,routes);
//
//        System.out.println(o.get(route));
//    }
}
