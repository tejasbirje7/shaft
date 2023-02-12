package org.shaft.administration.eventingestion.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.eventingestion.listener.ShaftKafkaEventListener;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/ingest")
public class Ingest {

    HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper = new ObjectMapper();
    ShaftKafkaEventListener kafkaStatusListener;

    @Autowired
    public Ingest(ShaftKafkaEventListener kafkaStatusListener) {
        this.kafkaStatusListener = kafkaStatusListener;
    }

    @RequestMapping(value = "/event", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> ingestEvent(@RequestHeader(value="account") int account,
                                              @RequestBody ObjectNode eventRequest) {
        return ShaftResponseHandler.generateResponse(kafkaStatusListener.onStatus(account, eventRequest));
    }

}
