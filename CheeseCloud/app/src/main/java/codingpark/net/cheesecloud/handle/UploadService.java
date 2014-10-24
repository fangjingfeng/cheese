package codingpark.net.cheesecloud.handle;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.eumn.UploadFileState;
import codingpark.net.cheesecloud.eumn.UploadFileType;
import codingpark.net.cheesecloud.eumn.WsResultType;
import codingpark.net.cheesecloud.model.UploadFile;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.wsi.WsFile;

/**
 * An {@link IntentService} subclass for handling asynchronous upload
 * task requests in a service on a separate handler thread.
 */
public class UploadService extends IntentService {
    public static final String TAG      = UploadService.class.getSimpleName();

    /**
     * The upload block size in byte unit
     * Default size 4KB
     */
    public static final int UPLOAD_BLOCK_SIZE           = 4096;

    /**
     * Start upload command
     */
    private static final String ACTION_START_UPLOAD     = "codingpark.net.cheesecloud.handle.ACTION_START_UPLOAD";
    /**
     * Pause upload command
     */
    private static final String ACTION_PAUSE_UPLOAD     = "codingpark.net.cheesecloud.handle.ACTION_PAUSE_UPLOAD";

    private UploadFileDataSource uploadFileDataSource   = null;

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
        Context c = getApplicationContext();
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
        uploadFileDataSource = new UploadFileDataSource(this);
        uploadFileDataSource.open();
        Log.d(TAG, "Start uploading");
    }

    /**
     * Handle action ACTION_PAUSE_UPLOAD in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPauseUpload() {
        Log.d(TAG, "Pause uploading");
    }

    private void root_upload() {
        List<UploadFile> fileList = uploadFileDataSource.getNotUploadedRootFiles();
        int result = WsResultType.Success;

        // 1. First select the uploading root to upload
        for (UploadFile file : fileList) {
            if (file.getState() == UploadFileState.Uploading) {
                if (file.getFiletype() == UploadFileType.TYPE_FILE) {

                }
                if (upload(file) == WsResultType.Success) {
                    //file.set
                    uploadFileDataSource.updateUploadFile(file);
                }
            }
            if (result == WsResultType.Success) {

            }
        }
        // 2. Traverse fileList
        for (UploadFile file : fileList) {
            if (file.getState() == UploadFileState.NotUpload) {
                // Create
            }
                upload(file);
        }
    }

    private int upload(UploadFile file) {
        return WsResultType.Success;
    }


    private int startUploading(UploadFile file) {
        if (file.getFiletype() == UploadFileType.TYPE_FILE) {
            if (file.getState() == UploadFileState.NotUpload) {
                this.checkedFileInfo_wrapper(file);
            }
        } else if (file.getFiletype() == UploadFileType.TYPE_FOLDER) {

        }
        return WsResultType.Success;
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
        WsFile wsFile = new WsFile();
        String path = file.getFilepath();
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
        return ClientWS.getInstance(this).checkedFileInfo(wsFile);
    }
}















