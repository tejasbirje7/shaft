package com.example.demo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@NoArgsConstructor
@Data
@Document(indexName = "users")
public class User {
    @Id
    int i;
    int c;
    int a;
    String nm;
    String e;
    String p;

}
