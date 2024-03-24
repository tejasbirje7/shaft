package org.shaft.administration.marketingengine.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.marketingengine.clients.QueueRestClient;
import org.shaft.administration.marketingengine.dao.CampaignDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.shaft.administration.marketingengine.constants.CampaignConstants.*;
import static org.shaft.administration.obligatory.constants.ShaftResponseCode.*;

@Component
@EnableScheduling
@Slf4j
public class CampaignWatcher {

  private final CampaignDao campaignDao;
  private final ObjectMapper mapper;
  private final QueueRestClient queueRestClient;
  private final ObjectNode CAMPAIGNS_STATUS_NOT_UPDATED;
  private final ObjectNode CAMPAIGNS_NOT_ENQUEUED;

  public CampaignWatcher(CampaignDao campaignDao, QueueRestClient queueRestClient) {
    this.campaignDao = campaignDao;
    this.queueRestClient = queueRestClient;
    this.mapper = new ObjectMapper();
    this.CAMPAIGNS_NOT_ENQUEUED = mapper.createObjectNode();
    this.CAMPAIGNS_STATUS_NOT_UPDATED = mapper.createObjectNode();
  }
  @Bean
  public Runnable pbsCampaignWatcher() {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 10000)
      public void run() {
        final int account = 1600;
        campaignDao.getActivePBSCampaigns(account)
          .publishOn(Schedulers.boundedElastic())
          .mapNotNull(campaignsResponse -> {
            ObjectNode response = mapper.convertValue(campaignsResponse,ObjectNode.class);
            if(isSuccessResponse(response)) {
              ArrayNode campaigns = (ArrayNode) response.get(SHAFT_RESPONSE_DATA_KEY);
              for (JsonNode cc: campaigns) {
                log.info("Campaigns {}", cc);
                final int CID = cc.get(CAMPAIGN_ID).asInt();
                final String CID_STR = String.valueOf(CID);
                if(!CAMPAIGNS_STATUS_NOT_UPDATED.has(CID_STR)) {
                  return queueRestClient.enqueueScheduledCampaign(account,cc)
                    .publishOn(Schedulers.boundedElastic())
                    .mapNotNull(queueResponse -> {
                      if (isSuccessResponse(queueResponse)) {
                        return campaignDao.updateCampaignStatus(account, CID)
                          .map(statusUpdateResponse -> {
                            if (isSuccessResponse(statusUpdateResponse)) {
                              return ShaftResponseBuilder.buildResponse(CAMPAIGN_QUEUED_SUCCESS, mapper.createObjectNode().put("success", true));
                            } else {
                              CAMPAIGNS_STATUS_NOT_UPDATED.put(CID_STR, statusUpdateResponse.get(SHAFT_RESPONSE_CODE_KEY).asText());
                              log.error("Failed updating campaign status {}", statusUpdateResponse.get(SHAFT_RESPONSE_CODE_KEY));
                              return ShaftResponseBuilder.buildResponse(FAILED_UPDATING_CAMPAIGN_STATUS);
                            }
                          }).onErrorResume(exception -> {
                            CAMPAIGNS_STATUS_NOT_UPDATED.put(CID_STR, exception.getMessage());
                            log.error("Exception updating campaign status {}", exception.getMessage());
                            return Mono.just(ShaftResponseBuilder.buildResponse(EXCEPTION_UPDATING_CAMPAIGN_STATUS));
                          }).block();
                      } else {
                        CAMPAIGNS_NOT_ENQUEUED.put(CID_STR, queueResponse.get(SHAFT_RESPONSE_CODE_KEY).asText());
                        log.error("Failed to enqueue campaign {}", queueResponse.get(SHAFT_RESPONSE_CODE_KEY));
                        return ShaftResponseBuilder.buildResponse(FAILED_ENQUEUE_CAMPAIGN);
                      }
                    })
                    .onErrorResume(error -> {
                      CAMPAIGNS_NOT_ENQUEUED.put(CID_STR, error.getMessage());
                      log.error("Exception to enqueue campaign {}", error.getMessage());
                      return Mono.just(ShaftResponseBuilder.buildResponse(EXCEPTION_ENQUEUE_CAMPAIGN));
                    }).block();
                }
              }
            }
            return mapper.createObjectNode();
          }).block();
      }
    };
  }

  private boolean isSuccessResponse(ObjectNode response) {
    return response.has(SHAFT_RESPONSE_CODE_KEY) && response.get(SHAFT_RESPONSE_CODE_KEY).asText().contains(SHAFT_RESPONSE_SUCCESS_KEY);
  }

  @Bean
  public Runnable campaignWithStatusUpdateFailed() {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 10000)
      public void run() {
        log.info("Campaigns Status not updated {}",CAMPAIGNS_STATUS_NOT_UPDATED);
      }
    };
  }

  @Bean
  public Runnable campaignFailedToEnqueue() {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 10000)
      public void run() {
        log.info("Campaigns not queued {}",CAMPAIGNS_NOT_ENQUEUED);
      }
    };
  }

}
