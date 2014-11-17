package codingpark.net.cheesecloud.handle;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.wsi.SyncFileBlock;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsFolder;
import codingpark.net.cheesecloud.wsi.WsSyncFile;

/**
 * An {@link IntentService} subclass for handling asynchronous upload
 * task requests in a service on a separate handler thread.
 */
public class UploadService extends IntentService {
    public static final String TAG      = UploadService.class.getSimpleName();

    /**
     * The upload block size in byte unit
     * Default size 100KB
     */
    public static final int UPLOAD_BLOCK_SIZE           = 100 * CheeseConstants.KB;

    /**
     * Start upload command
     */
    private static final String ACTION_START_UPLOAD     = "codingpark.net.cheesecloud.handle.ACTION_START_UPLOAD";
    /**
     * Pause upload command
     */
    private static final String ACTION_PAUSE_UPLOAD     = "codingpark.net.cheesecloud.handle.ACTION_PAUSE_UPLOAD";

    public static final String ACTION_UPLOAD_STATE_CHANGE       = "codingpark.net.cheesecloud.handle.ACTION_PAUSE_SUCCESS";

    public static final String EXTRA_UPLOAD_FILE                = "uploadfile";

    public static final String EXTRA_UPLOAD_STATE               = "uploadstate";

    public static final int EVENT_UPLOAD_BLOCK_SUCCESS                  = 0;

    public static final int EVENT_UPLOAD_BLOCK_FAILED                   = 1;

    public static final int EVENT_PAUSE_UPLOAD_SUCCESS                  = 2;

    public static final int EVENT_PAUSE_UPLOAD_FAILED                   = 3;

    public static final int EVENT_CANCEL_UPLOAD_SUCCESS                 = 4;

    public static final int EVENT_CANCEL_UPLOAD_FAILED                  = 5;

    private UploadFileDataSource uploadFileDataSource   = null;

    private ArrayList<UploadFile> mWaitTask                             = null;

    private static UploadTask mTask            = null;

    /**
     * Starts this service to perform action ACTION_START_UPLOAD with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     * @see IntentService
     */
    public static void startActionUpload(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_START_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ACTION_START_PAUSE with the
     * given parameters. If the service is already performing a task this
     * action will be queued.
     * @see IntentService
     */
    public static void startActionPause(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_PAUSE_UPLOAD);
        context.startService(intent);
    }

    public UploadService() {
        super("UploadService");
        if (mTask != null)
            mTask = new UploadTask();
        //Context c = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_UPLOAD.equals(action)) {
                handleActionStartUpload();
            } else if (ACTION_PAUSE_UPLOAD.equals(action)) {
                handleActionPauseUpload();
            }
        }
    }

    /**
     * Handle action ACTION_START_UPLOAD in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStartUpload() {
        Log.d(TAG, "handle action start upload");
        uploadFileDataSource = new UploadFileDataSource(this);
        uploadFileDataSource.open();
        Log.d(TAG, "Start uploading");
        root_upload();
    }

    /**
     * Handle action ACTION_PAUSE_UPLOAD in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPauseUpload() {
        Log.d(TAG, "handle action pause upload");
        Log.d(TAG, "Pause uploading");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "UploadService destroy");
        if (uploadFileDataSource != null) {
            uploadFileDataSource.close();
        }
        super.onDestroy();
    }

    private void sendChangedBroadcast(UploadFile file, int event) {
        Intent intent = new Intent(ACTION_UPLOAD_STATE_CHANGE);
        intent.putExtra(EXTRA_UPLOAD_FILE, file);
        intent.putExtra(EXTRA_UPLOAD_STATE, event);
        getApplicationContext().sendBroadcast(intent);
        Log.d(TAG, "Send upload state changed broadcast: " + event);
    }

    /**
     * Scan tree root
     */
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

    /**
     * Scan the tree
     * @param file
     * @return
     */
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


    /**
     * Scan node
     * @param file
     * @return
     */
    private int startUploading(UploadFile file) {
        int result = WsResultType.Success;
        byte[] buffer;
        if (file.getFileType() == CloudFileType.TYPE_FILE) {
            if (file.getState() == UploadFileState.NOT_UPLOAD) {
                result = this.checkedFileInfo_wrapper(file);
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
            result = createFolder_wrapper(file);
            if (result == WsResultType.Success) {
                // Update database
                file.setState(UploadFileState.UPLOADED);
                uploadFileDataSource.updateUploadFile(file);
            }
        }
        return result;
    }

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
     * Get current date format string
     * @return
     *  String: current date string, such as 2014/10/17 16:44:23
     */
    private String getDateString() {
        return DateFormat.format("yyyy/MM/dd HH:mm:ss", new Date(System.currentTimeMillis())).toString();
    }
    private int checkedFileInfo_wrapper(UploadFile file) {
        int result;
        WsFile wsFile = new WsFile();
        String path = file.getFilePath();
        File r_file = new File(path);
        wsFile.CreaterID = AppConfigs.current_remote_user_id;
        wsFile.FatherID = file.getRemote_parent_id();
        wsFile.Extend = path.substring(path.lastIndexOf(".") + 1);
        wsFile.SizeB = r_file.length();
        wsFile.FullName = r_file.getName();
        wsFile.CreatDate = getDateString();
        try {
            wsFile.MD5 = FileManager.generateMD5(new FileInputStream(r_file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        result = ClientWS.getInstance(this).checkedFileInfo(wsFile);
        // Update UploadFile.remote_id
        if (result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD) {
            file.setRemote_id(wsFile.ID);
            file.setState(UploadFileState.UPLOADED);
            file.setChangedSize(file.getFileSize());
        } else if (result == CheckedFileInfoResultType.RESULT_CHECK_SUCCESS) {
            file.setRemote_id(wsFile.ID);
            file.setState(UploadFileState.WAIT_UPLOAD);
        }
        return result;
    }

    private int createFolder_wrapper(UploadFile file) {
        int result;
        WsFolder wsFolder = new WsFolder();
        wsFolder.FatherID = file.getRemote_parent_id();
        File r_file = new File(file.getFilePath());
        wsFolder.Name = r_file.getName();
        result = ClientWS.getInstance(this).createFolder(wsFolder);
        return result;
    }

    private class UploadTask extends Thread {
        @Override
        public void run() {
            while (true) {
                Log.d(TAG, "UploadTask run, start sleep 5s");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}















