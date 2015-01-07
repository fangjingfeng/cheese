package codingpark.net.cheesecloud.enumr;


/**
 * Mark the download file state.
 * For folder: Folder needn't record on local table
 * For file: NOT_DOWNLOAD/WAIT_DOWNLOAD/DOWNLOADING/DOWNLOADED/PAUSE_DOWNLOAD
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:23:09
 */
public class DownloadFileState {

    /**
     * Just add the record to local database, DownloadService not run on it ever
     * Current, the download file never in this state.
     */
    public static final int NOT_DOWNLOAD = 0;
    /**
     * Can be select to download, wait the DownloadService select it
     */
    public static final int WAIT_DOWNLOAD = 1;
    /**
     * The file is downloading by DownloadService
     */
    public static final int DOWNLOADING   = 2;
    /**
     * The file is download completed
     */
    public static final int DOWNLOADED    = 3;
    /**
     * The download process is pause by user. Only user call resume it, the
     * file switch to WAIT_DOWNLOAD state
     */
    public static final int PAUSE_DOWNLOAD    = 4;

    public DownloadFileState(){

    }

    public void finalize() throws Throwable {

    }

}
