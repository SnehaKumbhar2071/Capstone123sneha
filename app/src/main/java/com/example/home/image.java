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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class image extends Fragment implements RecyclerAdapter.OnDeleteClickListener, RecyclerAdapter.OnImageClickListener {
    private SharedViewModel sharedViewModel;
    private ActionMode actionmode;
    private RecyclerView recyclerView;
    private TextView textView;
    private Button pick, capture, delete, select, nextfrag2;
    private boolean isSelectionMode = false;
    private ArrayList<ImageItem> imageItem = new ArrayList<>();
    RecyclerAdapter adapter;
    private FirebaseFirestore firestore;
    private static final int READ_PERMISSION = 101;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private ProgressBar progressBar;
    private int totalImages = 0;
    private int uploadedImages = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    public image() {
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
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        textView = view.findViewById(R.id.totalphotos);
        recyclerView = view.findViewById(R.id.recycler_gallary);
        pick = view.findViewById(R.id.pick);
        capture = view.findViewById(R.id.capture);
        delete = view.findViewById(R.id.delete);
        select = view.findViewById(R.id.select);
        nextfrag2 = view.findViewById(R.id.nextfrag2);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new RecyclerAdapter(imageItem, this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            // Permission already granted, perform necessary actions
            // For example, continue with capturing images or saving files
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
                    imageItem.remove(item);
                }
                adapter.clearSelection();
                toggleSelectionMode();
            }
        });

        select.setOnClickListener(v -> toggleSelectionMode());

        nextfrag2.setOnClickListener(v -> {
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
                            imageItem.add(0, new ImageItem(imageUri, date));
                            adapter.notifyDataSetChanged();
                            textView.setText("Photos (" + imageItem.size() + ")");
                        } else {
                            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        String date = getCurrentDate();
                        imageItem.add(new ImageItem(imageUri, date)); // Add at the end of the list
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    String date = getCurrentDate();
                    imageItem.add(new ImageItem(imageUri, date)); // Add at the end of the list
                }
                adapter.notifyDataSetChanged();
                textView.setText("Photos (" + imageItem.size() + ")");
            }

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform necessary actions
                // For example, continue with capturing images or saving files
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
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
            // Reset counters
            totalImages = imageItem.size();
            uploadedImages = 0;

            // Upload each image to Firebase Storage and store their download URLs in Firestore
            for (int i = 0; i < imageItem.size(); i++) {
                ImageItem item = imageItem.get(i);
                Uri imageUri = item.getImageUri();
                String imageName = "image_" + System.currentTimeMillis() + "_" + i + ".jpg"; // Generate a unique name for the image

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
                        storeImageUrlInFirestore(imageUrl);
                    }).addOnFailureListener(e -> {
                        // Handle failure to get download URL
                        Toast.makeText(requireContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        updateUploadCounter(); // Increment uploaded images count and check if all images have been uploaded
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful upload
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    updateUploadCounter(); // Increment uploaded images count and check if all images have been uploaded
                });
    }


    private void storeImageUrlInFirestore(String imageUrl) {
        String patientId = sharedViewModel.getPatientId();
        if (patientId != null) {
            // Update Firestore document with the image URL
            DocumentReference patientRef = firestore.collection("patients").document(patientId);

            // Get the current list of image URLs from Firestore
            patientRef.get().addOnSuccessListener(documentSnapshot -> {
                ArrayList<String> prepostimages = (ArrayList<String>) documentSnapshot.get("prepostimages");
                if (prepostimages == null) {
                    prepostimages = new ArrayList<>();
                }

                // Check if the image URL already exists
                if (!prepostimages.contains(imageUrl)) {
                    // If not, add it to the list
                    prepostimages.add(imageUrl);
                }

                // Update the Firestore document with the new list of image URLs
                patientRef.update("prepostimages", prepostimages)
                        .addOnSuccessListener(aVoid -> {
                            // Image URL stored successfully
                            updateUploadCounter(); // Increment uploaded images count and check if all images have been uploaded
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(requireContext(), "Failed to store image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            updateUploadCounter(); // Increment uploaded images count and check if all images have been uploaded
                        });
            }).addOnFailureListener(e -> {
                // Handle failure to retrieve document
                Toast.makeText(requireContext(), "Failed to retrieve document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                updateUploadCounter(); // Increment uploaded images count and check if all images have been uploaded
            });
        } else {
            Toast.makeText(requireContext(), "Patient ID is null", Toast.LENGTH_SHORT).show();
//            updateUploadCounter(); // Increment uploaded images count and check if all images have been uploaded
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
    public void onDeleteClick(int position) {
        if (!isSelectionMode) {
            imageItem.remove(position);
            adapter.notifyDataSetChanged();
            textView.setText("Photos (" + imageItem.size() + ")");
        } else {
            adapter.toggleSelection(position);
        }
    }

    @Override
    public void onImageClick(Uri imageUri) {
        Intent intent = new Intent(requireContext(), ImageZoomActivity.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }
    private void updateUploadCounter() {
        uploadedImages++; // Increment uploaded images count

        // Check if all images have been uploaded
        if (uploadedImages == totalImages) {
            // If all images uploaded successfully, hide the progress bar and navigate to the next fragment
            progressBar.setVisibility(View.GONE); // Hide the progress bar
            navigateToNextFragment();
        }
    }
}
