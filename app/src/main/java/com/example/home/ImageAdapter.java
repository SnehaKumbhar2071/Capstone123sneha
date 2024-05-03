package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imageUrls;
    private Context context;
    private ArrayList<String> selectedImages = new ArrayList<>();
    private boolean isSelectionMode = false;
    private ArrayList<ImageItem> imageItems;
    private RecyclerAdapter.OnDeleteClickListener onDeleteClickListener;
    private RecyclerAdapter.OnImageClickListener onImageClickListener;

    public ImageAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        holder.bind(imageUrl);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public ArrayList<String> getSelectedImages() {
        return selectedImages;
    }

    public void clearSelection() {
        selectedImages.clear();
        notifyDataSetChanged();
    }

    public void toggleSelection(String imageUrl) {
        if (selectedImages.contains(imageUrl)) {
            selectedImages.remove(imageUrl);
        } else {
            selectedImages.add(imageUrl);
        }
        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
        notifyDataSetChanged();
    }

    public ArrayList<String> getImageUrls() {
        return new ArrayList<>(imageUrls);
    }
    public ImageAdapter(ArrayList<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = new ArrayList<>(imageUrls);
        notifyDataSetChanged();
    }
    public void addImageUrl(String imageUrl) {
        imageUrls.add(imageUrl);
        notifyDataSetChanged();
    }

    public void setSelectMode(boolean b) {
        isSelectionMode = b;
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView gridImage;
        View selectionOverlay;
        TextView lastModified; // Add TextView for last modified date

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            gridImage = itemView.findViewById(R.id.gridImage);
            selectionOverlay = itemView.findViewById(R.id.selectionOverlay);
            lastModified = itemView.findViewById(R.id.lastmodified); // Initialize TextView for last modified date

            // Set OnClickListener for the image
            gridImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle image click event
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String imageUrl = imageUrls.get(position);
                        if (isSelectionMode) {
                            // Toggle selection
                            toggleSelection(imageUrl);
                        } else {
                            // Launch ImageZoomActivity with the clicked image URL
                            Intent intent = new Intent(context, ImageZoomActivity.class);
                            intent.putExtra("imageUrl", imageUrl);
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }

        public void bind(String imageUrl) {
            // Load image using Picasso
            Picasso.get().load(imageUrl).into(gridImage);

            // Fetch last modified date for the image from Firestore Storage metadata
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
            imageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    long lastModifiedTimestamp = storageMetadata.getUpdatedTimeMillis();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(new Date(lastModifiedTimestamp));
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()); // Corrected time format pattern
                    String formattedTime = timeFormat.format(new Date(lastModifiedTimestamp));
                    lastModified.setText("Date: " + formattedDate + " Time: " + formattedTime); // Set last modified date and time
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure
                    Log.e("ImageAdapter", "Failed to fetch metadata: " + e.getMessage());
                }
            });

            // Set visibility of selection overlay based on the selection mode
            selectionOverlay.setVisibility(isSelectionMode && selectedImages.contains(imageUrl) ? View.VISIBLE : View.GONE);
        }
    }}