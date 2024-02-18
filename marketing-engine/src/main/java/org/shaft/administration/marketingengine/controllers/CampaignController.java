package org.shaft.administration.marketingengine.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.marketingengine.dao.CampaignDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/marketing")
public class CampaignController {

  private final CampaignDao campaignDao;
  HttpHeaders headers;

  @Autowired
  public CampaignController(CampaignDao campaignDao) {
    this.campaignDao = campaignDao;
    this.headers = new HttpHeaders();
  }

  @RequestMapping(value = "/campaign/qualification", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> getCampaignQualifications(@RequestHeader(value="account") int account,
                                                    @RequestBody() Map<String,Object> eventRequest) {
    return campaignDao.checkForCampaignQualification(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }

  @RequestMapping(value = "/get/campaigns", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> getCampaigns(@RequestHeader(value="account") int account,
                                                   @RequestBody() ObjectNode eventRequest) {
    return campaignDao.getCampaigns(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }

  @RequestMapping(value = "/save/campaign", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> saveCampaign(@RequestHeader(value="account") int account,
                                                   @RequestBody Mono<MultiValueMap<String, Part>> request) {
//    return campaignDao.saveCampaign(account,eventRequest).map(ShaftResponseHandler::generateResponse);
    return getResponseEntityMono(account, request);
  }

  private Mono<ResponseEntity<Object>> getResponseEntityMono(@RequestHeader("account") int account,
                                                             @RequestBody Mono<MultiValueMap<String, Part>> request) {
    return request.flatMap(parts -> {
      Map<String, Part> partMap = parts.toSingleValueMap();

      // Handle file
      FilePart image = (FilePart) partMap.get("files");

      // Handle item details
      FormFieldPart details = (FormFieldPart) partMap.get("campaignDetails");
      String value = details.value();

      // Parsing and saving item details
      try {
        ObjectNode campaignDetails = new ObjectMapper().readValue(value, ObjectNode.class);
        return campaignDao.saveCampaign(account,campaignDetails, image).map(ShaftResponseHandler::generateResponse);
      } catch (Exception ex) {
        // #TODO Throw invalid request [ MAJOR EXCEPTION ] & notify
        // #TODO Remove this different response handling for this call
        log.error("Exception while saving item {}",ex);
        return Mono.just(ShaftResponseHandler.generateResponse("Success","S12345",new ArrayList<>(),headers));
      }
    });
  }
}
