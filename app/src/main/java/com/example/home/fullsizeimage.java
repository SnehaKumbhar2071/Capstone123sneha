package com.example.home;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class fullsizeimage extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimageview);

        ImageView imageView = findViewById(R.id.photo_view789);

        // Get the image URI from the intent
        String imageUriString = getIntent().getStringExtra("imageUri");

        // Load and display the image using Glide
        Glide.with(this)
                .load(Uri.parse(imageUriString))
                .into(imageView);
    }
}
