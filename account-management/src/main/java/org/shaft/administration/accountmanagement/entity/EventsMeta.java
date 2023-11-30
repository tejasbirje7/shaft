package org.shaft.administration.accountmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class EventsMeta {
  public String nm;
  public int eid;
  public List<EventProps> props;
}
