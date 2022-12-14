package org.shaft.administration.reportingmanagement.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "#{T(org.shaft.administration.reportingmanagement.services.QueryDAOImpl).getAccount()}_savedfilt")
public class Segment {
    @Id
    int sid;
    String nm;
    String q;
}
