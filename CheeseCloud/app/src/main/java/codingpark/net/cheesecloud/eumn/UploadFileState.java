package codingpark.net.cheesecloud.eumn;

/**
 * Created by ethanshan on 14-10-23.
 * Mark the upload file state.
 */
public class UploadFileState {
    /**
     * Prepare to upload
     */
    public static final int NotUpload       = 0;
    /**
     * In process uploading
     */
    public static final int Uploading       = 1;
    /**
     * Upload completed
     */
    public static final int Uploaded        = 2;
}
