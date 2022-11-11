package org.shaft.administration.inventory.entity.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class DeliveryInfo {

    private String recipientName;
    private long recipientNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postcode;
    private String country;

}
