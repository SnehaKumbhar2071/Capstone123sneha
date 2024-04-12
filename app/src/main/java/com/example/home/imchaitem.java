package com.example.home;
import android.net.Uri;

public class imchaitem{
    private Uri uri;
    private String uploadDate;

    public imchaitem(Uri uri, String uploadDate) {
        this.uri = uri;
        this.uploadDate = uploadDate;
    }

    public Uri getUri() {
        return uri;
    }

    public String getUploadDate() {
        return uploadDate;
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

