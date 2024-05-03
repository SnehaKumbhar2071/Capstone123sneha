package com.example.home;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class frag extends Fragment implements RecyclerAdapter.OnDeleteClickListener, RecyclerAdapter.OnImageClickListener {
    private static final int REQUEST_GALLERY = 1;
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String PREF_KEY_IMAGE_URIS = "image_uris";

    private RecyclerView recyclerView;
    private TextView textView;
    private Button pick, delete, select;
    private boolean isSelectionMode = false;
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private RecyclerAdapter adapter;

    public frag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag, container, false);
        textView = view.findViewById(R.id.totalphotossss);
        recyclerView = view.findViewById(R.id.recycler_gallarysss);
        pick = view.findViewById(R.id.picksss);
        delete = view.findViewById(R.id.deletesss);
        select = view.findViewById(R.id.selectsss);


        adapter = new RecyclerAdapter(imageItems, this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(adapter);

        // Set click listeners
        pick.setOnClickListener(v -> pickImagesFromGallery());
        delete.setOnClickListener(v -> onDeleteButtonClick());
        select.setOnClickListener(v -> toggleSelectionMode());


        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing); // Define your desired spacing in dp
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, spacingInPixels, true));


        retrieveImageUrisFromPrefs();

        return view;
    }

    private void pickImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_GALLERY);
    }

    private void saveImageToLocalStorage(Uri imageUri) {
        ContentResolver resolver = requireContext().getContentResolver();

        try {
            // Open an InputStream for reading from the imageUri
            InputStream inputStream = resolver.openInputStream(imageUri);

            // Generate a unique filename for each image
            String imageFileName = getImageFileName();

            // Create a FileOutputStream to save the image
            FileOutputStream outputStream = requireContext().openFileOutput(imageFileName, Context.MODE_PRIVATE);

            // Copy the data from the InputStream to the FileOutputStream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close both streams
            inputStream.close();
            outputStream.close();

            // Add the image URI to the list
            imageItems.add(new ImageItem(Uri.parse("file://" + requireContext().getFilesDir() + "/" + imageFileName), imageFileName));
            textView.setText("Photos (" + imageItems.size() + ")");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to generate a unique filename for each image
    private String getImageFileName() {
        // Use current timestamp and a unique identifier for the filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "JPEG_" + timeStamp + "_" + UUID.randomUUID().toString() + ".jpg";
    }


    private boolean containsImageUri(Uri uri) {
        for (ImageItem item : imageItems) {
            // Compare URIs as strings
            if (item.getImageUri().toString().equals(uri.toString())) {
                return true;
            }
        }
        return false;
    }

    private void onDeleteButtonClick() {
        if (isSelectionMode) {
            for (ImageItem item : adapter.getSelectedItems()) {
                imageItems.remove(item);
            }
            adapter.clearSelection();
            toggleSelectionMode();
        }
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
            if (requestCode == REQUEST_GALLERY && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        // Save each selected image
                        saveImageToLocalStorage(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    // Save the selected image
                    saveImageToLocalStorage(imageUri);
                }
                // Notify adapter after all images are added
                adapter.notifyDataSetChanged();

                // Save image URIs to SharedPreferences after updating imageItems
                saveImageUrisToPrefs();
            }
        }
    }

    private void saveImageUrisToPrefs() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();
        for (ImageItem item : imageItems) {
            jsonArray.put(item.getImageUri().toString());
        }
        editor.putString(PREF_KEY_IMAGE_URIS, jsonArray.toString());
        editor.apply();
    }


    private void retrieveImageUrisFromPrefs() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(PREF_KEY_IMAGE_URIS, null);
        if (json != null) {
            imageItems.clear(); // Clear the list before adding URIs from SharedPreferences
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String uriString = jsonArray.getString(i);
                    Uri imageUri = Uri.parse(uriString);
                    imageItems.add(new ImageItem(imageUri, ""));
                }
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDeleteClick(int position) {
        if (!isSelectionMode) {
            imageItems.remove(position);
            adapter.notifyDataSetChanged();

        } else {
            adapter.toggleSelection(position);
        }
    }

    @Override
    public void onImageClick(Uri imageUri) {
        Intent intent = new Intent(requireContext(), fullsizeimage.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        retrieveImageUrisFromPrefs();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveImageUrisToPrefs();
    }
}
