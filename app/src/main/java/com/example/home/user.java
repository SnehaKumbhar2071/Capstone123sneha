package com.example.home;

import java.io.Serializable;
import java.util.ArrayList;

public class user implements Serializable {
    private String fullName;
    private String date;
    private String id;
    private ArrayList<String> treatment;
    private ArrayList<String> prepostimages;
    private ArrayList<String> prescription;

    public user() {}

    public user(String fullName, String date, ArrayList<String> treatment, ArrayList<String> prepostimages, ArrayList<String> prescription) {
        this.fullName = fullName;
        this.date = date;
        this.treatment = treatment;
        this.prepostimages = prepostimages;
        this.prescription = prescription;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getTreatment() {
        return treatment;
    }

    public void setTreatment(ArrayList<String> treatment) {
        this.treatment = treatment;
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

    public String getName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
