package org.shaft.administration.inventory.entity.orders;

public class DeliveryInfo {

    private String recipientName;
    private long recipientNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postcode;
    private String country;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public long getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(long recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "DeliveryInfo{" +
                "recipientName='" + recipientName + '\'' +
                ", recipientNumber=" + recipientNumber +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postcode=" + postcode +
                ", country='" + country + '\'' +
                '}';
    }
}
