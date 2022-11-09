package org.shaft.administration.inventory.entity.orders;

public class PaymentInfo {
    private String holderName;
    private long cardNumber;
    private String expiredDate;
    private int cvc;

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getCvc() {
        return cvc;
    }

    public void setCvc(int cvc) {
        this.cvc = cvc;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "holderName='" + holderName + '\'' +
                ", cardNumber=" + cardNumber +
                ", expiredDate='" + expiredDate + '\'' +
                ", cvc=" + cvc +
                '}';
    }
}
