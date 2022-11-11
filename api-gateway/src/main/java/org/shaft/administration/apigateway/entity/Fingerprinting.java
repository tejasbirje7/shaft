package org.shaft.administration.apigateway.entity;

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
@Document(indexName = "1600_devices")
public class Fingerprinting {

    @Id
    private String _id = UUID.randomUUID().toString();
    @Field(name = "fp")
    private List<Map<String,String>> fingerPrint;
    @Field(name = "i")
    private int identity;
    private boolean isIdentified;
    private int requestTime;
}
