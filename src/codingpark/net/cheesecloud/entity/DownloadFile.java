package codingpark.net.cheesecloud.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一�?-2014 15:25:40
 */
public class DownloadFile extends CloudFile {

    public DownloadFile() {

    }


    public void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public DownloadFile(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<DownloadFile> CREATOR
            = new Parcelable.Creator<DownloadFile>() {
        public DownloadFile createFromParcel(Parcel in) {
            return new DownloadFile(in);
        }

        public DownloadFile[] newArray(int size) {
            return new DownloadFile[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DownloadFile(CloudFile file) {
        this.setChangedSize(file.getChangedSize());
        this.setCreateDate(file.getCreateDate());
        this.setFilePath(file.getFilePath());
        this.setFileSize(file.getFileSize());
        this.setFileType(file.getFileType());
        this.setId(file.getId());
        this.setLocal_user_id(file.getLocal_user_id());
        this.setMd5(file.getMd5());
        this.setParent_id(file.getParent_id());
        this.setRecordCreateDate(file.getRecordCreateDate());
        this.setRemote_id(file.getRemote_id());
        this.setRemote_parent_id(file.getRemote_parent_id());
        this.setThumb_uri_name(file.getThumb_uri_name());
        this.setState(file.getState());
    }

}
