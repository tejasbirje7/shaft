package org.shaft.administration.usermanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class EventIndex {

  @Id
  long i;
  List<Object> e;
  String email;

}
