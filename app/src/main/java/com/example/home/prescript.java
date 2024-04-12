package com.example.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class prescript extends Fragment implements RecyclerAdapter.OnDeleteClickListener, RecyclerAdapter.OnImageClickListener {
    private SharedViewModel sharedViewModel;

    private RecyclerView recyclerView;
    private static int totalImagess = 0;
    private int uploadedImagess = 0;
    private ProgressBar progressBar;
    private ArrayList<String> treatments = new ArrayList<>();
    private TextView textView;
    private Button pick, capture, delete, select, nextfrag2;
    private boolean isSelectionMode = false;
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    RecyclerAdapter adapter;
    private FirebaseFirestore firestore;
    private static final int READ_PERMISSION = 101;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private CheckBox c1, c2, c3, c4;

    public prescript() {
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
        View view = inflater.inflate(R.layout.activity_prescript, container, false);
        textView = view.findViewById(R.id.totalphotos);
        recyclerView = view.findViewById(R.id.recycler_gallary);
        pick = view.findViewById(R.id.pick);
        capture = view.findViewById(R.id.capture);
        delete = view.findViewById(R.id.delete);
        select = view.findViewById(R.id.select);
        nextfrag2 = view.findViewById(R.id.nextfrag3);
        c1 = view.findViewById(R.id.checkbox_brace);
        c2 = view.findViewById(R.id.checkbox_bridge);
        c3 = view.findViewById(R.id.checkbox_root);
        progressBar = view.findViewById(R.id.progressBar2);
        c4 = view.findViewById(R.id.checkbox_teeth);

        adapter = new RecyclerAdapter(imageItems, this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }

        pick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_GALLERY);
        });

        capture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    } else {
                        Toast.makeText(requireContext(), "No camera app available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "No camera feature available on this device", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        });

        delete.setOnClickListener(v -> {
            if (isSelectionMode) {
                for (ImageItem item : adapter.getSelectedItems()) {
                    imageItems.remove(item);
                }
                adapter.clearSelection();
                toggleSelectionMode();
            }
        });

        select.setOnClickListener(v -> toggleSelectionMode());

        nextfrag2.setOnClickListener(v -> {
            if (c1.isChecked()) {
                treatments.add("Brace");
            }
            if (c2.isChecked()) {
                treatments.add("Bridge");
            }
            if (c3.isChecked()) {
                treatments.add("Root");
            }
            if (c4.isChecked()) {
                treatments.add("Tooth");
            }
            progressBar.setVisibility(View.VISIBLE);
            saveImagesToDatabase();
        });

        return view;
    }

    private void toggleSelectionMode() {
        isSelectionMode = !isSelectionMode;
        adapter.setSelectMode(isSelectionMode);
        if (isSelectionMode) {
            delete.setVisibility(View.VISIBLE);
            select.setText("Cancel");
        } else {
            delete.setVisibility(View.GONE);
            select.setText("Select");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        Uri imageUri = getImageUri(requireContext(), imageBitmap);
                        if (imageUri != null) {
                            String date = getCurrentDate();
                            imageItems.add(0, new ImageItem(imageUri, date));
                            adapter.notifyDataSetChanged();
                            textView.setText("Photos (" + imageItems.size() + ")");
                        } else {
                            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }  else if (requestCode == REQUEST_GALLERY && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        String date = getCurrentDate();
                        imageItems.add(new ImageItem(imageUri, date)); // Add at the end of the list
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    String date = getCurrentDate();
                    imageItems.add(new ImageItem(imageUri, date)); // Add at the end of the list
                }
                adapter.notifyDataSetChanged();
                textView.setText("Photos (" + imageItems.size() + ")");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            // Permission denied
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = null;
        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            OutputStream outputStream = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void saveImagesToDatabase() {
        String patientId = sharedViewModel.getPatientId();

        if (patientId != null) {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar
            totalImagess=imageItems.size();
            // Upload each image to Firebase Storage and store their download URLs in Firestore
            for (ImageItem item : imageItems) {
                Uri imageUri = item.getImageUri();
                String imageName = "image_" + System.currentTimeMillis() + ".jpg"; // Generate a unique name for the image

                // Upload image to Firebase Storage
                uploadImageToFirebaseStorage(imageUri, imageName);
            }
        } else {
            Toast.makeText(requireContext(), "Patient ID is null", Toast.LENGTH_SHORT).show();
        }


    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String imageName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageName);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully

                    // Now get the download URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Get the download URL of the uploaded image
                        String imageUrl = uri.toString();
                        // Once you have the download URL, store it in Firestore
                        uploadedImagess++;
                        storeImageUrlInFirestore(imageUrl);
                    }).addOnFailureListener(e -> {
                        // Handle failure to get download URL
                        Toast.makeText(requireContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful upload
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                });
    }

    private void storeImageUrlInFirestore(String imageUrl) {
        String patientId = sharedViewModel.getPatientId();
        if (patientId != null) {
            // Update Firestore document with the image URL
            DocumentReference patientRef = firestore.collection("patients").document(patientId);

            // Increment total images count

            // Get the current list of image URLs from Firestore
            patientRef.get().addOnSuccessListener(documentSnapshot -> {
                ArrayList<String> prescription = (ArrayList<String>) documentSnapshot.get("prescription");
                if (prescription == null) {
                    prescription = new ArrayList<>();
                }

                // Check if the image URL already exists
                if (!prescription.contains(imageUrl)) {
                    // If not, add it to the list
                    prescription.add(imageUrl);

                    // Update the Firestore document with the new list of image URLs
                    patientRef.update("prescription", prescription, "treatment", treatments)
                            .addOnSuccessListener(aVoid -> {
                                // Image URL stored successfully
                                uploadedImagess++; // Increment uploaded images count

                                // Check if all images have been uploaded
                                if (uploadedImagess == totalImagess) {
                                    // If all images uploaded successfully, navigate to the next fragment
                                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                                    navigateToNextFragment();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(requireContext(), "Failed to store image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

            }).addOnFailureListener(e -> {
                // Handle failure to retrieve document
                Toast.makeText(requireContext(), "Failed to retrieve document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(requireContext(), "Patient ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        if (!isSelectionMode) {
            imageItems.remove(position);
            adapter.notifyDataSetChanged();
            textView.setText("Photos (" + imageItems.size() + ")");
        } else {
            adapter.toggleSelection(position);
        }
    }
    private void navigateToNextFragment() {
        if (getActivity() instanceof NavigationListener) {
            ((NavigationListener) getActivity()).navigateToNextFragment();
        } else {
            Toast.makeText(requireContext(), "NavigationListener not implemented in activity", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onImageClick(Uri imageUri) {
        Intent intent = new Intent(requireContext(), ImageZoomActivity.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }
}
