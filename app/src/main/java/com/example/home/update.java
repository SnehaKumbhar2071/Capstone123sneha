package com.example.home;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class update extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_GALLERY2 = 2;
    private EditText fullNameEditText, dateEditText, addressEditText,editFollowUpDate, phoneNumberEditText, dobEditText, genderEditText, statusEditText, treatmentEditText,teethedittext;
    private Button updateButton;
    private ImageButton addbtn, deletebtn, addbtn2, deletebtn2,addFollowUpDateButton;
    private RecyclerView recyclerView1,recyclerView2;

    private String key;
    private ImageAdapter adapter,adapter2;

    private boolean isDateField;

    private FirebaseFirestore firestore;
    private static final int READ_PERMISSION = 101;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        fullNameEditText = findViewById(R.id.editFullName);
        dateEditText = findViewById(R.id.editDate);
        addressEditText = findViewById(R.id.editAddress);
        phoneNumberEditText = findViewById(R.id.editPhoneNumber);
        dobEditText = findViewById(R.id.editDOB);
        genderEditText = findViewById(R.id.editGender);
        statusEditText = findViewById(R.id.editStatus);
        treatmentEditText = findViewById(R.id.editTreatment);
        updateButton = findViewById(R.id.updateButton);
        addbtn = findViewById(R.id.addbtn);
        deletebtn = findViewById(R.id.deletebtn);
        addbtn2 = findViewById(R.id.addbtn2);
        deletebtn2 = findViewById(R.id.deletebtn2);
        teethedittext=findViewById(R.id.teethedittext);
        recyclerView2=findViewById(R.id.recyclerView2);
        editFollowUpDate = findViewById(R.id.editFollowUpDate);
        addFollowUpDateButton = findViewById(R.id.addFollowUpDateBtn);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");
            fullNameEditText.setText(bundle.getString("fullName"));
            dateEditText.setText(bundle.getString("date"));
            addressEditText.setText(bundle.getString("address"));
            phoneNumberEditText.setText(bundle.getString("phoneNumber"));
            dobEditText.setText(bundle.getString("dob"));
            genderEditText.setText(bundle.getString("gender"));
            statusEditText.setText(bundle.getString("status"));
            treatmentEditText.setText(bundle.getString("treatments"));
            teethedittext.setText(bundle.getString("Teeth"));


            recyclerView1 = findViewById(R.id.recyclerView1);
            recyclerView1.setLayoutManager(new GridLayoutManager(this, 2)); // Use GridLayoutManager for multiple images
            recyclerView2 = findViewById(R.id.recyclerView2);
            recyclerView2.setLayoutManager(new GridLayoutManager(this, 2));
            // Fetch and display follow-up dates
            ArrayList<String> followUpDates = bundle.getStringArrayList("followUpDates");
            if (followUpDates != null && !followUpDates.isEmpty()) {
                StringBuilder followUpDatesBuilder = new StringBuilder();
                for (String followUpDate : followUpDates) {
                    followUpDatesBuilder.append(followUpDate).append(", ");
                }
                String followUpDatesText = followUpDatesBuilder.substring(0, followUpDatesBuilder.length() - 2);
                editFollowUpDate.setText(followUpDatesText);
            }// Use GridLayoutManager for multiple images

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("patients").document(key);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> prepostimages = (ArrayList<String>) documentSnapshot.get("prepostimages");
                        if (prepostimages != null) {
                            // Sort the list of image URLs based on their last modified date (newest first)
                            sortImagesByLastModified(prepostimages, new detail.OnImagesSortedListener() {
                                @Override
                                public void onImagesSorted(ArrayList<String> sortedImages) {
                                    // Initialize and set up the adapter for prepostimages
                                    adapter = new ImageAdapter(sortedImages, update.this);
                                    recyclerView1.setAdapter(adapter);
                                }
                            });
                        }

// Fetch and display prescription images
                        ArrayList<String> prescription = (ArrayList<String>) documentSnapshot.get("prescription");
                        if (prescription != null) {
                            // Sort the list of image URLs based on their last modified date (newest first)
                            sortImagesByLastModified(prescription, new detail.OnImagesSortedListener() {
                                @Override
                                public void onImagesSorted(ArrayList<String> sortedImages) {
                                    // Initialize and set up the adapter for prescription
                                    adapter2 = new ImageAdapter(sortedImages, update.this);
                                    recyclerView2.setAdapter(adapter2);
                                }
                            });
                        }

                    }
                }
            });
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatient();

            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateField = true;
                showDatePickerDialog();
            }
        });



        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateField = false;
                showDatePickerDialog();
            }
        });

        addbtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_GALLERY);
        });

        addbtn2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_GALLERY2); // Change the request code
        });


        deletebtn.setOnClickListener(v -> {
            deleteImages(adapter);
        });
        addFollowUpDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogForFollowUp(); // Function to show DatePickerDialog for follow-up dates
            }
        });

        deletebtn2.setOnClickListener(v -> {
            deleteImages(adapter2);
        });}
    private void deleteImages(ImageAdapter adapter) {
        if (adapter != null) {
            ArrayList<String> selectedImages = adapter.getSelectedImages();
            ArrayList<String> imageUrls = adapter.getImageUrls();

            // Remove selected images from the list
            for (String image : selectedImages) {
                imageUrls.remove(image);
            }

            // Update RecyclerView with the new list of images
            adapter.setImageUrls(imageUrls);
            adapter.notifyDataSetChanged();

            // Disable selection mode after deletion
            toggleSelectionMode(adapter);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                handleGalleryImage(data, adapter); // Pass adapter for recyclerView1
            } else if (requestCode == REQUEST_GALLERY2) { // Handle result for addbtn2
                handleGalleryImage(data, adapter2); // Pass adapter for recyclerView2
            }
        }
    }



    private void handleGalleryImage(Intent data, ImageAdapter adapter) {
        if (data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                ArrayList<String> selectedImages = new ArrayList<>();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    // Upload the image to Firebase Storage
                    uploadImageToStorage(uri, adapter);
                }
            } else {
                Uri uri = data.getData();
                if (uri != null) {
                    // Upload the image to Firebase Storage
                    uploadImageToStorage(uri, adapter);
                }
            }
        }
    }

    private void uploadImageToStorage(Uri imageUri, ImageAdapter adapter) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images").child("image_" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // URL generated successfully, add it to the adapter
                        String downloadUrl = uri.toString();
                        adapter.addImageUrl(downloadUrl);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(update.this, "Successfully uploaded: ", Toast.LENGTH_SHORT).show();

                    }).addOnFailureListener(e -> {
                        // Handle failure to retrieve download URL
                        Toast.makeText(update.this, "Failed to generate download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }




    private void toggleSelectionMode(ImageAdapter adapter) {
        if (adapter != null) {
            adapter.setSelectMode(!adapter.isSelectionMode());

            // Show or hide the delete button based on selection mode
            if (adapter.isSelectionMode()) {

            } else {
                adapter.clearSelection(); // Clear selection when exiting selection mode
            }
        }
    }

    private void updatePatient() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference patientRef = db.collection("patients").document(key);

        // Retrieve existing patient data and follow-up dates
        patientRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> patientData = documentSnapshot.getData();

                // Update patient data fields
                patientData.put("fullName", fullNameEditText.getText().toString());
                patientData.put("address", addressEditText.getText().toString());
                patientData.put("phoneNumber", phoneNumberEditText.getText().toString());
                patientData.put("gender", genderEditText.getText().toString());
                patientData.put("status", statusEditText.getText().toString());
                patientData.put("treatments", treatmentEditText.getText().toString());
                patientData.put("Teeth", teethedittext.getText().toString());

                // Update prepostimages if adapter is not null
                if (adapter != null && adapter.getImageUrls() != null) {
                    patientData.put("prepostimages", adapter.getImageUrls());
                }

                // Update prescription if adapter2 is not null
                if (adapter2 != null && adapter2.getImageUrls() != null) {
                    patientData.put("prescription", adapter2.getImageUrls());
                }



                // Add new follow-up dates
                ArrayList<String> newFollowUpDatesList = new ArrayList<>();
                String followUpDateText = editFollowUpDate.getText().toString().trim();
                if (!followUpDateText.isEmpty()) {
                    String[] newFollowUpDatesArray = followUpDateText.split(",");
                    newFollowUpDatesList.addAll(Arrays.asList(newFollowUpDatesArray));
                }
                Collections.reverse(newFollowUpDatesList);

                // Update follow-up dates
                patientData.put("followUpDates", newFollowUpDatesList);

                // Update treatment and teeth lists
                patientData.put("treatment", parseStringToList(treatmentEditText.getText().toString()));
                patientData.put("Teeth", parseStringToList(teethedittext.getText().toString()));

                // Update the Firestore document
                patientRef.set(patientData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(update.this, "Patient data updated successfully", Toast.LENGTH_SHORT).show();
                            // After updating patient data, upload images to Firebase Storage
                            uploadImagesToStorage();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(update.this, "Failed to update patient data", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(update.this, "Document does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(update.this, "Failed to retrieve document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private ArrayList<String> parseStringToList(String text) {
        if (!text.isEmpty()) {
            String[] array = text.split("[{},]");
            return new ArrayList<>(Arrays.asList(array));
        } else {
            return new ArrayList<>();
        }
    }

    private void uploadImagesToStorage() {
        // Upload prepostimages
        if (adapter != null) {
            ArrayList<String> prepostImages = adapter.getImageUrls();
            uploadImages("prepostimages", prepostImages);
        }

        // Upload prescription
        if (adapter2 != null) {
            ArrayList<String> prescriptionUrls = adapter2.getImageUrls();
            uploadImages("prescription", prescriptionUrls);

        }
        if (getIntent().getBooleanExtra("finish", false)) {
            finish();
        }
        Intent intent = new Intent(update.this, detail.class);
        intent.putExtra("Key", key);
        startActivity(intent);
        finish();
    }
    private void uploadImages(String field, ArrayList<String> imageUrls) {
        // Get a reference to the Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Iterate through image URLs and upload them to Firebase Storage
        for (String imageUrl : imageUrls) {
            // Create a unique filename for each image
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";

            // Create a reference to the image file
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + fileName);

            // Convert the image URL to Uri
            Uri imageUri = Uri.parse(imageUrl);

            // Upload the image file to Firebase Storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, retrieve the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // URL generated successfully, store it in Firestore
                            String downloadUrl = uri.toString();
                            // Store the download URL in Firestore
                            storeImageUrlOrPrescriptionUrl(field, downloadUrl);

                        }).addOnFailureListener(e -> {
                            // Handle failure to retrieve download URL
                            Toast.makeText(update.this, "Failed to generate download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    });
        }

    }

    private void storeImageUrlOrPrescriptionUrl(String field, String url) {
        // Update the specified field with the download URL
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference patientRef = db.collection("patients").document(key);
        patientRef.update(field, FieldValue.arrayUnion(url))
                .addOnSuccessListener(aVoid -> {
                    // URL stored successfully
                    Toast.makeText(update.this, "URL stored successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to store URL
                    Toast.makeText(update.this, "Failed to store URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        finish();
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.show();
    }

    private void showDatePickerDialogForFollowUp() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                String currentText = editFollowUpDate.getText().toString();
                if (!currentText.isEmpty()) {
                    currentText += ", ";
                }
                currentText += selectedDate;
                editFollowUpDate.setText(currentText);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);

        if (isDateField) {
            String currentText = dateEditText.getText().toString();
            if (!currentText.isEmpty()) {
                currentText += ", ";
            }
            currentText += selectedDate;
            dateEditText.setText(currentText);
        } else {
            // For follow-up dates
            String currentText = editFollowUpDate.getText().toString();
            if (!currentText.isEmpty()) {
                currentText += ", ";
            }
            currentText += selectedDate;
            editFollowUpDate.setText(currentText);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the detail activity when navigating back
    }

    private void sortImagesByLastModified(final ArrayList<String> imageUrls, final detail.OnImagesSortedListener listener) {
        final int totalImages = imageUrls.size();
        final ArrayList<update.ImagMetadata> imageMetadataList = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(0);
        ArrayList<String> sortedImageUrls = new ArrayList<>();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (final String imageUrl : imageUrls) {
            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
            imageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    Date uploadDate = new Date(storageMetadata.getUpdatedTimeMillis());
                    imageMetadataList.add(new update.ImagMetadata(imageUrl, uploadDate));
                    if (counter.incrementAndGet() == totalImages) {
                        // All metadata retrieved, sort the image URLs based on their metadata
                        Collections.sort(imageMetadataList);
                        for (update.ImagMetadata metadata : imageMetadataList) {
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
                        for (update.ImagMetadata metadata : imageMetadataList) {
                            sortedImageUrls.add(metadata.getImageUrl());
                        }
                        listener.onImagesSorted(sortedImageUrls);
                    }
                }
            });
        }
    }



    // Class to store image metadata along with the image URL
    private static class ImagMetadata implements Comparable<update.ImagMetadata> {
        private String imageUrl;
        private Date uploadDate;

        public ImagMetadata(String imageUrl, Date uploadDate) {
            this.imageUrl = imageUrl;
            this.uploadDate = uploadDate;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        @Override
        public int compareTo(update.ImagMetadata other) {
            // Sort in descending order (newest first)
            return other.uploadDate.compareTo(this.uploadDate);
        }

        // Interface for the listener to handle sorted images

    }}