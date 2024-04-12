package com.example.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class information extends Fragment implements DatePickerDialog.OnDateSetListener {
    private SharedViewModel sharedViewModel;

    private EditText fullNameEditText;
    private EditText addressEditText;
    private EditText phoneNumberEditText;
    private String gender;
    private EditText dob, date;
    private Button nextButton;
    private RadioButton male, female, other;
    private String selectedDate2 = "";
    private String selectedDate1 = "";
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_infor, container, false);
        fullNameEditText = view.findViewById(R.id.patientname);
        addressEditText = view.findViewById(R.id.patientaddress);
        dob = view.findViewById(R.id.btnDate);
        nextButton = view.findViewById(R.id.nextfrag);
        phoneNumberEditText = view.findViewById(R.id.patientphoneno);
        date = view.findViewById(R.id.patie);
        db = FirebaseFirestore.getInstance();
        male = view.findViewById(R.id.radioMale);
        female = view.findViewById(R.id.radioFemale);
        other = view.findViewById(R.id.radioOther);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        date.setOnClickListener(v -> showDatePickerDialog(true));

        dob.setOnClickListener(v -> showDatePickerDialog(false));

        nextButton.setOnClickListener(v -> {
            String dob = selectedDate2.trim();
            String fullName = fullNameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String appointmentDate = selectedDate1.trim();

            if (male.isChecked()) {
                gender = "Male";
            } else if (female.isChecked()) {
                gender = "Female";
            } else if (other.isChecked()) {
                gender = "Other";
            } else {
                Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fullName.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneNumber.length() != 10) {
                Toast.makeText(requireContext(), "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dob.isEmpty()) {
                Toast.makeText(requireContext(), "Please select Date of Birth", Toast.LENGTH_SHORT).show();
                return;
            }

            savePatientInformation(fullName, address, dob, phoneNumber, gender, appointmentDate);
        });

        return view;
    }

    private void showDatePickerDialog(boolean isDateField) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
            if (isDateField) {
                date.setText(selectedDate);
                selectedDate = formatDate(year1, month1, dayOfMonth); // Format the date for storing in Firestore
                selectedDate1 = selectedDate;
            } else {
                dob.setText(selectedDate);
                selectedDate2 = formatDate(year1, month1, dayOfMonth); // Format the date for storing in Firestore
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void savePatientInformation(String fullName, String address, String dob, String phoneNumber, String gender, String appointmentDate) {
        Patient patient = new Patient(fullName, address, dob, phoneNumber, gender, appointmentDate);

        db.collection("patients").add(patient)
                .addOnSuccessListener(documentReference -> {
                    String patientId = documentReference.getId();
                    if (patientId == null) {
                        Toast.makeText(requireContext(), "Patient ID is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getActivity() instanceof NavigationListener) {
                        sharedViewModel.setPatientId(patientId);
                        ((NavigationListener) getActivity()).navigateToNextFragment();
                    } else {
                        Toast.makeText(requireContext(), "NavigationListener not implemented in activity", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save patient information", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        selectedDate2 = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
        selectedDate1 = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
    }
}
