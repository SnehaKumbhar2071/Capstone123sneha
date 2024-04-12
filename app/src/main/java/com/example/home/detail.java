package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class detail extends AppCompatActivity {

    private TextView date, fullName, address, phoneNumber, dob, gender,treatmentss,statuss;
    private FloatingActionButton deleteButton, editButton;
    private RecyclerView recyclerView1,recyclerView2;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<String> imageUrls2 = new ArrayList<>();


    String key = "";
    private ImageAdapter adapter;
    private ImageAdapter adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        date = findViewById(R.id.appointmentdate);
        fullName = findViewById(R.id.fullName);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phonenum);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        recyclerView1 = findViewById(R.id.recyclerView1);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        recyclerView2=findViewById(R.id.recycler_gallary2);
        treatmentss = findViewById(R.id.treatmentss);
        statuss = findViewById(R.id.statuss);


        recyclerView1.setLayoutManager(new GridLayoutManager(this, 2)); // Use GridLayoutManager for multiple images
        recyclerView2.setLayoutManager(new GridLayoutManager(this, 2)); // Use GridLayoutManager for multiple images

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");
        }

        // Fetch data from Firestore using the key
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("patients").document(key);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("fullName");
                    String add = documentSnapshot.getString("address");
                    String phnum = documentSnapshot.getString("phoneNumber");
                    String dateob = documentSnapshot.getString("dob");
                    String gen = documentSnapshot.getString("gender");
                    String appointdate = documentSnapshot.getString("date");
                    String status = documentSnapshot.getString("status");
                    StringBuilder treat= new StringBuilder();
                    List<String> tres = (List<String>) documentSnapshot.get("treatment");
                    if (tres != null) {
                        for (int i = 0; i < tres.size(); i++) {
                            treat.append(tres.get(i));
                            if (i < tres.size() - 1) {
                                treat.append(", "); // Add comma and space if not the last item
                            }
                        }
                    }


                    fullName.setText(name);
                    address.setText(add);
                    phoneNumber.setText(phnum);
                    dob.setText(dateob);
                    date.setText(appointdate);
                    gender.setText(gen);
                    statuss.setText(status);
                    treatmentss.setText(treat.toString());


                    // Get the list of image URLs
                    ArrayList<String> prepostimages = (ArrayList<String>) documentSnapshot.get("prepostimages");
                    if (prepostimages != null) {
                        imageUrls.addAll(prepostimages);
                        // Initialize and set up the adapter
                        adapter = new ImageAdapter(imageUrls, detail.this);
                        recyclerView1.setAdapter(adapter);
                    }
                    ArrayList<String> prescription = (ArrayList<String>) documentSnapshot.get("prescription");
                    if (prescription != null) {
                        imageUrls2.addAll(prescription);
                        // Initialize and set up the adapter
                        adapter2 = new ImageAdapter(imageUrls2, detail.this);
                        recyclerView2.setAdapter(adapter2);
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete data from Firestore and storage
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("patients").document(key);
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Toast.makeText(detail.this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(detail.this,MainActivity.class);
                        startActivity(i);
                        finish(); // Close detail activity after deletion
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Toast.makeText(detail.this, "Failed to delete patient", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(detail.this, update.class)
                        .putExtra("fullName", fullName.getText().toString())
                        .putExtra("date", date.getText().toString())
                        .putExtra("address", address.getText().toString())
                        .putExtra("phoneNumber",phoneNumber.getText().toString())
                        .putExtra("dob",dob.getText().toString())
                        .putExtra("gender",gender.getText().toString())
                        .putExtra("status",statuss.getText().toString())
                        .putExtra("treatments",treatmentss.getText().toString())
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }
}