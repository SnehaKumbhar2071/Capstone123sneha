package com.example.home;

import java.util.ArrayList;

public class DataClass {
    private String date;
    private String fullName;
    private String address;
    private String phoneNumber;
    private String dob;
    private String gender;
    private ArrayList<String> prepostimages;
    private ArrayList<String> prescription;
    private String status;
    private ArrayList<String> treatment;

    public ArrayList<String> getTreatment() {
        return treatment;
    }

    public void setTreatment(ArrayList<String> treatment) {
        this.treatment = treatment;
    }





    private String key;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getPrepostimages() {
        return prepostimages;
    }

    public void setPrepostimages(ArrayList<String> prepostimages) {
        this.prepostimages = prepostimages;
    }

    public ArrayList<String> getPrescription() {
        return prescription;
    }

    public void setPrescription(ArrayList<String> prescription) {
        this.prescription = prescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String fullName, String date, String address, String phoneNumber, String dob, String gender,ArrayList<String> prepostimages,ArrayList<String> prescription,String status,ArrayList<String> treatment) {
        this.fullName = fullName;
        this.date = date;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.gender = gender;
        this.prepostimages=prepostimages;
        this.prescription=prescription;
        this.status=status;
        this.treatment=treatment;

    }
    public DataClass(){
    }

    public String getPaymentStatus() {
        return "done";
    }
}
