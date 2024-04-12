package com.example.myapp;

public class Patient {
    private  String date;
    private  String gender;
    private String fullName;
    private String address;
    private String dob;
    private String phoneNumber;



    public Patient() {
        // Default constructor required for Firebase
    }

    public Patient(String fullName, String address, String dob, String phoneNumber,String gender,String date) {
        this.fullName = fullName;
        this.address = address;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.gender=gender;
        this.date=date;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
