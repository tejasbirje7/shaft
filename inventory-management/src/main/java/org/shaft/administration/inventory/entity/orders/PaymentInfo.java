package org.shaft.administration.inventory.entity.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class PaymentInfo {
    private String holderName;
    private long cardNumber;
    private String expiredDate;
    private int cvc;

}
