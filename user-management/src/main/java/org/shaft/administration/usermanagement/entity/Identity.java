package org.shaft.administration.usermanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "#{T(org.shaft.administration.usermanagement.services.IdentityDAOImpl).getAccount()}_devices")
public class Identity {

    @Id
    private String _id = UUID.randomUUID().toString();
    @Field(name = "fp")
    private List<Map<String,String>> fingerPrint;
    @Field(name = "i")
    private int identity;
    private boolean isIdentified;
    private int requestTime;
}
