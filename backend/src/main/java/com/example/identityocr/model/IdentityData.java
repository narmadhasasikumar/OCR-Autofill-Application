package com.example.identityocr.model;

public class IdentityData {

    private String name;

    private String dateOfBirth;

    private String gender;

    private String address;

    private String identityNumber;


    public IdentityData() {
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDateOfBirth() {
        return dateOfBirth;
    }


    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getIdentityNumber() {
        return identityNumber;
    }


    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
}