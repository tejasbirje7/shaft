package org.shaft.administration.inventory.entity.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class Item {
    private String id;
    private int costPrice;
    private int quantity;
    private String option;

}
