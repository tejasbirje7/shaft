package org.shaft.administration.accountmanagement.controllers;

import org.shaft.administration.accountmanagement.dao.DashboardDAO;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    DashboardDAO dashboardDAO;
    HttpHeaders httpHeaders;
    public DashboardController(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
        this.httpHeaders = new HttpHeaders();
    }

    @RequestMapping(value = "/pin/query", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> pinQueryToDashboard(@RequestHeader int account,
                                                            @RequestBody Map<String,Object> query) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return dashboardDAO.pinToDashboard(account,query)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,httpHeaders));
    }

}
