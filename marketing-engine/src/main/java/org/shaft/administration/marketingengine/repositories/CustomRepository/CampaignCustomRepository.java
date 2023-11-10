package org.shaft.administration.marketingengine.repositories.CustomRepository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CampaignCustomRepository {
  Flux<CampaignCriteria> checkIfCampaignExistsForEvent(int account, int eventId);
  ObjectNode checkEligibleCampaignsForI(int accountId, String query);
  Mono<CampaignCriteria> save(int accountId, CampaignCriteria cc);
  Flux<CampaignCriteria> getSavedCampaigns(int accountId);
}
