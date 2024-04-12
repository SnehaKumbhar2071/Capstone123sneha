package com.example.home;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private String patientId;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}