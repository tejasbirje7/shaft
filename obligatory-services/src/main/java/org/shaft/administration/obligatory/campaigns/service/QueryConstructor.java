package org.shaft.administration.obligatory.campaigns.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.binary.Base64;

import java.util.List;
import java.util.Map;

public class QueryConstructor {

  private final ObjectMapper mapper;

  public QueryConstructor() {
    this.mapper = new ObjectMapper();
  }

  public ObjectNode constructMsearchQuery(List<Map<String,Object>> campaignCriteria, Map<String,Object> evtInRequest, int i,String index) {
    ArrayNode cidMap = mapper.createArrayNode();
    ArrayNode campaignWithoutQ = mapper.createArrayNode();
    StringBuilder request = new StringBuilder();
    String lineSeparator = System.getProperty("line.separator");
    campaignCriteria.forEach(eachCriteria -> {
      System.out.println(eachCriteria.size());
      ObjectNode eachNode = mapper.convertValue(eachCriteria,ObjectNode.class);
      if(didTriggerEventMatch(eachNode,evtInRequest)) {
        int cid = eachNode.get("cid").asInt();
        String query = eachNode.get("q").asText();
        ObjectNode qToAppend = appendIToMustQuery(query,i);
        ObjectNode m = mapper.createObjectNode();
        m.put("cid",cid);
        m.put("icn","");
        m.set("te",eachNode.get("te"));
        if(!qToAppend.isEmpty()) {
          if(request.toString().isEmpty()) {
            request.append(mapper.createObjectNode().put("index", index));
            request.append(lineSeparator).append(qToAppend);
          } else {
            request.append(lineSeparator).append(mapper.createObjectNode().put("index", index));
            request.append(lineSeparator).append(qToAppend);
          }
          cidMap.add(m);
        } else {
          campaignWithoutQ.add(m);
        }
      }
    });
    request.append(lineSeparator);
    cidMap.addAll(campaignWithoutQ);
    ObjectNode returnParameters = mapper.createObjectNode();
    returnParameters.set("cidMap",cidMap);
    returnParameters.put("queries", String.valueOf(request));
    return returnParameters;
  }

  private boolean didTriggerEventMatch(ObjectNode cc, Map<String,Object> e) {
    if(cc.has("te")) {
      if(cc.get("te").has("fe")) {
        ObjectNode eventRequest = mapper.convertValue(e,ObjectNode.class);
        ObjectNode te = mapper.convertValue(cc.get("te"),ObjectNode.class);
        if(te.has("o") && !te.get("o").asText().isEmpty() && eventRequest.has(cc.get("te").get("f").asText())) {
          String eventFieldToCompare = eventRequest.get(cc.get("te").get("f").asText()).asText();
          if("GT".equals(te.get("GT").asText())) {
            return Integer.parseInt(eventFieldToCompare) > cc.get("te").get("v").asInt();
          } else if ("LT".equals(te.get("LT").asText())) {
            return Integer.parseInt(eventFieldToCompare) < cc.get("te").get("v").asInt();
          } else if ("GTE".equals(te.get("GTE").asText())) {
            return Integer.parseInt(eventFieldToCompare) >= cc.get("te").get("v").asInt();
          } else if ("LTE".equals(te.get("LTE").asText())) {
            return Integer.parseInt(eventFieldToCompare) <= cc.get("te").get("v").asInt();
          } else if ("EQ".equals(te.get("EQ").asText())) {
            return eventFieldToCompare.equals(cc.get("te").get("v").asText());
          } else if ("NE".equals(te.get("NE").asText())) {
            return !eventFieldToCompare.equals(cc.get("te").get("v").asText());
          }
        } else {
          ObjectNode parsedE = mapper.convertValue(e, ObjectNode.class);
          return parsedE.get("e").get("eid").asInt() == cc.get("te").get("e").asInt();
        }
      }
    }
    return false;
  }


  private ObjectNode appendIToMustQuery(String query, int i) {
    byte[] bytesEncoded = Base64.decodeBase64(query.getBytes());
    JsonNode jsonQuery;
    try {
      jsonQuery = mapper.readTree(new String(bytesEncoded));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode q = jsonQuery.get("query");
    if(!q.has("bool")) {
      return mapper.createObjectNode();
    }
    ObjectNode boolQuery = (ObjectNode) q.get("bool");
    JsonNode qToAppend = mapper.createObjectNode().set("bool",
      mapper.createObjectNode().set("must",
        mapper.createArrayNode().add(mapper.createObjectNode().set("term",
          mapper.createObjectNode().set("i",
            mapper.createObjectNode().put("value",i))))));
    if(boolQuery.has("must")) {
      ArrayNode an = (ArrayNode) boolQuery.get("must");
      an.add(qToAppend);
      boolQuery.set("must",an);
    } else {
      boolQuery.set("must",qToAppend);
    }
    ObjectNode returnQuery = mapper.createObjectNode();
    returnQuery.set("query",mapper.createObjectNode().set("bool",boolQuery));
    returnQuery.set("_source",mapper.createArrayNode().add("i").add("u"));
    return returnQuery;
  }

}
