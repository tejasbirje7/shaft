package com.shaft.administration.accountmanagement.controllers;

import com.shaft.administration.accountmanagement.dao.DashboardDAO;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> pinQueryToDashboard(@RequestHeader int account,
                                                      @RequestBody Map<String,Object> query) {
        Boolean response = dashboardDAO.pinToDashboard(account, query);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",response, httpHeaders);
    }

}
