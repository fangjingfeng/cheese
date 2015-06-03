package codingpark.net.cheesecloud.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * The UploadFile entity, store uploading files information from database or
 * remote server
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一�?-2014 15:33:15
 */
public class UploadFile extends CloudFile implements Parcelable{

    public UploadFile(){}

    public void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public UploadFile(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<UploadFile> CREATOR
            = new Parcelable.Creator<UploadFile>() {
        public UploadFile createFromParcel(Parcel in) {
            return new UploadFile(in);
        }

        public UploadFile[] newArray(int size) {
            return new UploadFile[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
