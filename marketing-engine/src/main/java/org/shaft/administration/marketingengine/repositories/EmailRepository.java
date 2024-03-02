package org.shaft.administration.marketingengine.repositories;

import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.shaft.administration.marketingengine.repositories.Email.EmailCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EmailRepository extends ReactiveCrudRepository<CampaignCriteria,String>, EmailCustomRepository {
}
