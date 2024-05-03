package com.example.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class imageview extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImage;
    private EditText doctorNameEditText, mobileNumberEditText, emailAddressEditText, addressEditText;
    private Button editProfileButton, saveButton;
    private boolean isEditModeEnabled = false;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        profileImage = findViewById(R.id.image2shruti);
        doctorNameEditText = findViewById(R.id.dshr);
        mobileNumberEditText = findViewById(R.id.shrutinumber);
        emailAddressEditText = findViewById(R.id.shrutiemail);
        addressEditText = findViewById(R.id.addressshruti);
        editProfileButton = findViewById(R.id.editshruti);
        saveButton = findViewById(R.id.saveshruti);

        profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileImage.setClipToOutline(true);

        loadProfile();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditModeEnabled) {
                    openImagePicker();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableEditMode() {
        doctorNameEditText.setEnabled(true);
        mobileNumberEditText.setEnabled(true);
        emailAddressEditText.setEnabled(true);
        addressEditText.setEnabled(true);
        isEditModeEnabled = true;

        saveButton.setVisibility(View.VISIBLE);
        editProfileButton.setVisibility(View.GONE);
    }

    private void saveProfile() {
        String doctorName = doctorNameEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        SharedPreferences.Editor editor = getSharedPreferences("ProfilePrefs", MODE_PRIVATE).edit();
        editor.putString("doctorName", doctorName);
        editor.putString("mobileNumber", mobileNumber);
        editor.putString("emailAddress", emailAddress);
        editor.putString("address", address);
        if (imageUri != null) {
            editor.putString("imageUri", imageUri.toString());
        }
        editor.apply();

        String profileInfo = "Doctor's Name: " + doctorName + "\n" +
                "Mobile Number: " + mobileNumber + "\n" +
                "Email Address: " + emailAddress + "\n" +
                "Address: " + address;
        Toast.makeText(this, profileInfo, Toast.LENGTH_LONG).show();

        doctorNameEditText.setEnabled(false);
        mobileNumberEditText.setEnabled(false);
        emailAddressEditText.setEnabled(false);
        addressEditText.setEnabled(false);
        isEditModeEnabled = false;

        saveButton.setVisibility(View.GONE);
        editProfileButton.setVisibility(View.VISIBLE);
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
        doctorNameEditText.setText(prefs.getString("doctorName", ""));
        mobileNumberEditText.setText(prefs.getString("mobileNumber", ""));
        emailAddressEditText.setText(prefs.getString("emailAddress", ""));
        addressEditText.setText(prefs.getString("address", ""));
        String imageUriString = prefs.getString("imageUri", null);
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Save the profile information before exiting the app
        saveProfile();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the profile information when the activity is paused
        saveProfile();
    }
}
