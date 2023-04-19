package org.shaft.administration.eventingestion.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.eventingestion.services.EventListenerService;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingest")
public class Ingest {
    EventListenerService eventListenerService;

    @Autowired
    public Ingest(EventListenerService eventListenerService) {
        this.eventListenerService = eventListenerService;
    }

    @RequestMapping(value = "/event", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> ingestEvent(@RequestHeader(value="account") int account,
                                              @RequestBody ObjectNode eventRequest) {
        return ShaftResponseHandler.generateResponse(eventListenerService.onStatus(account, eventRequest));
    }

}
