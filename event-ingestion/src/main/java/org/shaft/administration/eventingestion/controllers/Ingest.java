package org.shaft.administration.eventingestion.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shaft.administration.eventingestion.entity.EventAction;
import org.shaft.administration.eventingestion.listener.TwitterKafkaStatusListener;
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
    @Autowired
    TwitterKafkaStatusListener kafkaStatusListener;

    @RequestMapping(value = "/event", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> transactCartProducts(@RequestBody Map<String,Object> productsToUpdate) {

        final EventAction pojo = objectMapper.convertValue(productsToUpdate, EventAction.class);
        kafkaStatusListener.onStatus(pojo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

}
