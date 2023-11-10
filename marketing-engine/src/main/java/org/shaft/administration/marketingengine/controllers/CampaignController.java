package org.shaft.administration.marketingengine.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.marketingengine.dao.CampaignDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
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
  public Mono<ResponseEntity<Object>> getCategories(@RequestHeader(value="account") int account,
                                                    @RequestBody() Map<String,Object> eventRequest) {
    return campaignDao.checkForCampaignQualification(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }

  @RequestMapping(value = "/save/campaign", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> saveCampaign(@RequestHeader(value="account") int account,
                                                    @RequestBody() ObjectNode eventRequest) {
    return campaignDao.saveCampaign(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }


  @RequestMapping(value = "/get/campaigns", method = { RequestMethod.GET, RequestMethod.POST })
  public Mono<ResponseEntity<Object>> getCampaigns(@RequestHeader(value="account") int account,
                                                   @RequestBody() ObjectNode eventRequest) {
    return campaignDao.getCampaigns(account,eventRequest).map(ShaftResponseHandler::generateResponse);
  }
}
