package com.example.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class prescript extends Fragment {
    private SharedViewModel sharedViewModel;

    private ProgressBar progressBar;
    private ArrayList<String> treatments = new ArrayList<>();
    private ArrayList<String> teeth32 = new ArrayList<>();
    private TextView textView;
    private Button nextfrag3;
    private CheckBox c1, c2, c3, c4,c5,c6,c7,c8;
    private CheckBox t18,t17,t16,t15,t14,t13,t12,t11,t21,t22,t23,t24,t25,t26,t27,t28,t48,t47,t46,t45,t44,t43,t42,t41,t31,t32,t33,t34,t35,t36,t37,t38;

    private FirebaseFirestore firestore;

    public prescript() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_prescript, container, false);
        progressBar = view.findViewById(R.id.progressBar2);
        nextfrag3 = view.findViewById(R.id.nextfrag3);
        c1 = view.findViewById(R.id.checkbox_filling);
        c2 = view.findViewById(R.id.checkbox_periodontics);
        c3 = view.findViewById(R.id.checkbox_laminants);
        c4 = view.findViewById(R.id.checkbox_implants);
        c5 = view.findViewById(R.id.checkbox_crown);
        c6 = view.findViewById(R.id.checkbox_extraction);
        c7 = view.findViewById(R.id.checkbox_bridge);
        c8 = view.findViewById(R.id.checkbox_rc);
        t18 = view.findViewById(R.id.t18);
        t17 = view.findViewById(R.id.t17);
        t16 = view.findViewById(R.id.t16);
        t15 = view.findViewById(R.id.t15);
        t14 = view.findViewById(R.id.t14);
        t13 = view.findViewById(R.id.t13);
        t12 = view.findViewById(R.id.t12);
        t11 = view.findViewById(R.id.t11);
        t21 = view.findViewById(R.id.t21);
        t22 = view.findViewById(R.id.t22);
        t23 = view.findViewById(R.id.t23);
        t24 = view.findViewById(R.id.t24);
        t25 = view.findViewById(R.id.t25);
        t26 = view.findViewById(R.id.t26);
        t27 = view.findViewById(R.id.t27);
        t28 = view.findViewById(R.id.t28);
        t48 = view.findViewById(R.id.t48);
        t47 = view.findViewById(R.id.t47);
        t46 = view.findViewById(R.id.t46);
        t45 = view.findViewById(R.id.t45);
        t44 = view.findViewById(R.id.t44);
        t43 = view.findViewById(R.id.t43);
        t42 = view.findViewById(R.id.t42);
        t41 = view.findViewById(R.id.t41);
        t31 = view.findViewById(R.id.t31);
        t32 = view.findViewById(R.id.t32);
        t33 = view.findViewById(R.id.t33);
        t34 = view.findViewById(R.id.t34);
        t35 = view.findViewById(R.id.t35);
        t36 = view.findViewById(R.id.t36);
        t37 = view.findViewById(R.id.t37);
        t38 = view.findViewById(R.id.t38);

        nextfrag3.setOnClickListener(v -> {
            if (c1.isChecked()) treatments.add("Fillings");
            if (c2.isChecked()) treatments.add("Root Canal");
            if (c3.isChecked()) treatments.add("Extractions");
            if (c4.isChecked()) treatments.add("Laser Surgery");
            if (c5.isChecked()) treatments.add("Crown And Bridges");
            if (c6.isChecked()) treatments.add("Laminants");
            if (c7.isChecked()) treatments.add("Implants");
            if (c8.isChecked()) treatments.add("Periodontics");
            if (t18.isChecked()) teeth32.add("18");
            if (t17.isChecked()) teeth32.add("17");
            if (t16.isChecked()) teeth32.add("16");
            if (t15.isChecked()) teeth32.add("15");
            if (t14.isChecked()) teeth32.add("14");
            if (t13.isChecked()) teeth32.add("13");
            if (t12.isChecked()) teeth32.add("12");
            if (t11.isChecked()) teeth32.add("11");
            if (t21.isChecked()) teeth32.add("21");
            if (t22.isChecked()) teeth32.add("22");
            if (t23.isChecked()) teeth32.add("23");
            if (t24.isChecked()) teeth32.add("24");
            if (t25.isChecked()) teeth32.add("25");
            if (t26.isChecked()) teeth32.add("26");
            if (t27.isChecked()) teeth32.add("27");
            if (t28.isChecked()) teeth32.add("28");
            if (t48.isChecked()) teeth32.add("48");
            if (t47.isChecked()) teeth32.add("47");
            if (t46.isChecked()) teeth32.add("46");
            if (t45.isChecked()) teeth32.add("45");
            if (t44.isChecked()) teeth32.add("44");
            if (t43.isChecked()) teeth32.add("43");
            if (t42.isChecked()) teeth32.add("42");
            if (t41.isChecked()) teeth32.add("41");
            if (t31.isChecked()) teeth32.add("31");
            if (t32.isChecked()) teeth32.add("32");
            if (t33.isChecked()) teeth32.add("33");
            if (t34.isChecked()) teeth32.add("34");
            if (t35.isChecked()) teeth32.add("35");
            if (t36.isChecked()) teeth32.add("36");
            if (t37.isChecked()) teeth32.add("37");
            if (t38.isChecked()) teeth32.add("38");

            progressBar.setVisibility(View.VISIBLE);
            savedata();
        });

        return view;
    }

    private void savedata() {
//        Map<String, Object> data = new HashMap<>();
//        data.put("treatments", treatments);
//        data.put("teeth32", teeth32);
        String patientId = sharedViewModel.getPatientId();
        DocumentReference patientRef = firestore.collection("patients").document(patientId);

        ArrayList<String> prescription = new ArrayList<String>();
        patientRef.update("prescription", prescription, "treatment", treatments,"Teeth",teeth32)                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        navigateToNextFragment();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToNextFragment() {
        if (getActivity() instanceof NavigationListener) {
            ((NavigationListener) getActivity()).navigateToNextFragment();
        } else {
            Toast.makeText(requireContext(), "NavigationListener not implemented in activity", Toast.LENGTH_SHORT).show();
        }
    }
}
