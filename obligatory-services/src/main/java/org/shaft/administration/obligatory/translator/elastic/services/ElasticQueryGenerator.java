package org.shaft.administration.obligatory.translator.elastic.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.shaft.administration.obligatory.translator.elastic.constants.QueryConstants.*;

/**
 *  This class generates a aggregation query which will give results of total users performing `x` no. of events
 *  `user_count` will be number of users
 *  `graph_plot` will have buckets of date range
 *  Never compare user count documents with graph_plot numbers in bucket for that time range
 *  As a user can perform a given event multiple time and could be included in multiple buckets.
 *  If you search for a document says e.eid=2 then the count will be more when compared to bucket number
 *  because there will be results of other time range too when searched with e.eid=2 in actual result in kibana or query console
 */
public class ElasticQueryGenerator {
    private final ObjectMapper mapper = new ObjectMapper();
    private int from = 0;
    private int to = 0;
    private boolean isCampaignQuery = false;
    public static void main(String[] args) {
        ElasticQueryGenerator qG = new ElasticQueryGenerator();
        try {

            String t = "Tejas,Tejas2";
            ArrayNode m = qG.mapper.convertValue(t.split(","),ArrayNode.class);


            qG.mapper.configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true);
            qG.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            qG.mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            ObjectNode o = qG.mapper.readValue("{\"whoDid\":[[{\"e\":0,\"fe\":false,\"o\":\"\",\"f\":\"\",\"v\":\"\",\"sT\":1635705000,\"eT\":1636137000},{\"e\":1,\"fe\":true,\"o\":\"EQ\",\"f\":\"nm\",\"v\":[\"Chicken Handi\",\"Veg Pulav\"],\"sT\":1635705000,\"eT\":1636137000},{\"e\":2,\"fe\":true,\"o\":\"GTE\",\"f\":\"p\",\"v\":\"20\",\"sT\":1635705000,\"eT\":1636137000}],[{\"e\":0,\"fe\":false,\"o\":\"\",\"f\":\"\",\"v\":\"\",\"sT\":1635705000,\"eT\":1636137000}]],\"didNot\":[],\"commonProp\":[]}",ObjectNode.class);
            ObjectNode i = qG.mapper.readValue("{\"whoDid\":[[{\"e\":1,\"fe\":true,\"o\":\"EQ\",\"f\":\"cg\",\"v\":\"2343\",\"sT\":1670736428,\"eT\":1670736428,\"dt\":\"Integer\"},{\"e\":1,\"fe\":true,\"o\":\"EQ\",\"f\":\"cg\",\"v\":\"f676,dfsf,sf,sdfsdf\",\"sT\":1670736440,\"eT\":1670736440,\"dt\":\"String\"}]],\"didNot\":[],\"commonProp\":[]}",ObjectNode.class);
            ObjectNode k = qG.prepareAnalyticsQuery(i,true);

            String json = qG.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(k);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectNode getEventQuery(int eventId) {
        return mapper.createObjectNode().set("term",
          mapper.createObjectNode().set("e.eid",
            mapper.createObjectNode()
              .put("value", eventId)));
    }

    private ObjectNode getRangeQuery(ObjectNode query) {
        return mapper.createObjectNode().set("range",
          mapper.createObjectNode().set("e.".concat(query.get("f").textValue()),
            mapper.createObjectNode().put(query.get("o").textValue().toLowerCase(),query.get(VALUE).asInt())));
    }

    private ObjectNode getDateRangeQuery(int from, int to) {
        return mapper.createObjectNode().set("range",
          mapper.createObjectNode().set("e.ts",
            mapper.createObjectNode().put(GTE,from).put(LTE,to)));
    }

    private ObjectNode getTermsQuery(ObjectNode query) {
        if(isDataTypeInteger(query)) {
            List<Integer> ints = Arrays.stream(query.get(VALUE).textValue().split(","))
              .map(Integer::parseInt)
              .collect(Collectors.toList());
            ArrayNode value = mapper.convertValue(ints,ArrayNode.class);
            return mapper.createObjectNode().set("terms",
              mapper.createObjectNode().set("e.".concat(query.get("f").textValue()), value));
        } else {
            ArrayNode value = mapper.convertValue(query.get(VALUE).textValue().split(","),ArrayNode.class);
            return mapper.createObjectNode().set("terms",
              mapper.createObjectNode().set("e.".concat(query.get("f").textValue()).concat(".keyword"), value));
        }
    }

    private ObjectNode getTermQuery(ObjectNode query) {
        if(isDataTypeInteger(query)) {
            return mapper.createObjectNode().set("term" ,
              mapper.createObjectNode().set("e.".concat(query.get("f").textValue()),
                mapper.createObjectNode().put("value",query.get(VALUE).asInt())));
        } else {
            return mapper.createObjectNode().set("term" ,
              mapper.createObjectNode().set("e.".concat(query.get("f").textValue()).concat(".keyword"),
                mapper.createObjectNode().put("value",query.get(VALUE).asText())));
        }
    }

    private boolean isDataTypeInteger(JsonNode query) {
        return query.has("dt") && query.get("dt").asInt() == INTEGER_ORDINAL;
    }

    private boolean isNotOperator(String operator) {
        return "NE".equals(operator);
    }

    private ObjectNode getQueryForNotOperator(String queryType,ObjectNode query) {
        if("range".equals(queryType)) {
            return getRangeQuery(query);
        } else if ("terms".equals(queryType)) {
            return getTermsQuery(query);
        } else if ("term".equals(queryType)) {
            return getTermQuery(query);
        } else {
            throw new RuntimeException("No elastic found");
        }
    }

    private ObjectNode createBoolQuery(ArrayNode mustQuery, ArrayNode mustNotQuery) {
        ObjectNode boolQuery = mapper.createObjectNode();
        if (mustQuery.size() > 0 && mustNotQuery.size() > 0) {
            ObjectNode b = mapper.createObjectNode();
            b.set("must",mustQuery);
            b.set("must_not", mapper.createObjectNode().set("must_not",mapper.createObjectNode().set("bool",mapper.createObjectNode().set("must",mustNotQuery))));
            return boolQuery.set("bool",b);
        } else if (mustQuery.size() > 0) {
            return boolQuery.set("bool",mapper.createObjectNode().set("must",mustQuery));
        } else if (mustNotQuery.size() > 0) {
            return boolQuery.set("bool",mapper.createObjectNode().set("must_not",mustNotQuery));
        } else {
            if(isCampaignQuery) {
                return mapper.createObjectNode();
            }
            throw new RuntimeException("Query is empty");
        }
    }


    private ObjectNode getBoolFilteredQuery(ObjectNode jsonRequest) {
        ArrayNode mustQuery = mapper.createArrayNode();
        ArrayNode mustNotQuery = mapper.createArrayNode();
        Iterator<Map.Entry<String, JsonNode>> it = jsonRequest.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            ArrayNode queries = (ArrayNode) entry.getValue();
            for(int i = 0; i < queries.size(); i++) {
                ArrayNode should = mapper.createArrayNode();
                JsonNode query = queries.get(i);
                for(int j = 0; j<query.size(); j++) {
                    JsonNode q = query.get(j);
                    from = Math.max(from, q.get("sT").intValue());
                    to = Math.max(to, q.get("eT").intValue());
                    ObjectNode newQuery = mapper.createObjectNode();
                    if (q.size() > 3) {
                        ArrayNode eachQuery = mapper.createArrayNode();
                        String qType = "";
                        if(q.has("o") && !"EQ".equals(q.get("o").textValue()) && !"NE".equals(q.get("o").textValue())) {
                            qType = "range";
                            if(!q.get("o").textValue().isEmpty()) {
                                if (isNotOperator(q.get("o").textValue())) {
                                    eachQuery.add(getEventQuery(q.get("e").intValue()));
                                    eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                                } else {
                                    eachQuery.add(getRangeQuery((ObjectNode) q));
                                    eachQuery.add(getEventQuery(q.get("e").intValue()));
                                    eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                                }
                            } else {
                                eachQuery.add(getEventQuery(q.get("e").intValue()));
                                eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                            }
                        } else if (q.has(VALUE) && q.get(VALUE).textValue().contains(",")) {
                            qType = "terms";
                            if (q.has("o") && isNotOperator(q.get("o").textValue())) {
                                eachQuery.add(getEventQuery(q.get("e").intValue()));
                                eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                            } else {
                                eachQuery.add(getTermsQuery((ObjectNode) q));
                                eachQuery.add(getEventQuery(q.get("e").intValue()));
                                eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                            }
                        } else {
                            qType = "term";
                            if (q.has("o") && isNotOperator(q.get("o").textValue())) {
                                eachQuery.add(getEventQuery(q.get("e").intValue()));
                                eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                            } else {
                                eachQuery.add(getTermQuery((ObjectNode) q));
                                eachQuery.add(getEventQuery(q.get("e").intValue()));
                                eachQuery.add(getDateRangeQuery(q.get("sT").intValue(),q.get("eT").intValue()));
                            }
                        }
                        if (q.has("o") && isNotOperator(q.get("o").textValue())) {
                            newQuery.set("must",eachQuery);
                            newQuery.set("must_not",mapper.createArrayNode().add(getQueryForNotOperator(qType, (ObjectNode) q)));
                        } else {
                            newQuery.set("bool", mapper.createObjectNode().set("must",eachQuery));
                        }
                        should.add(newQuery);
                    }
                }
                if("whoDid".equals(entry.getKey()) || "commonProp".equals(entry.getKey())) {
                    mustQuery.add(mapper.createObjectNode().set("bool",mapper.createObjectNode().set("should",should)));
                } else if ("didNot".equals(entry.getKey())) {
                    mustNotQuery.add(mapper.createObjectNode().set("bool",mapper.createObjectNode().set("should",should)));
                }
            }
        }
        return createBoolQuery(mustQuery,mustNotQuery);
    }

    private ArrayNode splitDateRangeInNBuckets(int x, int y) {
        ArrayNode ranges = mapper.createArrayNode();
        mapper.createObjectNode();
        ObjectNode fromTo;
        int diff = Math.floorDiv(y-x,DATE_RANGE_BUCKETS + 1);
        for (int i =0; i < DATE_RANGE_BUCKETS; i++) {
            int d = x + diff;
            if(d > y) {
                fromTo = mapper.createObjectNode();
                fromTo.put("from",x);
                fromTo.put("to",y);
                ranges.add(fromTo);
            } else {
                fromTo = mapper.createObjectNode();
                fromTo.put("from",x);
                if(i == DATE_RANGE_BUCKETS - 1  && d < y) {
                    fromTo.put("to",y);
                } else {
                    fromTo.put("to",d);
                }
                ranges.add(fromTo);
                x = x + diff;
            }
        }
        return ranges;
    }

    public ObjectNode prepareAnalyticsQuery(ObjectNode requestQuery,boolean aggs) {
        if(!aggs) {
            isCampaignQuery = true;
        }
        ObjectNode boolQuery = getBoolFilteredQuery(requestQuery);
        if(!aggs) {
            //return mapper.createObjectNode().set("query",boolQuery);
            return boolQuery;
        } else {
            ObjectNode rangeNode = mapper.createObjectNode();
            rangeNode.put("field", "e.ts");
            rangeNode.set("ranges",splitDateRangeInNBuckets(from,to));

            ObjectNode termsNode = mapper.createObjectNode();
            termsNode.put("size",1);
            termsNode.put("field","i");

            ObjectNode graphPlotNode = mapper.createObjectNode();
            graphPlotNode.set("range",rangeNode);
            graphPlotNode.set("aggs",mapper.createObjectNode().set("users",mapper.createObjectNode().set("terms",termsNode)));

            ObjectNode userCountNode = mapper.createObjectNode();
            userCountNode.set("terms",termsNode);

            ObjectNode aggsNode = mapper.createObjectNode();
            aggsNode.set("graph_plot",graphPlotNode);
            aggsNode.set("user_count",userCountNode);

            ObjectNode aggsQuery = mapper.createObjectNode();
            aggsQuery.set("aggs",aggsNode);
            aggsQuery.set("query",boolQuery);
            aggsQuery.put("size",0);

            return aggsQuery;
        }
    }
}
