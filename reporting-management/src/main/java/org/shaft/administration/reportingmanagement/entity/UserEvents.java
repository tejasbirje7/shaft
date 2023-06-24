package org.shaft.administration.reportingmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
@NoArgsConstructor
@ToString
@Getter
@Setter
public class UserEvents {
    @Id
    private int i;
    private List<Event> e;
}
