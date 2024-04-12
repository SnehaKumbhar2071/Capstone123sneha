package com.example.home;

import android.net.Uri;

public class ImageItem {
    private Uri uri;
    private String uploadDate;
    private boolean isSelected;

    public ImageItem(Uri uri, String uploadDate) {
        this.uri = uri;
        this.uploadDate = uploadDate;
    }

    public Uri getUri() {
        return uri;
    }

    public String getUploadDate() {
        return uploadDate;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Uri getImageUri() {
        return uri; // Return the image URI
    }

    public Object getImageUrl() {
        return getImageUrl();
    }

    public String getId() {
        return getId();
    }
}

