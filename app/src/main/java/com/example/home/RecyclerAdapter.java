package com.example.home;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<ImageItem> imageItems;
    private OnDeleteClickListener onDeleteClickListener;
    private OnImageClickListener onImageClickListener;
    private boolean isSelectionMode = false;
    private ArrayList<ImageItem> selectedItems = new ArrayList<>();

    public RecyclerAdapter(ArrayList<ImageItem> imageItems, OnDeleteClickListener onDeleteClickListener, OnImageClickListener onImageClickListener) {
        this.imageItems = imageItems;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem item = imageItems.get(position);
        holder.bind(item);
        holder.itemView.setActivated(selectedItems.contains(item));
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public ImageItem[] getSelectedItems() {
        return selectedItems.toArray(new ImageItem[0]);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void setSelectMode(boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        if (position != RecyclerView.NO_POSITION) {
            ImageItem item = imageItems.get(position);
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
            } else {
                selectedItems.add(item);
            }
            notifyItemChanged(position);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnImageClickListener {
        void onImageClick(Uri imageUri);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image2);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectionMode) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            toggleSelection(position);
                        }
                    } else {
                        if (onImageClickListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                onImageClickListener.onImageClick(imageItems.get(position).getUri());
                            }
                        }
                    }
                }
            });
        }

        public void bind(ImageItem item) {
            Glide.with(itemView.getContext())
                    .load(item.getUri())
                    .centerCrop()
                    .into(imageView);

            dateTextView.setText(item.getUploadDate());
        }
    }
}
