package org.shaft.administration.marketingengine.entity.CampaignCriteria;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CampaignCriteria {
  @Id
  private int cid;
  private int status;
  private String q;
  private String nm;
  private TriggerEvent te;
  private FilterStrings fs;
}
