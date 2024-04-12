package com.example.home;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class card2 extends AppCompatActivity implements gallerychaadapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private gallerychaadapter galleryAdapter;
    private ArrayList<Uri> selectedImageUris;
    private ActionMode actionMode;

    private static final int REQUEST_PICK_IMAGES = 101;
    private static final int REQUEST_CAPTURE_IMAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card2);

        recyclerView = findViewById(R.id.recycler_gallery15);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Button pick = findViewById(R.id.pick);
//
        selectedImageUris = new ArrayList<>();
        galleryAdapter = new gallerychaadapter(this, selectedImageUris, this);
        recyclerView.setAdapter(galleryAdapter);

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open system image picker for multiple images
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_PICK_IMAGES);
            }
        });

//        capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Open camera to capture image
//
//                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (captureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(captureIntent, REQUEST_CAPTURE_IMAGE);
//                } else {
//                    Toast.makeText(MainActivity.this, "No camera app available", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            handleImageSelection(data);
        } else if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK && data != null) {
            handleImageCapture(data);
        }
    }

    private void handleImageSelection(Intent data) {
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                selectedImageUris.add(imageUri);
            }
        } else if (data.getData() != null) {
            Uri imageUri = data.getData();
            selectedImageUris.add(imageUri);
        }
        galleryAdapter.notifyDataSetChanged();
    }

    private void handleImageCapture(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null && extras.containsKey("data")) {
            // Extract the captured image bitmap
            // Here you can handle the captured image bitmap as needed
            // For example, you can convert it to a URI and add it to the selectedImageUris list
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Example: Convert bitmap to URI
            Uri imageUri = getImageUriFromBitmap(imageBitmap);
            selectedImageUris.add(imageUri);
            galleryAdapter.notifyDataSetChanged();
        }
    }

    // Utility method to convert Bitmap to URI
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }

    @Override
    public void onItemClick(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            // Open image in full screen
            Uri imageUri = selectedImageUris.get(position);
            openFullScreenImage(imageUri.toString());
        }
    }

    private void toggleSelection(int position) {
        galleryAdapter.toggleSelection(position);
        int count = galleryAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_deleteshruti) {
            deleteSelectedImages();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSelectedImages() {
        List<Integer> selectedItemPositions = galleryAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            selectedImageUris.remove(selectedItemPositions.get(i).intValue());
            galleryAdapter.notifyItemRemoved(selectedItemPositions.get(i));
        }
        actionMode.finish();
    }

    @Override
    public void onItemLongClick(int position) {
        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_deleteshruti) {
                deleteSelectedImages();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            galleryAdapter.clearSelection();
            actionMode = null;
        }
    };

    private void openFullScreenImage(String imageUri) {
        Intent intent = new Intent(card2.this, fullsizeimage.class);
        intent.putExtra("imageUri", imageUri);
        startActivity(intent);
    }
}
