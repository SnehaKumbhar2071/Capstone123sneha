package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class payment extends Fragment {

    private EditText amountEditText;
    private Button submit;
    private String status;
    private String patientId;
    private RadioButton done,notdone;
    private FirebaseFirestore firestore;
    private SharedViewModel sharedViewModel;
    private DatabaseReference databaseReference;

    public payment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        submit = view.findViewById(R.id.submitbtn);
        done = view.findViewById(R.id.radiodone);
        notdone = view.findViewById(R.id.radionotdone);
        Bundle bundle = getArguments();
        if (bundle != null) {
            patientId = bundle.getString("patientId");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (done.isChecked()) {
                    status = "Done";
                } else if (notdone.isChecked()) {
                    status = "Not done";
                } else {
                    Toast.makeText(requireContext(), "Please select status", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if gender is not selected
                }

                String patientId = sharedViewModel.getPatientId();
                if (patientId == null) {
                    Toast.makeText(requireContext(), "Patient ID is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference patientRef = firestore.collection("patients").document(patientId);

                // Update the Firestore document with the patient data
                patientRef.set(new HashMap<String, Object>() {{
                            put("status", status);
                        }}, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            // Patient data saved successfully
                            Toast.makeText(requireContext(), "Patient details successfully added", Toast.LENGTH_SHORT).show();
                            backToMainActivity(); // Navigate back to MainActivity upon successful submission
                        })
                        .addOnFailureListener(e -> {
                            // Error saving patient data
                            Toast.makeText(requireContext(), "Error saving patient data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
        return view;
    }
    private void backToMainActivity() {
        Intent intent = new Intent(getActivity(), card1.class);
        startActivity(intent);
        requireActivity().finish();// Finish the current activity to prevent navigating back to it
    }}




