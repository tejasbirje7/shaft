package org.shaft.administration.inventory.entity.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class CustomerInfo {
    private String name;
    private String email;
    private long phoneNumber;
}
