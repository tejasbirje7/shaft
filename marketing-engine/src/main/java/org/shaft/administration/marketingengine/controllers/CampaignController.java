package org.shaft.administration.marketingengine.controllers;

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
    campaignDao.checkForCampaignQualification(account,eventRequest);
    return Mono.empty();
  }
}
