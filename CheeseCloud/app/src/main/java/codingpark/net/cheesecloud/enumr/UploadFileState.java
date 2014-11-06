package codingpark.net.cheesecloud.enumr;


/**
 * Mark the upload file state.
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:23:09
 */
public class UploadFileState {

    /**
     * Prepare to upload
     */
    public static final int NotUpload = 0;
    /**
     * User cancel upload the file
     */
    public static final int UploadCanceled = 3;
    /**
     * Upload completed file: the file already uploaded complete folder: the folder
     * created and sub file upload complete on server
     */
    public static final int Uploaded = 2;
    /**
     * In process uploading file: the file is uploading, and created on server folder:
     * the folder already created on server, but sub fille not upload complete.
     */
    public static final int Uploading = 1;

    public UploadFileState(){

    }

    public void finalize() throws Throwable {

    }

}
