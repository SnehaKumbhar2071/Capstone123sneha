package com.example.home;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImageZoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        ImageView imageView = findViewById(R.id.photo_view);

        // Retrieve the image URL from intent extras
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Check if imageUrl is not null before using it
        if (imageUrl != null) {
            // Load the image into the zoomable ImageView using Picasso

            // Load and display the image using Glide
            Glide.with(this)
                    .load(Uri.parse(imageUrl))
                    .into(imageView);
        } else {
            // Handle the case where imageUrl is null
            Toast.makeText(this, "Image URL is null", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if imageUrl is null
        }
    }

}
