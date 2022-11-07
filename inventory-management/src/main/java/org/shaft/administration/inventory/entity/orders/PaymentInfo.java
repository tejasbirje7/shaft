package org.shaft.administration.inventory.entity.orders;

public class PaymentInfo {
    private String holderName;
    private long cardNumber;
    private String expiredDate;
    private int cvv;

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

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "holderName='" + holderName + '\'' +
                ", cardNumber=" + cardNumber +
                ", expiredDate='" + expiredDate + '\'' +
                ", cvv=" + cvv +
                '}';
    }
}
