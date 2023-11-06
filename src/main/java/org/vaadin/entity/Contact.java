package org.vaadin.entity;


import java.time.LocalDateTime;


public class Contact {


    private String name;
    private String phoneNumber;
    private String street;
    private String city;
    private String country;
    private String email;
    private LocalDateTime lastUpdatedTime;

    public Contact() {
    }

    public Contact(String name, String phoneNumber, String email, String street, String city, String country, LocalDateTime dateTime) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.city = city;
        this.country = country;
        this.email = email;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

}
