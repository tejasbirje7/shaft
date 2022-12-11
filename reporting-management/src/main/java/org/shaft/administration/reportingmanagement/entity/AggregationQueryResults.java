package org.shaft.administration.reportingmanagement.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
class UserCount {
    int doc_count_error_upper_bound;
    int sum_other_doc_count;
    List<Object> buckets;
}
@Data
class GraphPlot {
    List<Buckets> buckets;
}
@Data
class Buckets {
    String key;
    double from;
    double to;
    int doc_count;
    UserCount user_count;
}

@Data
class Aggregation {
    UserCount user_count;
    GraphPlot graph_plot;
}
@Data
public class AggregationQueryResults {
    Aggregation aggregations;

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
}
