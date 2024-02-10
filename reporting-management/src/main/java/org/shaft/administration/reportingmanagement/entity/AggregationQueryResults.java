package org.shaft.administration.reportingmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.List;


/*
{
  "took": 0,
  "timed_out": false,
  "_shards": {
      "total": 1,
      "successful": 1,
      "skipped": 0,
      "failed": 0
  },
  "hits": {
      "total": {
          "value": 0,
          "relation": "eq"
      },
      "max_score": null,
      "hits": []
  },
  "aggregations": {
      "user_count": {
          "doc_count_error_upper_bound": 0,
          "sum_other_doc_count": 0,
          "buckets": []
      },
      "graph_plot": {
          "buckets": [
              {
                  "key": "1.67073644E9-1.67073644E9",
                  "from": 1.67073644E9,
                  "to": 1.67073644E9,
                  "doc_count": 0,
                  "users": {
                      "doc_count_error_upper_bound": 0,
                      "sum_other_doc_count": 0,
                      "buckets": []
                  }
              }
          ]
      }
  }
}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class UserCount {
    int doc_count_error_upper_bound;
    int sum_other_doc_count;
    List<Buckets> buckets;
}
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class GraphPlot {
    List<Buckets> buckets;
}
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Buckets {
    String key;
    double from;
    double to;
    int doc_count;
    UserCount users;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Aggregation {
    UserCount user_count;
    GraphPlot graph_plot;
}
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregationQueryResults {
    Aggregation aggregations;

    public int getUserCount() {
        // #TODO First check if document exists and then directly get(0) - Error handling
        return this.aggregations.getUser_count().getBuckets().get(0).getDoc_count() + this.aggregations.getUser_count().getSum_other_doc_count();
    }

    public ArrayNode getGraphCount() {
        ObjectMapper mapper = new ObjectMapper();
        List<Buckets> buckets = this.aggregations.getGraph_plot().getBuckets();
        ArrayNode graphCount = mapper.createArrayNode();
        buckets.forEach(b -> {
            ObjectNode eachBucketDetails = mapper.createObjectNode();
            eachBucketDetails.put("from",b.getFrom());
            eachBucketDetails.put("to",b.getTo());
            if(b.getUsers() != null) {
                eachBucketDetails.put("u",b.getDoc_count());
            } else {
                eachBucketDetails.put("u",-1);
            }
            graphCount.add(eachBucketDetails);
        });
        return graphCount;
    }

}
