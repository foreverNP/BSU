package com.project4;

public class Manufacturer {
    private int id;
    private String name;
    private String country;
    private String contactInfo;


    public Manufacturer(int id, String name, String country, String contactInfo) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.contactInfo = contactInfo;
    }

    public Manufacturer(int id, String name) {
        this.id = id;
        this.name = name;
        this.country = "";
        this.contactInfo = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return name;
    }
}