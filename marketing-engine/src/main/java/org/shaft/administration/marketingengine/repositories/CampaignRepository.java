package org.shaft.administration.marketingengine.repositories;

import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.shaft.administration.marketingengine.repositories.Campaign.CampaignCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends ReactiveCrudRepository<CampaignCriteria,String>, CampaignCustomRepository {
}
