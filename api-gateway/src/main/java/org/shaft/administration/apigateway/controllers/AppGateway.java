package org.shaft.administration.apigateway.controllers;

import org.shaft.administration.apigateway.common.ShaftResponseHandler;
import org.shaft.administration.apigateway.entity.AppMapping;
import org.shaft.administration.apigateway.dao.AppMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/shaft")
public class AppGateway {
    @Autowired
    private AppMappingService appMapping;

    @PostMapping("shop/v1")
    public ShaftResponseHandler handleShopRequest(
            @RequestHeader(value="operation-type") String operationType,
            @RequestHeader(value="user",required = false) String user,
            @RequestHeader(value="i",required = false) String i,
            @RequestHeader(value="account") String account,
            @RequestBody Map<String,Object> request) {

        ShaftResponseHandler response = new ShaftResponseHandler();

        // If service call then, JWT Token validation

        // Get Mappings from cache
        AppMapping mapping = appMapping.getMappings();
        Map<String,Object> routes = (Map<String, Object>) mapping.getRoutes().get(operationType);
        System.out.println(routes);
        response.setData(routes);

        // Invoke service according to mappings

        // Populate response in shaftResponseHandler

        return response;
    }

    @PostMapping("track/v1")
    public ShaftResponseHandler handletrackRequest(@Valid @RequestBody Map<String,Object> request) {

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
