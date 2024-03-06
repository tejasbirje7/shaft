package org.shaft.administration.usermanagement.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@NoArgsConstructor
@Data
@Document(indexName = "users") // #TODO Not to store all users in generic collection instead there should be index <account_id>_users
public class User {
    @Id
    long i;
    long c;
    int a;
    String nm;
    String e;
    String p;

}
