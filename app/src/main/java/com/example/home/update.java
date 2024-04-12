package com.example.home;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class update extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_GALLERY2 = 2;
    private EditText fullNameEditText, dateEditText, addressEditText, phoneNumberEditText, dobEditText, genderEditText, statusEditText, treatmentEditText;
    private Button updateButton;
    private ImageButton addbtn, deletebtn, addbtn2, deletebtn2;
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
        recyclerView2=findViewById(R.id.recyclerView2);

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

            recyclerView1 = findViewById(R.id.recyclerView1);
            recyclerView1.setLayoutManager(new GridLayoutManager(this, 2)); // Use GridLayoutManager for multiple images
            recyclerView2 = findViewById(R.id.recyclerView2);
            recyclerView2.setLayoutManager(new GridLayoutManager(this, 2)); // Use GridLayoutManager for multiple images

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("patients").document(key);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> prepostimages = (ArrayList<String>) documentSnapshot.get("prepostimages");
                        if (prepostimages != null) {
                            // Initialize and set up the adapter
                            adapter = new ImageAdapter(prepostimages, update.this);
                            recyclerView1.setAdapter(adapter);
                        }
                        ArrayList<String> prescription = (ArrayList<String>) documentSnapshot.get("prescription");
                        if (prescription != null) {
                            // Initialize and set up the adapter
                            adapter2 = new ImageAdapter(prescription, update.this);
                            recyclerView2.setAdapter(adapter2);
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

        // Delete existing data associated with the patient key
        patientRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Once deletion is successful, add the new patient data
                    Map<String, Object> patientData = new HashMap<>();
                    patientData.put("fullName", fullNameEditText.getText().toString());
                    patientData.put("date", dateEditText.getText().toString());
                    patientData.put("address", addressEditText.getText().toString());
                    patientData.put("phoneNumber", phoneNumberEditText.getText().toString());
                    patientData.put("dob", dobEditText.getText().toString());
                    patientData.put("gender", genderEditText.getText().toString());
                    patientData.put("status", statusEditText.getText().toString());
                    patientData.put("treatments", treatmentEditText.getText().toString());
                    ArrayList<String> prepostImages = (adapter != null && adapter.getImageUrls() != null) ? adapter.getImageUrls() : new ArrayList<>();

// Check if adapter2 is not null and get prescription
                    ArrayList<String> prescriptionUrls = (adapter2 != null && adapter2.getImageUrls() != null) ? adapter2.getImageUrls() : new ArrayList<>();

// Add prepostimages to patientData
                    patientData.put("prepostimages", prepostImages);

// Add prescription to patientData
                    patientData.put("prescription", prescriptionUrls);

                    String treatmentText = treatmentEditText.getText().toString().trim();
                    if (!treatmentText.isEmpty()) {
                        String[] treatmentsArray = treatmentText.split("[{},]");                        ArrayList<String> treatmentsList = new ArrayList<>(Arrays.asList(treatmentsArray));
                        patientData.put("treatment", treatmentsList);
                    } else {
                        patientData.put("treatment", new ArrayList<String>()); // Empty array if no treatments entered
                    }

                    // Add the new patient data to Firestore
                    db.collection("patients").document(key).set(patientData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(update.this, "Patient data updated successfully", Toast.LENGTH_SHORT).show();
                                // After updating patient data, upload images to Firebase Storage
                                uploadImagesToStorage();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(update.this, "Failed to update patient data", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(update.this, "Failed to delete existing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
        finish();
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

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);

        if (isDateField) {
            dateEditText.setText(selectedDate);
        } else {
            dobEditText.setText(selectedDate);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the detail activity when navigating back
    }
}
