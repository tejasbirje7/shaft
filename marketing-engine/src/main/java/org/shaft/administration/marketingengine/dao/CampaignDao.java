package org.shaft.administration.marketingengine.dao;

import java.util.Map;

public interface CampaignDao {
  void checkForCampaignQualification(int accountId, Map<String,Object> request);
}
