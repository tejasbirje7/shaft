package org.shaft.administration.marketingengine.entity.CampaignCriteria;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
  private String img;
  private String type;
  private boolean spawned;
  private int mode;
}
