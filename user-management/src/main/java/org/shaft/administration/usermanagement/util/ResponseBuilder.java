package org.shaft.administration.usermanagement.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResponseBuilder {

  static ObjectMapper mapper = new ObjectMapper();

  public static ObjectNode buildResponse(String code, JsonNode data) {
    ObjectNode resp = mapper.createObjectNode();
    resp.put("code",code);
    resp.set("data",data);
    return resp;
  }

  public static ObjectNode buildResponse(String code) {
    ObjectNode resp = mapper.createObjectNode();
    resp.put("code",code);
    resp.set("data",mapper.createObjectNode());
    return resp;
  }
}
