package org.shaft.administration.apigateway.controllers;

import org.shaft.administration.apigateway.dao.FingerPrintingDAO;
import org.shaft.administration.apigateway.entity.AppMapping;
import org.shaft.administration.apigateway.dao.AppMappingDAO;
import org.shaft.administration.apigateway.entity.Routes;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
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
    private final AppMappingDAO appMapping;
    private final FingerPrintingDAO fingerprintingDAO;
    private final RestTemplate httpFactory;
    HttpHeaders headers;

    @Autowired
    public AppGateway(AppMappingDAO appMapping, FingerPrintingDAO fingerprintingDAO, RestTemplate httpFactory) {
        this.appMapping = appMapping;
        this.fingerprintingDAO = fingerprintingDAO;
        this.httpFactory = httpFactory;
    }

    // #TODO Add try..catch for this method with appropriate responses
    @RequestMapping(value = "/shop/v1", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<ShaftResponseHandler> handleShopRequest(
            @RequestHeader(value="operation_type") String operationType,
            @RequestHeader(value="account") String account,
            @RequestHeader(value="user",required = false) String user,
            @RequestHeader(value="i",required = false) String i,
            @RequestBody(required = false) Map<String,Object> request) {

        // Do fingerprinting tracking
        //fingerprintingDAO.checkIdentity(request);

        // Get Mappings from cache
        AppMapping mapping = appMapping.getMappings();
        Routes routes = mapping.getRoutes().get(operationType);


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
            case "cart":
                host = mapping.getCart();
        }

        // Configure HTTP Request
        headers = new HttpHeaders();
        headers.set("account",account);
        headers.set("i",i);

        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request,headers);

        // Invoke service according to mappings
        ResponseEntity<ShaftResponseHandler> resp = httpFactory.exchange(host + routes.getPath(), HttpMethod.POST,entity,ShaftResponseHandler.class);

        // Return response from microservice
        return resp;
    }

    @PostMapping("track/v1")
    public ShaftResponseHandler handleTrackRequest(@Valid @RequestBody Map<String,Object> request) {

        ShaftResponseHandler response = new ShaftResponseHandler();

        return response;
    }
}
