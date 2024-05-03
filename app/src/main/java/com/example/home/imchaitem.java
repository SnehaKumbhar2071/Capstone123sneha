import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class imchaitem implements Parcelable {
    private Uri imageUri;
    private String timeStamp;

    public imchaitem(Uri imageUri, String timeStamp) {
        this.imageUri = imageUri;
        this.timeStamp = timeStamp;
    }

    protected imchaitem(Parcel in) {
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        timeStamp = in.readString();
    }

    public static final Creator<imchaitem> CREATOR = new Creator<imchaitem>() {
        @Override
        public imchaitem createFromParcel(Parcel in) {
            return new imchaitem(in);
        }

        @Override
        public imchaitem[] newArray(int size) {
            return new imchaitem[size];
        }
    };

    public Uri getImageUri() {
        return imageUri;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(imageUri, flags);
        dest.writeString(timeStamp);
    }
}
