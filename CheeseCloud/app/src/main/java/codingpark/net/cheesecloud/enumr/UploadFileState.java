package codingpark.net.cheesecloud.enumr;


/**
 * Mark the upload file state.
 * For folder: NOT_UPLOAD/UPLOADED
 * For file: NOT_UPLOAD/WAIT_UPLOAD/UPLOADING/UPLOADED/PAUSE_UPLOAD
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:23:09
 */
public class UploadFileState {

    /**
     * For file: Not call checkedFileInfo
     * For folder: Not call createFolder
     * Warning: Current not use
     */
    public static final int NOT_UPLOAD = 0;
    /**
     * For file: Already call checkedFileInfo, and the upload thread not run(wait run)
     * For folder: Not available
     */
    public static final int WAIT_UPLOAD = 1;
    /**
     * For file: Already call checkedFileInfo, and the upload thread run on it
     * For folder: Not available
     */
    public static final int UPLOADING   = 2;
    /**
     * For file: Already call checkedFileInfo, and all file block uploaded
     * For folder: Already call createFolder
     */
    public static final int UPLOADED    = 3;
    /**
     * For file: Already call checkedFileInfo, and the upload thread not run(User pause file uploading)
     * For folder: Not available
     */
    public static final int PAUSE_UPLOAD    = 4;

    public UploadFileState(){

    }

    public void finalize() throws Throwable {

    }

}
