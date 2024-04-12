package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        Picasso.get().load(imageUrl).into(holder.gridImage);

        // Set visibility of selection overlay based on the selection mode
        holder.selectionOverlay.setVisibility(isSelectionMode && selectedImages.contains(imageUrl) ? View.VISIBLE : View.GONE);
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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            gridImage = itemView.findViewById(R.id.gridImage);
            selectionOverlay = itemView.findViewById(R.id.selectionOverlay);

            // Set OnClickListener for the image
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
    }

}
