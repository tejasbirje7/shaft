package org.shaft.administration.reportingmanagement.controllers;

import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.reportingmanagement.dao.QueryDao;
import org.shaft.administration.reportingmanagement.dao.SegmentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/segment")
public class SegmentController {

    HttpHeaders headers;
    SegmentDAO segmentDAO;
    @Autowired
    public SegmentController(SegmentDAO segmentDAO) {
        this.headers = new HttpHeaders();
        this.segmentDAO = segmentDAO;
    }

    @RequestMapping(value="/save", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> saveSegment(@RequestHeader(value="account") int accountId,
                                                    @RequestBody Map<String,Object> rawQuery) {
        return segmentDAO.saveSegment(accountId,rawQuery).map(ShaftResponseHandler::generateResponse);
    }
}
