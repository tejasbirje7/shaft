package org.shaft.administration.reportingmanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.reportingmanagement.dao.QueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/reporting")
public class QueryControllers {
    HttpHeaders headers;
    QueryDao queryDao;

    @Autowired
    public QueryControllers(QueryDao queryDao) {
        this.headers = new HttpHeaders();;
        this.queryDao = queryDao;
    }

    @RequestMapping(value = "/query/results", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getQueryResults(@RequestHeader(value="account") int account,
                                                  @RequestBody Map<String,Object> rawQuery) {
        queryDao.getQueryResults(account, rawQuery);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",new ArrayList<>(),headers);
    }

    @RequestMapping(value="/query/save/filter", method = { RequestMethod.POST })
    public ResponseEntity<Object> saveFilters(@RequestHeader(value="account") int account,
                                              @RequestBody Map<String,Object> rawQuery) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",new ArrayList<>(),headers);
    }
}
