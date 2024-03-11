package org.shaft.administration.usermanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
public class EventIndex {

  @Id
  long i;
  List<Object> e;
  Map<String,Object> props;

}
