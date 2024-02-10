package org.shaft.administration.reportingmanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.reportingmanagement.dao.QueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/reporting")
public class QueryController {
    HttpHeaders headers;
    QueryDao queryDao;

    @Autowired
    public QueryController(QueryDao queryDao) {
        this.headers = new HttpHeaders();;
        this.queryDao = queryDao;
    }

    @RequestMapping(value = "/query/results", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getQueryResults(@RequestHeader(value="account") int account,
                                                        @RequestBody Map<String,Object> rawQuery) {
        return queryDao.getQueryResults(account,rawQuery).map(ShaftResponseHandler::generateResponse);
    }

    /**
     *  This function is to evaluate bulk queries which is used in saved custom boards
     * @param account
     * @param rawQuery
     * @return
     */
    @RequestMapping(value = "/query/eval", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> evaluateEncodedQueries(@RequestHeader(value="account") int account,
                                                         @RequestBody Map<String,Object> rawQuery) {
        return queryDao.evaluateEncodedQueries(account,rawQuery).map(ShaftResponseHandler::generateResponse);
    }

}
