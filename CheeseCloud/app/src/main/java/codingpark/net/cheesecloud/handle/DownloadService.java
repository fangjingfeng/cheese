package codingpark.net.cheesecloud.handle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;

/**
 * The service download file from remote server
 */
public class DownloadService extends Service {
    private static final String TAG         = DownloadService.class.getSimpleName();

    /**
     * The download block size in byte unit
     * Default size 100KB
     */
    public static final int DOWNLOAD_BLOCK_SIZE             = 100 * CheeseConstants.KB;

    /**
     * Download state changed action
     */
    public static final String ACTION_DOWNLOAD_STATE_CHANGE   = "codingpark.net.cheesecloud.handle.ACTION_DOWNLOAD_STATE_CHANGE";
    /**
     * In TransferStateActivity, when havel download file item in wait or downloading
     * state, user can click cancel all button, then trigger send
     * ACTION_CANCEL_ALL_DOWNLOAD action to DownloadService, DownloadService will stop
     * current download thread and remove all download record which state is wait or
     * downloading  in download table. In addition, DownloadService update wait list.
     */
    public static final String ACTION_CANCEL_ALL_DOWNLOAD = "codingpark.net.cheesecloud.handle.ACTION_CANCEL_ALL_DOWNLOAD";
    /**
     * In TransferStateActivity, when have download file item in
     * wait/pause/downloading  state, user can click one of the item, then trigger
     * send ACTION_CANCEL_ALL_DOWNLOAD action to DownloadService with the selected
     * item data, DownloadService will stop current download thread and remove the
     * selectedl download record which state is wait or downloading  in download table.
     * In addition, DownloadService update wait list, and then restart the download
     * thread.
     */
    public static final String ACTION_CANCEL_ONE_DOWNLOAD = "codingpark.net.cheesecloud.handle.ACTION_CANCEL_ONE_DOWNLOAD";
    /**
     * In TransferStateActivity, when all download item record in downloaded state,
     * user can click clear all button , then trigger send ACTION_CLEAR_ALL_DOWNLOAD_RECORD
     * action to DownloadService, DownloadService will stop current download thread
     * and remove the  download record which state is downloaded  in download table.
     * In addition, DownloadService update wait list.
     */
    public static final String ACTION_CLEAR_ALL_DOWNLOAD_RECORD = "codingpark.net.cheesecloud.handle.ACTION_CLEAR_ALL_DOWNLOAD_RECORD";
    /**
     * In TransferStateActivity, when havel download file item in wait or downloading
     * state, user can click pause all button, then trigger send
     * ACTION_PAUSE_ALL_DOWNLOAD action to DownloadService, DownloadService will stop
     * current download thread and udpate all download record in download table. In
     * addition, DownloadService update wait list.
     */
    public static final String ACTION_PAUSE_ALL_DOWNLOAD = "codingpark.net.cheesecloud.handle.ACTION_PAUSE_ALL_DOWNLOAD";
    /**
     * In TransferStateActivity, when all download file item is pause state, user
     * click resume all button, then trigger send ACTION_RESUME_ALL_DOWNLOAD action to
     * DownloadService, DownloadService will update all record state to wait in
     * download table. In addition, DownloadService update wait list.
     */
    public static final String ACTION_RESUME_ALL_DOWNLOAD = "codingpark.net.cheesecloud.handle.ACTION_RESUME_ALL_DOWNLOAD";
    /**
     * When insert all file which to be download to local database, send
     * ACTION_START_ALL_DOWNLOAD to DownloadService, to notify this service to update
     * download wait file list.
     */
    public static final String ACTION_START_ALL_DOWNLOAD = "codingpark.net.cheesecloud.handle.ACTION_START_ALL_DOWNLOAD";


    public static final int EVENT_CANCEL_ALL_DOWNLOAD_FAILED = 1;
    public static final int EVENT_CANCEL_ALL_DOWNLOAD_SUCCESS = 0;
    public static final int EVENT_CANCEL_ONE_DOWNLOAD_FAILED = 3;
    public static final int EVENT_CANCEL_ONE_DOWNLOAD_SUCCESS = 2;
    public static final int EVENT_CLEAR_ALL_DOWNLOAD_FAILED = 5;
    public static final int EVENT_CLEAR_ALL_DOWNLOAD_SUCCESS = 4;
    public static final int EVENT_PAUSE_ALL_DOWNLOAD_FAILED = 7;
    public static final int EVENT_PAUSE_ALL_DOWNLOAD_SUCCESS = 6;
    public static final int EVENT_RESUME_ALL_DOWNLOAD_FAILED = 9;
    public static final int EVENT_RESUME_ALL_DOWNLOAD_SUCCESS = 8;
    public static final int EVENT_START_ALL_DOWNLOAD_FAILED = 11;
    public static final int EVENT_START_ALL_DOWNLOAD_SUCCESS = 10;
    public static final int EVENT_DOWNLOAD_BLOCK_SUCCESS                  = 12;

    public static final int EVENT_DOWNLOAD_BLOCK_FAILED                   = 13;

    /**
     * When send download related action with extra data download file object, use
     * EXTRA_DOWNLOAD_FILE as the data key.
     */
    public static final String EXTRA_DOWNLOAD_FILE = "download_file";
    /**
     * When send download related action with extra state information, use
     * EXTRA_DOWNLOAD_STATE as the data key.
     */
    public static final String EXTRA_DOWNLOAD_STATE = "download_file";

    private DownloadFileDataSource downloadFileDataSource     = null;

    private static ArrayList<DownloadFile> mWaitDataList       = null;

    private static DownloadTask mTask                       = null;

    private static Context mContext                         = null;

    /**
     * Instance method, dispatch the intent request to local command hanlder
     *
     * @param intent    The Intent object, include download related action and data
     * information
     */
    private void onHandleIntent(Intent intent){
        // TODO Implement the handle function for every action
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_ALL_DOWNLOAD.equals(action)) {
                handleActionStartAllDownload(intent);
            } else if(ACTION_RESUME_ALL_DOWNLOAD.equals(action)) {
                handleActionResumeAllDownload(intent);
            } else if (ACTION_PAUSE_ALL_DOWNLOAD.equals(action)) {
                handleActionPauseAllDownload(intent);
            } else if (ACTION_CANCEL_ALL_DOWNLOAD.equals(action)) {
                handleActionCancelAllDownload(intent);
            } else if (ACTION_CANCEL_ONE_DOWNLOAD.equals(action)) {
                handleActionCancelOneDownload(intent);
            } else if (ACTION_CLEAR_ALL_DOWNLOAD_RECORD.equals(action)) {
                handleActionClearAllDownloadRecord(intent);
            }
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "DownloadService created");
        if (mTask == null) {
            Log.d(TAG, "DownloadTask is null, create new");
            mTask = new DownloadTask();
        }
        Log.d(TAG, "Create DownloadFileDataSource success");
        downloadFileDataSource = new DownloadFileDataSource(this);
        downloadFileDataSource.open();
        // 1. Stop download thread
        //handleActionPauseAllDownload();
        // 2. Update mWaitDataList data
        //mWaitDataList =
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        refreshWaitData();
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void refreshWaitData() {
        mWaitDataList = downloadFileDataSource.getAllDownloadFile();
        //mWaitDataList = downloadFileDataSource.getAllUploadFile();
        Log.d(TAG, "refreshWaitData: mWaitDataList.size = " + mWaitDataList.size());
    }
    // TODO Implement follow handle action function

    /**
     * Handle ACTION_START_ALL_DOWNLOAD
     */
    private void handleActionStartAllDownload(Intent intent) {
        Log.d(TAG, "handelActionStartAllDownload");
    }

    /**
     * Handle ACTION_RESUME_ALL_DOWNLOAD
     */
    private void handleActionResumeAllDownload(Intent intent) {
        Log.d(TAG, "handleActionResumeAllDownload");
    }

    /**
     * Handle ACTION_PAUSE_ALL_DOWNLOAD
     */
    private void handleActionPauseAllDownload(Intent intent) {
        Log.d(TAG, "handleActionPauseAllDownload");
    }

    /**
     * Handle ACTION_CANCEL_ALL_DOWNLOAD
     */
    private void handleActionCancelAllDownload(Intent intent) {
        Log.d(TAG, "handleActionCancelAllDownload");
    }

    /**
     * Handle ACTION_CANCEL_ONE_DOWNLOAD
     */
    private void handleActionCancelOneDownload(Intent intent) {
        Log.d(TAG, "handleActionCancelOneDownload");
    }

    /**
     * Handle ACTION_CLEAR_ALL_DOWNLOAD_RECORD
     */
    private void handleActionClearAllDownloadRecord(Intent intent) {
        Log.d(TAG, "handleActionClearAllDownloadRecord");
    }

    /**
     * Call this function to send ACTION_CANCEL_ALL_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionCancelAll(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_CANCEL_ALL_DOWNLOAD);
        context.startService(intent);
    }

    /**
     * Call this function to send ACTION_CANCEL_ONE_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionCancelOne(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_CANCEL_ONE_DOWNLOAD);
        context.startService(intent);
    }

    /**
     * Call this function to send ACTION_CLEAR_ALL_DOWNLOAD_RECORD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionClearAll(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_CLEAR_ALL_DOWNLOAD_RECORD);
        context.startService(intent);
    }

    /**
     * Call this function to send ACTION_PAUSE_ALL_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionPauseAll(Context context){
        Log.d(TAG, "startActionPauseAll:");
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE_ALL_DOWNLOAD);
        context.startService(intent);
    }

    /**
     * Call this function to send ACTION_RESUME_ALL_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionResumeAll(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_RESUME_ALL_DOWNLOAD);
        context.startService(intent);
    }

    /**
     * Call this function to send ACTION_START_ALL_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionStartAll(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_START_ALL_DOWNLOAD);
        context.startService(intent);
    }

    /**
     * Call this function to stop the DownloadService.
     *
     * @param context    The application context
     */
    public static void stopUploadService(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        context.stopService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class DownloadTask extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }
}
