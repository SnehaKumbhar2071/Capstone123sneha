package com.example.home;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class detail extends AppCompatActivity {

    private TextView date, fullName, address, phoneNumber, dob, gender, treatmentss, statuss, teethss, followUpDatesTextView;
    private FloatingActionButton deleteButton, editButton;
    private RecyclerView recyclerView1, recyclerView2;
    private ArrayList<String> followUpDatesList = new ArrayList<>(); // New list to store follow-up dates



    String key = "";
    private ImageAdapter adapter1;
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
        recyclerView2 = findViewById(R.id.recycler_gallary2);
        treatmentss = findViewById(R.id.treatmentss);
        statuss = findViewById(R.id.statuss);
        teethss = findViewById(R.id.teethss);
        followUpDatesTextView = findViewById(R.id.followUpDatesTextView); // New TextView for displaying follow-up dates


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
                    StringBuilder treat = new StringBuilder();

                    List<String> tres = (List<String>) documentSnapshot.get("treatment");
                    if (tres != null) {
                        for (int i = 0; i < tres.size(); i++) {
                            treat.append(tres.get(i));
                            if (i < tres.size() - 1) {
                                treat.append(", "); // Add comma and space if not the last item
                            }
                        }
                    }
                    StringBuilder teeth2 = new StringBuilder();
                    List<String> teeth32 = (List<String>) documentSnapshot.get("Teeth");
                    if (teeth32 != null) {
                        for (int i = 0; i < teeth32.size(); i++) {
                            teeth2.append(teeth32.get(i));
                            if (i < teeth32.size() - 1) {
                                teeth2.append(","); // Add comma and space if not the last item
                            }
                        }
                    }
                    List<String> followUpDates = (List<String>) documentSnapshot.get("followUpDates");
                    if (followUpDates != null) {
                        followUpDatesList.addAll(followUpDates);
                        // Display follow-up dates in the TextView
                        StringBuilder followUpDatesBuilder = new StringBuilder();
                        for (String followUpDate : followUpDatesList) {
                            followUpDatesBuilder.append(followUpDate).append("\n");
                        }
                        followUpDatesTextView.setText(followUpDatesBuilder.toString());
                        followUpDatesTextView.setVisibility(View.VISIBLE); // Set visibility to visible
                    }

                    fullName.setText(name);
                    address.setText(add);
                    phoneNumber.setText(phnum);
                    dob.setText(dateob);
                    date.setText(appointdate);
                    gender.setText(gen);
                    statuss.setText(status);
                    treatmentss.setText(treat.toString());
                    teethss.setText(teeth2.toString());


                    // Get the list of image URLs for prepostimages
                    // Fetch and display prepostimages
                    ArrayList<String> prepostimages = (ArrayList<String>) documentSnapshot.get("prepostimages");
                    if (prepostimages != null) {
                        // Sort the list of image URLs based on their last modified date (newest first)
                        sortImagesByLastModified(prepostimages, new OnImagesSortedListener() {
                            @Override
                            public void onImagesSorted(ArrayList<String> sortedImages) {
                                // Initialize and set up the adapter for prepostimages
                                adapter1 = new ImageAdapter(sortedImages, detail.this);
                                recyclerView1.setAdapter(adapter1);
                            }
                        });
                    }

// Fetch and display prescription images
                    ArrayList<String> prescription = (ArrayList<String>) documentSnapshot.get("prescription");
                    if (prescription != null) {
                        // Sort the list of image URLs based on their last modified date (newest first)
                        sortImagesByLastModified(prescription, new OnImagesSortedListener() {
                            @Override
                            public void onImagesSorted(ArrayList<String> sortedImages) {
                                // Initialize and set up the adapter for prescription
                                adapter2 = new ImageAdapter(sortedImages, detail.this);
                                recyclerView2.setAdapter(adapter2);
                            }
                        });
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
                        Intent i = new Intent(detail.this, card1.class);
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
                        .putExtra("phoneNumber", phoneNumber.getText().toString())
                        .putExtra("dob", dob.getText().toString())
                        .putExtra("gender", gender.getText().toString())
                        .putExtra("status", statuss.getText().toString())
                        .putExtra("treatments", treatmentss.getText().toString())
                        .putExtra("Teeth", teethss.getText().toString())
                        .putExtra("Key", key)
                        .putExtra("followUpDates", followUpDatesList);
                startActivity(intent);

            }
        });
    }

    // Method to sort images by their last modified date (newest first)
    // Method to sort images by their last modified date (newest first)
    private void sortImagesByLastModified(final ArrayList<String> imageUrls, final OnImagesSortedListener listener) {
        final int totalImages = imageUrls.size();
        final ArrayList<ImageMetadata> imageMetadataList = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(0);
        ArrayList<String> sortedImageUrls = new ArrayList<>();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (final String imageUrl : imageUrls) {
            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
            imageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    Date uploadDate = new Date(storageMetadata.getUpdatedTimeMillis());
                    imageMetadataList.add(new ImageMetadata(imageUrl, uploadDate));
                    if (counter.incrementAndGet() == totalImages) {
                        // All metadata retrieved, sort the image URLs based on their metadata
                        Collections.sort(imageMetadataList);
                        for (ImageMetadata metadata : imageMetadataList) {
                            sortedImageUrls.add(metadata.getImageUrl());
                        }
                        Log.d("Sort", "Sorted image URLs: " + sortedImageUrls.size());
                        // Notify listener with sorted image URLs
                        listener.onImagesSorted(sortedImageUrls);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure
                    Log.e("Sort", "Failed to retrieve metadata for image: " + imageUrl, e);
                    counter.incrementAndGet(); // Increment counter even in case of failure to prevent deadlock
                    if (counter.get() == totalImages) {
                        // If all images have been processed (even in case of failure), notify listener
                        Collections.sort(imageMetadataList);
                        for (ImageMetadata metadata : imageMetadataList) {
                            sortedImageUrls.add(metadata.getImageUrl());
                        }
                        listener.onImagesSorted(sortedImageUrls);
                    }
                }
            });
        }
    }

    // Class to store image metadata along with the image URL
    private static class ImageMetadata implements Comparable<ImageMetadata> {
        private String imageUrl;
        private Date uploadDate;

        public ImageMetadata(String imageUrl, Date uploadDate) {
            this.imageUrl = imageUrl;
            this.uploadDate = uploadDate;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        @Override
        public int compareTo(ImageMetadata other) {
            // Sort in descending order (newest first)
            return other.uploadDate.compareTo(this.uploadDate);
        }
    }

    // Interface for the listener to handle sorted images
    interface OnImagesSortedListener {
        void onImagesSorted(ArrayList<String> sortedImages);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the detail activity when navigating back
    }
}