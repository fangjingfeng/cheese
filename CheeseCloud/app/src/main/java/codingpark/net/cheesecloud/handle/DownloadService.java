package codingpark.net.cheesecloud.handle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;
import codingpark.net.cheesecloud.utils.Misc;
import codingpark.net.cheesecloud.wsi.SyncFileBlock;
import codingpark.net.cheesecloud.wsi.WsSyncFile;
import codingpark.net.cheesecloud.wsi.WsSyncFileBlock;

/**
 * The service download file from remote server
 */
public class DownloadService extends Service {
    private static final String TAG         = DownloadService.class.getSimpleName();
    public static final int MAX_RETRY_TIME                  = 3;

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
    public static final int EVENT_CLEAR_ALL_DOWNLOAD_RECORD_FAILED = 5;
    public static final int EVENT_CLEAR_ALL_DOWNLOAD_RECORD_SUCCESS = 4;
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
        mWaitDataList = downloadFileDataSource.getNotDownloadedFile();
        Log.d(TAG, "refreshWaitData: mWaitDataList.size = " + mWaitDataList.size());
    }

    private void pauseDownloadThread() {
        if (mTask != null && mTask.isAlive()) {
            mTask.interrupt();
            try {
                mTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Pause download thread success");
        }
    }

    private void startDownloadThread() {
        if (mTask == null) {
            mTask = new DownloadTask();
        }
        if (mTask.isAlive()) {
            Log.d(TAG, "DownloadThread is running, not need start again");
            return;
        }
        // If task stop and not NEW state, just create new DownloadTask
        if (mTask.getState() != Thread.State.NEW) {
            Log.d(TAG, "download thread have completed, new ThreadTask");
            mTask = null;
            mTask = new DownloadTask();
        }
        Log.d(TAG, "Start downloading");
        // Start download
        mTask.start();
    }

    // TODO Implement follow handle action function

    /**
     * Handle ACTION_START_ALL_DOWNLOAD
     */
    private void handleActionStartAllDownload(Intent intent) {
        Log.d(TAG, "handelActionStartAllDownload");
        // For sync, we stop upload thread first
        // 1. Pause mTask
        pauseDownloadThread();
        // 2. Refresh mWaitDataList from local table upload_table
        refreshWaitData();
        // 3. Start mTask again
        startDownloadThread();
    }

    /**
     * Handle ACTION_RESUME_ALL_DOWNLOAD
     */
    private void handleActionResumeAllDownload(Intent intent) {
        Log.d(TAG, "handleActionResumeAllDownload");

        // For sync, we stop download thread first
        // 1. Pause mTask
        pauseDownloadThread();
        // 2. Update all pause state record to wait
        ArrayList<DownloadFile> tmp_datas = downloadFileDataSource.getAllDownloadFileByState(DownloadFileState.PAUSE_DOWNLOAD);
        for (int i = 0; i < tmp_datas.size(); i++) {
            DownloadFile file = tmp_datas.get(i);
            file.setState(DownloadFileState.WAIT_DOWNLOAD);
            downloadFileDataSource.updateDownloadFile(file);
        }
        // 3. Refresh mWaitDataList from local table download_table
        refreshWaitData();
        // 4. Start mTask again
        startDownloadThread();
        // 5. Send broadcast
        sendChangedBroadcast(EVENT_RESUME_ALL_DOWNLOAD_SUCCESS);
    }

    /**
     * Handle ACTION_PAUSE_ALL_DOWNLOAD
     */
    private void handleActionPauseAllDownload(Intent intent) {
        Log.d(TAG, "handleActionPauseAllDownload");

        // 1. Pause download thread
        pauseDownloadThread();
        // 2. Set all wait and downloading state record to pause state
        for (int i = 0; i < mWaitDataList.size(); i++) {
            DownloadFile file = mWaitDataList.get(i);
            int state = file.getState();
            if (state == DownloadFileState.DOWNLOADING || state == DownloadFileState.WAIT_DOWNLOAD)
                file.setState(DownloadFileState.PAUSE_DOWNLOAD);
            downloadFileDataSource.updateDownloadFile(file);
        }
        sendChangedBroadcast(EVENT_PAUSE_ALL_DOWNLOAD_SUCCESS);
    }

    /**
     * Handle ACTION_CANCEL_ALL_DOWNLOAD
     */
    private void handleActionCancelAllDownload(Intent intent) {
        Log.d(TAG, "handleActionCancelAllDownload");

        // 1. Pause download thread
        pauseDownloadThread();
        // 2. Delete downloading and wait state record from database
        // TODO Need delete rubbish records/datas from server
        downloadFileDataSource.deleteDownloadFileByState(DownloadFileState.DOWNLOADING);
        downloadFileDataSource.deleteDownloadFileByState(DownloadFileState.WAIT_DOWNLOAD);
        downloadFileDataSource.deleteDownloadFileByState(DownloadFileState.PAUSE_DOWNLOAD);
        // 3. Update mWaitDataList
        refreshWaitData();
        // 4. Send broadcast
        sendChangedBroadcast(EVENT_CANCEL_ALL_DOWNLOAD_SUCCESS);
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
        // 1. Pause download thread
        pauseDownloadThread();
        // 2. Delete downloaded state record from database
        downloadFileDataSource.deleteDownloadFileByState(DownloadFileState.DOWNLOADED);
        // 3. Send broadcast
        sendChangedBroadcast(EVENT_CLEAR_ALL_DOWNLOAD_RECORD_SUCCESS);
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

    private void sendChangedBroadcast(DownloadFile file, int event) {
        Intent intent = new Intent(ACTION_DOWNLOAD_STATE_CHANGE);
        intent.putExtra(EXTRA_DOWNLOAD_FILE, file);
        intent.putExtra(EXTRA_DOWNLOAD_STATE, event);
        getApplicationContext().sendBroadcast(intent);
        Log.d(TAG, "Send download state changed broadcast with file: " + event);
    }

    private void sendChangedBroadcast(int event) {
        Intent intent = new Intent(ACTION_DOWNLOAD_STATE_CHANGE);
        intent.putExtra(EXTRA_DOWNLOAD_STATE, event);
        getApplicationContext().sendBroadcast(intent);
        Log.d(TAG, "Send download state changed broadcast without file: " + event);
    }

    /**
     * Get the download files/folders stored root folder.
     * Such as /sdcard/CheeseCloudDownload
     * @return The download root folder
     */
    private String getDownloadRootDir() {
        String prefix_path = Environment.getExternalStorageDirectory().toString();
        String path = Misc.mergePath(prefix_path, CheeseConstants.DOWNLOAD_ROOT_DIR_NAME);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class DownloadTask extends Thread {
        private int mTry = 0;

        @Override
        public void run() {
            for (int i = 0; i < mWaitDataList.size(); i++) {
                int result = WsResultType.Success;
                mTry = 0;
                DownloadFile file = mWaitDataList.get(i);
                // 1. Update file state to DOWNLOADING
                if (file.getState() == DownloadFileState.WAIT_DOWNLOAD) {
                    file.setState(DownloadFileState.DOWNLOADING);
                    downloadFileDataSource.updateDownloadFile(file);
                }
                // 2. Create local full directory
                String parent_dir_path = getDownloadRootDir();
                parent_dir_path = Misc.mergePath(parent_dir_path, file.getFilePath().substring(0, file.getFilePath().lastIndexOf(CheeseConstants.SEPARATOR)));
                String full_path = Misc.mergePath(parent_dir_path, file.getFilePath().substring(file.getFilePath().lastIndexOf(CheeseConstants.SEPARATOR) + 1));
                Log.d(TAG, "Download File parent directory path: " + parent_dir_path);
                Log.d(TAG, "Download file full path: " + full_path);
                // TODO Handle create full directory failed situation
                Misc.createFullDir(parent_dir_path);
                // 3. Create the download file
                File r_file = new File(file.getFilePath());
                /* Not need create the empty
                if (!r_file.exists()) {
                    try {
                        // TODO Handle create file failed situation
                        r_file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                */
                // 4. Download block one by one
                int count = 0;
                WsSyncFile syncFile = new WsSyncFile();
                SyncFileBlock syncBlock = new SyncFileBlock();
                syncBlock.SourceSize = DOWNLOAD_BLOCK_SIZE;
                syncFile.Blocks = syncBlock;
                syncFile.ID = file.getRemote_id();
                try {
                    while (true) {
                        if (isInterrupted()) {
                            Log.d(TAG, "isInterrupted");
                            return;
                        }
                        syncBlock.OffSet = file.getChangedSize();
                        result = ClientWS.getInstance(DownloadService.this).downloadFile(syncFile);
                        if (result != WsResultType.Success) {
                            sendChangedBroadcast(file, EVENT_DOWNLOAD_BLOCK_FAILED);
                            if (mTry < MAX_RETRY_TIME) {
                                Log.d(TAG, "Download failed, retry: " + mTry);
                                continue;
                            }
                            else {
                                Log.d(TAG, "Download failed, reach limit:" + MAX_RETRY_TIME + "\t" + " stop: " + file.getFilePath());
                                break;
                            }
                        }
                        mTry = 0;
                        count = syncFile.Blocks.UpdateData.length;
                        Log.d(TAG, "Download real size:" + count + "\n" + "Download block size: " + DOWNLOAD_BLOCK_SIZE);
                        if (count >0 ) {
                            RandomAccessFile stream = new RandomAccessFile(r_file, "rw");
                            stream.seek(file.getChangedSize());
                            stream.write(syncFile.Blocks.UpdateData, 0, count);
                            // Increase index
                            file.setChangedSize(file.getChangedSize() + count);
                            // Download completed
                            if (syncFile.IsFinally) {
                                file.setState(DownloadFileState.DOWNLOADED);
                            }
                            // Update to database
                            downloadFileDataSource.updateDownloadFile(file);
                            sendChangedBroadcast(file, EVENT_DOWNLOAD_BLOCK_SUCCESS);
                        } else
                            break;// Download completed
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
