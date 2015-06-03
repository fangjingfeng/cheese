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
     * 只是将记录添加到本地数据库,而不是在上面运行
     * 目前,下载文件从来没有在这种状态。
     */
    public static final int NOT_DOWNLOAD = 0;
    /**
     * 可以选择下载,等待选择它吗
     */
    public static final int WAIT_DOWNLOAD = 1;
    /**
     * 下载该文件
     */
    public static final int DOWNLOADING   = 2;
    /**
     * 文件下载完成
     */
    public static final int DOWNLOADED    = 3;
    
    /**
     * The download process is pause by user. Only user call resume it, the  
     * 文件切换到WAIT_DOWNLOAD状态
     */
    public static final int PAUSE_DOWNLOAD    = 4;
    /**
     * 文件下载失败
     */
    public static final int DOWNLOADEDFAILURE    = 5;
    public DownloadFileState(){

    }

    public void finalize() throws Throwable {

    }

}
