package org.shaft.administration.marketingengine.entity.CampaignCriteria;

import lombok.Data;

import java.util.List;

@Data
public class FilterStrings {
  private List<String> whoDid;
  private List<String> didNot;
  private List<String> commonProp;

}
