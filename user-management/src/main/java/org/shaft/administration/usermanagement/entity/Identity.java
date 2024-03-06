package org.shaft.administration.usermanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
@Document(indexName = "#{T(org.shaft.administration.usermanagement.services.IdentityService).getAccount()}_device_mapping",createIndex = false)
public class Identity {


    @Field(name = "i")
    @Id
    private long identity;
    private boolean isIdentified;
    private int requestTime;
    @Field(name = "fp")
    private List<Map<String,String>> fingerPrint;
}
