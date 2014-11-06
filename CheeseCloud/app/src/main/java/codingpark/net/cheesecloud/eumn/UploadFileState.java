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
     * file: the file is uploading, and created on server
     * folder: the folder already created on server, but sub
     * file not upload complete.
     */
    public static final int Uploading       = 1;
    /**
     * Upload completed
     * file: the file already uploaded complete
     * folder: the folder created and sub file upload complete on server
     */
    public static final int Uploaded        = 2;
    /**
     * User cancel upload the file
     */
    public static final int UploadCanceled  = 3;
}
