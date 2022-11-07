package org.shaft.administration.apigateway.controllers;

import org.shaft.administration.apigateway.common.ShaftResponseHandler;
import org.shaft.administration.apigateway.entity.AppMapping;
import org.shaft.administration.apigateway.dao.AppMappingDAO;
import org.shaft.administration.apigateway.entity.Routes;
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
    public ResponseEntity<ShaftResponseHandler> handleShopRequest(
            @RequestHeader(value="operation-type") String operationType,
            @RequestHeader(value="user",required = false) String user,
            @RequestHeader(value="i",required = false) String i,
            @RequestHeader(value="account") String account,
            @RequestBody Map<String,Object> request) {

        // Get Mappings from cache
        AppMapping mapping = appMapping.getMappings();
        Routes routes = mapping.getRoutes().get(operationType);

        // Configure HTTP Request
        headers = new HttpHeaders();
        headers.set("account",account);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request,headers);

        // Identify service to invoke
        String path = routes.getPath().split("/")[1];
        String host = "";
        switch (path) {
            case "catalog":
                host = mapping.getCatalog();
                break;
            case "inventory":
                host = mapping.getInventory();
                break;
        }
        HttpMethod httpMethod = routes.getMethod().equals("GET") ? HttpMethod.GET : HttpMethod.POST;

        // Respond with failure if host or method is empty
        if(host.isEmpty() || httpMethod == null) {
           // #TODO Response with failure if host is empty
        }

        // Invoke service according to mappings
        ResponseEntity<ShaftResponseHandler> resp = httpFactory.exchange(host + routes.getPath(), httpMethod,entity,ShaftResponseHandler.class);

        // Return response from microservice
        return resp;
    }

    @PostMapping("track/v1")
    public ShaftResponseHandler handleTrackRequest(@Valid @RequestBody Map<String,Object> request) {

        ShaftResponseHandler response = new ShaftResponseHandler();

        return response;
    }
}
