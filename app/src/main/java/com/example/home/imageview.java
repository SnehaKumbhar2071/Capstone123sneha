package com.example.home;
import android.app.Activity;
import android.content.Intent;
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

        profileImage = findViewById(R.id.shruti1);
        doctorNameEditText = findViewById(R.id.docshruti2);
        mobileNumberEditText = findViewById(R.id.shrutinumber);
        emailAddressEditText = findViewById(R.id.shrutiemail);
        addressEditText = findViewById(R.id.addressshruti);
        editProfileButton = findViewById(R.id.editshruti);
        saveButton = findViewById(R.id.saveshruti);
        ImageView imageView = findViewById(R.id.image2shruti);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setClipToOutline(true);

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

        // Here, you would save the profile information locally or via any other preferred storage mechanism
        // For demonstration purposes, we'll just display a toast message with the information
        String profileInfo = "Doctor's Name: " + doctorName + "\n" +
                "Mobile Number: " + mobileNumber + "\n" +
                "Email Address: " + emailAddress + "\n" +
                "Address: " + address;
        if ((imageUri != null)) {
            imageUri.toString();
        }
        Toast.makeText(this, profileInfo, Toast.LENGTH_LONG).show();

        doctorNameEditText.setEnabled(false);
        mobileNumberEditText.setEnabled(false);
        emailAddressEditText.setEnabled(false);
        addressEditText.setEnabled(false);
        isEditModeEnabled = false;

        saveButton.setVisibility(View.GONE);
        editProfileButton.setVisibility(View.VISIBLE);
    }
}
