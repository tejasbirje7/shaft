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
    TwitterKafkaStatusListener kafkaStatusListener;

    @Autowired
    public Ingest(TwitterKafkaStatusListener kafkaStatusListener) {
        this.kafkaStatusListener = kafkaStatusListener;
    }

    @RequestMapping(value = "/event", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> transactCartProducts(@RequestBody Map<String,Object> productsToUpdate) {

        // #TODO Handle extra keys error present in schema - com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field
        // #TODO Handle wrong datatype / format in schema - com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize
        // #TODO create generic object mapper and expose it via obligatory services
        final EventAction pojo = objectMapper.convertValue(productsToUpdate, EventAction.class);
        kafkaStatusListener.onStatus(pojo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

}
