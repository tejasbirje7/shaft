package org.shaft.administration.inventory.entity.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class Item {
    @Field("id")
    @Id
    private String id;
    private int costPrice;
    private int quantity;
    private String option;

}
