package org.shaft.administration.usermanagement.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@NoArgsConstructor
@Data
@Document(indexName = "users")
public class User {
    @Id
    int i;
    String nm;
    String e;
    String p;
    String c;

}
