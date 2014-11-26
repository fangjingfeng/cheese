package codingpark.net.cheesecloud.handle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.wsi.SyncFileBlock;
import codingpark.net.cheesecloud.wsi.WsSyncFile;

/**
 * An {@link Service} subclass for handling asynchronous upload
 * task requests in a service on a separate handler thread.
 */
public class UploadService extends Service {
    public static final String TAG      = UploadService.class.getSimpleName();

    public static final int MAX_RETRY_TIME                  = 3;

    /**
     * The upload block size in byte unit
     * Default size 100KB
     */
    public static final int UPLOAD_BLOCK_SIZE               = 100 * CheeseConstants.KB;
    /**
     * Upload state changed action
     */
    public static final String ACTION_UPLOAD_STATE_CHANGE       = "codingpark.net.cheesecloud.handle.ACTION_PAUSE_SUCCESS";
    /**
     * Start upload command
     */
    private static final String ACTION_START_ALL_UPLOAD         = "codingpark.net.cheesecloud.handle.ACTION_START_ALL_UPLOAD";
    /**
     * Resume all upload
     */
    private static final String ACTION_RESUME_ALL_UPLOAD        = "codingpark.net.cheesecloud.handle.ACTION_RESUME_ALL_UPLOAD";
    /**
     * Pause upload command
     */
    private static final String ACTION_PAUSE_ALL_UPLOAD         = "codingpark.net.cheesecloud.handle.ACTION_PAUSE_ALL_UPLOAD";
    /**
     * Cancel all upload command
     */
    private static final String ACTION_CANCEL_ALL_UPLOAD        = "codingpark.net.cheesecloud.handle.ACTION_CANCEL_ALL_UPLOAD";
    /**
     * Cancel target upload record command
     */
    private static final String ACTION_CANCEL_ONE_UPLOAD        = "codingpark.net.cheesecloud.handle.ACTION_CANCEL_ONE_UPLOAD";
    /**
     * Clear all upload record
     */
    private static final String ACTION_CLEAR_ALL_UPLOAD_RECORD  = "codingpark.net.cheesecloud.handle.ACTION_CLEAR_ALL_UPLOAD_RECORD";
    /**
     * As send extra data(UploadFile) to UploadService, use EXTRA_UPLOAD_FILE as key
     */
    public static final String EXTRA_UPLOAD_FILE                = "uploadfile";
    /**
     * As send ACTION_UPLOAD_STATE_CHANGE broadcast to receiver, add some extra
     * data(EVENT) to Intent, use EXTRA_UPLOAD_STATE as key.
     */
    public static final String EXTRA_UPLOAD_STATE               = "uploadstate";

    public static final int EVENT_UPLOAD_BLOCK_SUCCESS                  = 0;

    public static final int EVENT_UPLOAD_BLOCK_FAILED                   = 1;

    public static final int EVENT_RESUME_ALL_UPLOAD_SUCCESS             = 2;

    public static final int EVENT_RESUME_ALL_UPLOAD_FAILED              = 3;

    public static final int EVENT_PAUSE_ALL_UPLOAD_SUCCESS              = 4;

    public static final int EVENT_PAUSE_ALL_UPLOAD_FAILED               = 5;

    public static final int EVENT_CANCEL_ALL_UPLOAD_SUCCESS             = 6;

    public static final int EVENT_CANCEL_ALL_UPLOAD_FAILED              = 7;

    public static final int EVENT_CANCEL_ONE_UPLOAD_SUCCESS             = 8;

    public static final int EVENT_CANCEL_ONE_UPLOAD_FAILED              = 9;

    public static final int EVENT_CLEAR_ALL_UPLOAD_RECORD_SUCCESS       = 10;

    public static final int EVENT_CLEAR_ALL_UPLOAD_RECORD_FAILED        = 11;

    private UploadFileDataSource uploadFileDataSource   = null;

    private static ArrayList<UploadFile> mWaitDataList  = null;

    private static UploadTask mTask                     = null;

    private static Context mContext                     = null;

    /**
     * Starts this service to perform action ACTION_START_ALL_UPLOAD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     */
    public static void startActionUploadAll(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_START_ALL_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ACTION_START_ALL_UPLOAD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     */
    public static void startActionResumeAll(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_RESUME_ALL_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ACTION_PAUSE_ALL_UPLOAD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     */
    public static void startActionPauseAll(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_PAUSE_ALL_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ACTION_CANCEL_ALL_UPLOAD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     */
    public static void startActionCancelAll(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_CANCEL_ALL_UPLOAD);
        context.startService(intent);
    }


    /**
     * Starts this service to perform action ACTION_CANCEL_ONE_UPLOAD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     */
    public static void startActionCancelOne(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_CANCEL_ALL_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ACTION_CLEAR_ALL_UPLOAD_RECORD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     */
    public static void startActionClearAll(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_CLEAR_ALL_UPLOAD_RECORD);
        context.startService(intent);
    }

    public static void stopUploadService(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        context.stopService(intent);
    }


    private void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_ALL_UPLOAD.equals(action)) {
                handleActionStartAllUpload();
            } else if(ACTION_RESUME_ALL_UPLOAD.equals(action)) {
                handleActionResumeAllUpload();
            } else if (ACTION_PAUSE_ALL_UPLOAD.equals(action)) {
                handleActionPauseAllUpload();
            } else if (ACTION_CANCEL_ALL_UPLOAD.equals(action)) {
                handleActionCancelAllUpload();
            } else if (ACTION_CANCEL_ONE_UPLOAD.equals(action)) {
                handleActionCancelOneUpload();
            } else if (ACTION_CLEAR_ALL_UPLOAD_RECORD.equals(action)) {
                handleActionClearAllUploadRecord();
            }
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "UploadService created");
        if (mTask == null) {
            Log.d(TAG, "UploadTask is null, create new");
            mTask = new UploadTask();
        }
        Log.d(TAG, "Create UploadFileDataSource success");
        uploadFileDataSource = new UploadFileDataSource(this);
        uploadFileDataSource.open();
        // 1. Stop upload thread
        //handleActionPauseAllUpload();
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

    /**
     * Handle action ACTION_START_ALL_UPLOAD in the provided background thread with the provided
     * parameters.
     */
    private synchronized void handleActionStartAllUpload() {
        Log.d(TAG, "handle action start all action");
        // For sync, we stop upload thread first
        // 1. Pause mTask
        pauseUploadThread();
        // 2. Refresh mWaitDataList from local table upload_table
        refreshWaitData();
        // 3. Start mTask again
        startUploadThread();
    }

    /**
     * Handle action ACTION_RESUME_ALL_UPLOAD in the provided background thread with the provided
     * parameters.
     */
    private synchronized void handleActionResumeAllUpload() {
        Log.d(TAG, "handle resume all action");
        // For sync, we stop upload thread first
        // 1. Pause mTask
        pauseUploadThread();
        // 2. Update all pause state record to wait
        ArrayList<UploadFile> tmp_datas = uploadFileDataSource.getAllUploadFileByState(UploadFileState.PAUSE_UPLOAD);
        for (int i = 0; i < tmp_datas.size(); i++) {
            UploadFile file = tmp_datas.get(i);
            file.setState(UploadFileState.WAIT_UPLOAD);
            uploadFileDataSource.updateUploadFile(file);
        }
        // 3. Refresh mWaitDataList from local table upload_table
        refreshWaitData();
        // 4. Start mTask again
        startUploadThread();
        // 5. Send broadcast
        sendChangedBroadcast(EVENT_RESUME_ALL_UPLOAD_SUCCESS);
    }

    /**
     * Handle action ACTION_PAUSE_ALL_UPLOAD in the provided background thread with the provided
     * parameters.
     */
    private synchronized  void handleActionPauseAllUpload() {
        Log.d(TAG, "handle pause all action");
        // 1. Pause upload thread
        pauseUploadThread();
        // 2. Set all wait and uploading state record to pause state
        for (int i = 0; i < mWaitDataList.size(); i++) {
            UploadFile file = mWaitDataList.get(i);
            int state = file.getState();
            if (state == UploadFileState.UPLOADING || state == UploadFileState.WAIT_UPLOAD)
                file.setState(UploadFileState.PAUSE_UPLOAD);
            uploadFileDataSource.updateUploadFile(file);
        }
        sendChangedBroadcast(EVENT_PAUSE_ALL_UPLOAD_SUCCESS);
    }

    private synchronized void handleActionCancelAllUpload() {
        Log.d(TAG, "handle action cancel all upload");
        // 1. Pause upload thread
        pauseUploadThread();
        // 2. Delete uploading and wait state record from database
        // TODO Need delete rubbish records/datas from server
        uploadFileDataSource.deleteUploadFileByState(UploadFileState.UPLOADING);
        uploadFileDataSource.deleteUploadFileByState(UploadFileState.WAIT_UPLOAD);
        uploadFileDataSource.deleteUploadFileByState(UploadFileState.PAUSE_UPLOAD);
        // 3. Update mWaitDataList
        refreshWaitData();
        // 4. Send broadcast
        sendChangedBroadcast(EVENT_CANCEL_ALL_UPLOAD_SUCCESS);
    }

    private synchronized void handleActionCancelOneUpload() {
        Log.d(TAG, "handle action cancel one upload");
    }

    private synchronized void handleActionClearAllUploadRecord() {
        Log.d(TAG, "handle action clear all upload record");
        // 1. Pause upload thread
        pauseUploadThread();
        // 2. Delete uploaded state record from database
        uploadFileDataSource.deleteUploadFileByState(UploadFileState.UPLOADED);
        // 3. Send broadcast
        sendChangedBroadcast(EVENT_CLEAR_ALL_UPLOAD_RECORD_SUCCESS);
    }

    private void pauseUploadThread() {
        if (mTask != null && mTask.isAlive()) {
            mTask.interrupt();
            try {
                mTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Pause upload thread success");
        }
    }

    private void startUploadThread() {
        if (mTask == null) {
            mTask = new UploadTask();
        }
        if (mTask.isAlive()) {
            Log.d(TAG, "UploadThread is running, not need start again");
            return;
        }
        // If task stop and not NEW state, just create new UploadTask
        if (mTask.getState() != Thread.State.NEW) {
            Log.d(TAG, "upload thread have completed, new ThreadTask");
            mTask = null;
            mTask = new UploadTask();
        }
        Log.d(TAG, "Start uploading");
        // Start upload
        mTask.start();
    }

    private void refreshWaitData() {
        mWaitDataList = uploadFileDataSource.getNotUploadedFiles();
        //mWaitDataList = uploadFileDataSource.getAllUploadFile();
        Log.d(TAG, "refreshWaitData: mWaitDataList.size = " + mWaitDataList.size());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "UploadService destroy[Close UploadFileDataSource]");
        if (uploadFileDataSource != null) {
            uploadFileDataSource.close();
        }
        if (mTask != null && mTask.isAlive()) {
            mTask.interrupt();
            try {
                mTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendChangedBroadcast(UploadFile file, int event) {
        Intent intent = new Intent(ACTION_UPLOAD_STATE_CHANGE);
        intent.putExtra(EXTRA_UPLOAD_FILE, file);
        intent.putExtra(EXTRA_UPLOAD_STATE, event);
        getApplicationContext().sendBroadcast(intent);
        Log.d(TAG, "Send upload state changed broadcast with file: " + event);
    }

    private void sendChangedBroadcast(int event) {
        Intent intent = new Intent(ACTION_UPLOAD_STATE_CHANGE);
        intent.putExtra(EXTRA_UPLOAD_STATE, event);
        getApplicationContext().sendBroadcast(intent);
        Log.d(TAG, "Send upload state changed broadcast without file: " + event);
    }

    /**
     * Scan tree root
     */
    /*
    private void root_upload() {
        Log.d(TAG, "Start root_upload");
        List<UploadFile> rootFileList = uploadFileDataSource.getNotUploadedRootFiles();
        int result = WsResultType.Success;

        // 1. First select the root and uploading root to upload
        for (UploadFile file : rootFileList) {
            if (file.getLocal_user_id() == -2) {
                // The root is file, upload directly
                if (file.getFileType() == CloudFileType.TYPE_FILE) {
                    result = startUploading(file);
                    if (result != WsResultType.Success)
                        return;
                    break;
                }
                // The root is folder
                else if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
                    result = upload(file);
                }
                if (result != WsResultType.Success) return;
                // Upload root success, update root local_parent_id to -3
                file.setLocal_user_id(-3);
                uploadFileDataSource.updateUploadFile(file);
                break;
            }
        }
        // 2. Traverse fileList the NOT_UPLOAD file
        for (UploadFile file : rootFileList) {
            if (file.getParent_id() == -1) {
                result = startUploading(file);
                if (result != WsResultType.Success) return;
                // Update the root file state to -2
                file.setParent_id(-2);
                uploadFileDataSource.updateUploadFile(file) ;
                result = upload(file);
                if (result != WsResultType.Success) return;
                file.setParent_id(-3);
                uploadFileDataSource.updateUploadFile(file);
            }
        }
    }
    */

    /**
     * Scan the tree
     * @param file
     * @return
     */
    /*
    private int upload(UploadFile file) {
        int result = WsResultType.Success;
        // If the target is file, upload directly
        if (file.getFileType() == CloudFileType.TYPE_FILE) {
            if (file.getState() != UploadFileState.UPLOADED)
                result = startUploading(file);
        }
        // If the target is folder, upload sub files recursion
        else {
            // Create the folder, remember the folder's local id
            if (file.getState() != UploadFileState.UPLOADED) {
                result = startUploading(file);
                if (result != WsResultType.Success)
                    return result;
            }
            // Get sub files
            List<UploadFile> uploadFileList = uploadFileDataSource.getSubUploadFiles(file);
            for (UploadFile uFile : uploadFileList) {
                if (uFile.getFileType() == CloudFileType.TYPE_FOLDER) {
                }
                if (uFile.getState() != UploadFileState.UPLOADED) {
                    // Update the sub files remote parent id
                    uFile.setRemote_parent_id(file.getRemote_id());
                    result = startUploading(uFile);
                    if (result == WsResultType.Success) {
                        if (uFile.getFileType() == CloudFileType.TYPE_FOLDER) {
                            result = upload(file);
                        }
                    }
                }
            }
        }
        return result;
    }
    */


    /**
     * Scan node
     * @param file
     * @return
     */
    /*
    private int startUploading(UploadFile file) {
        int result = WsResultType.Success;
        byte[] buffer;
        if (file.getFileType() == CloudFileType.TYPE_FILE) {
            if (file.getState() == UploadFileState.NOT_UPLOAD) {
                result = ClientWS.getInstance(mContext).checkedFileInfo_wrapper(file);
                // Call WS occur error
                if (result < 0)
                    return result;
                // Update to database
                uploadFileDataSource.updateUploadFile(file);
                result = WsResultType.Success;
                if (result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD) {
                    sendChangedBroadcast(file, EVENT_UPLOAD_BLOCK_SUCCESS);
                    return result;
                }
            }
            //Upload block one by one
            buffer = new byte[UPLOAD_BLOCK_SIZE];
            File r_file = new File(file.getFilePath());
            int count = 0;
            try {
                while (true) {
                    //FileInputStream stream = new FileInputStream(r_file);
                    RandomAccessFile stream = new RandomAccessFile(r_file, "r");
                    Log.d(TAG, "Array size:" + buffer.length + "\n" + "uploadedsize: " + (int)file.getChangedSize());
                    stream.seek(file.getChangedSize());
                    count = stream.read(buffer, 0, UPLOAD_BLOCK_SIZE);
                    if (count != -1) {
                        result = uploadFile_wrapper(file, buffer, count);
                        if (result != WsResultType.Success) {
                            sendChangedBroadcast(file, EVENT_UPLOAD_BLOCK_FAILED);
                            return result;
                        }
                        // Increase index
                        file.setChangedSize(file.getChangedSize() + count);
                        // Upload completed
                        if (file.getChangedSize() == file.getFileSize()) {
                            file.setState(UploadFileState.UPLOADED);
                        }
                        // Update to database
                        uploadFileDataSource.updateUploadFile(file);
                        sendChangedBroadcast(file, EVENT_UPLOAD_BLOCK_SUCCESS);
                    } else
                        break;// Upload completed
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return WsResultType.Faild;
            } catch (IOException e) {
                e.printStackTrace();
                return WsResultType.Faild;
            }
        } else if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
            result = ClientWS.getInstance(mContext).createFolderUpload_wrapper(file);
            if (result == WsResultType.Success) {
                // Update database
                file.setState(UploadFileState.UPLOADED);
                uploadFileDataSource.updateUploadFile(file);
            }
        }
        return result;
    }
    */

    private int uploadFile_wrapper(UploadFile file, byte[] buf, int size) {
        int result = 0;
        WsSyncFile wsFile = new WsSyncFile();
        wsFile.ID = file.getRemote_id();
        if (file.getFileSize() == (file.getChangedSize() + size)) {
            wsFile.IsFinally = true;
            byte[] r_buf = new byte[size];
            System.arraycopy(buf, 0, r_buf, 0, size);
            buf = r_buf;
        }
        wsFile.Blocks = new SyncFileBlock();
        wsFile.Blocks.OffSet = file.getChangedSize();
        wsFile.Blocks.UpdateData = buf;
        wsFile.Blocks.SourceSize = size;
        result = ClientWS.getInstance(UploadService.this).uploadFile(wsFile);
        return result;
    }


    /**
     * Upload file block thread
     */
    private class UploadTask extends Thread {
        private int mTry = 0;
        @Override
        public void run() {
            for (int i = 0; i < mWaitDataList.size(); i++) {
                int result = WsResultType.Success;
                mTry = 0;
                UploadFile file = mWaitDataList.get(i);
                // Update file state to UPLOADING
                if (file.getState() == UploadFileState.WAIT_UPLOAD) {
                    file.setState(UploadFileState.UPLOADING);
                    uploadFileDataSource.updateUploadFile(file);
                }
                //Upload block one by one
                byte[] buffer = new byte[UPLOAD_BLOCK_SIZE];
                File r_file = new File(file.getFilePath());
                int count = 0;
                try {
                    while (true) {
                        //FileInputStream stream = new FileInputStream(r_file);
                        RandomAccessFile stream = new RandomAccessFile(r_file, "r");
                        Log.d(TAG, "Array size:" + buffer.length + "\n" + "uploadedsize: " + (int)file.getChangedSize());
                        stream.seek(file.getChangedSize());
                        count = stream.read(buffer, 0, UPLOAD_BLOCK_SIZE);
                        if (count != -1) {
                            if (isInterrupted()) {
                                Log.d(TAG, "isInterrupted");
                                return;
                            }
                            result = uploadFile_wrapper(file, buffer, count);
                            if (result != WsResultType.Success) {
                                sendChangedBroadcast(file, EVENT_UPLOAD_BLOCK_FAILED);
                                if (mTry < MAX_RETRY_TIME) {
                                    Log.d(TAG, "Upload failed, retry: " + mTry);
                                    mTry++;
                                    continue;
                                }
                                else {
                                    Log.d(TAG, "Upload failed, reach limit:" + MAX_RETRY_TIME + "\t" + " stop: " + file.getFilePath());
                                    break;
                                }
                            }
                            mTry = 0;
                            // Increase index
                            file.setChangedSize(file.getChangedSize() + count);
                            // Upload completed
                            if (file.getChangedSize() == file.getFileSize()) {
                                file.setState(UploadFileState.UPLOADED);
                            }
                            // Update to database
                            uploadFileDataSource.updateUploadFile(file);
                            sendChangedBroadcast(file, EVENT_UPLOAD_BLOCK_SUCCESS);
                        } else
                            break;// Upload completed
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















