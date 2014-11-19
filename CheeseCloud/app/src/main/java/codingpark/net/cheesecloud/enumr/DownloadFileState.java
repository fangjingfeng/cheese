package codingpark.net.cheesecloud.enumr;


/**
 * Mark the download file state.
 * For folder: NOT_UPLOAD/UPLOADED
 * For file: NOT_UPLOAD/WAIT_UPLOAD/UPLOADING/UPLOADED/PAUSE_UPLOAD
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:23:09
 */
public class DownloadFileState {

    // TODO Analysis the state meaning
    public static final int NOT_DOWNLOAD = 0;
    public static final int WAIT_DOWNLOAD = 1;
    public static final int DOWNLOADING   = 2;
    public static final int DOWNLOADED    = 3;
    public static final int PAUSE_DOWNLOAD    = 4;

    public DownloadFileState(){

    }

    public void finalize() throws Throwable {

    }

}
