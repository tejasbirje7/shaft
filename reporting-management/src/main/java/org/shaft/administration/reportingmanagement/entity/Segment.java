package org.shaft.administration.reportingmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "#{T(org.shaft.administration.reportingmanagement.services.QueryDAOImpl).getAccount()}_savedfilt")
public class Segment {
    int sid;
    String nm;
    String q;
}
