package org.shaft.administration.marketingengine.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.marketingengine.dao.CampaignDao;
import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@EnableScheduling
@Slf4j
public class CampaignWatcher {

  private final CampaignDao campaignDao;
  private final ObjectMapper mapper;

  public CampaignWatcher(CampaignDao campaignDao) {
    this.campaignDao = campaignDao;
    this.mapper = new ObjectMapper();
  }
  @Bean
  public Runnable pbsCampaignWatcher() {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 10000)
      public void run() {
        campaignDao.getActivePBSCampaigns(1600)
          .publishOn(Schedulers.boundedElastic())
          .map(campaignsResponse -> {
            ObjectNode response = mapper.convertValue(campaignsResponse,ObjectNode.class);
            if(response.has("code") && response.get("code").asText().contains("S")) {
              ArrayNode campaigns = (ArrayNode) response.get("data");
              for (JsonNode cc: campaigns) {
                log.info("Campaigns {}", cc);
                campaignDao.qualifyUsersForCampaign(1600,cc)
                  .map(isSuccess -> {
                    log.info("Response : {}",isSuccess);
                    return Mono.empty();
                  }).block();
              }
            }
            return mapper.createObjectNode();
          }).block();
      }
    };
  }

}
