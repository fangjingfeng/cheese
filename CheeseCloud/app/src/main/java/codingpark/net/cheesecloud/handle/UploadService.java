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
import java.util.Date;
import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.eumn.CheckedFileInfoType;
import codingpark.net.cheesecloud.eumn.UploadFileState;
import codingpark.net.cheesecloud.eumn.UploadFileType;
import codingpark.net.cheesecloud.eumn.WsResultType;
import codingpark.net.cheesecloud.model.UploadFile;
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
        Log.d(TAG, "Pause uploading");
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
                if (file.getFiletype() == UploadFileType.TYPE_FILE) {
                    result = startUploading(file);
                    if (result != WsResultType.Success)
                        return;
                    break;
                }
                // The root is folder
                else if (file.getFiletype() == UploadFileType.TYPE_FOLDER) {
                    result = upload(file);
                }
                if (result != WsResultType.Success) return;
                // Upload root success, update root local_parent_id to -3
                file.setLocal_user_id(-3);
                uploadFileDataSource.updateUploadFile(file);
                break;
            }
        }
        // 2. Traverse fileList the NotUpload file
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
        if (file.getFiletype() == UploadFileType.TYPE_FILE) {
            if (file.getState() != UploadFileState.Uploaded)
                result = startUploading(file);
        }
        // If the target is folder, upload sub files recursion
        else {
            // Create the folder, remember the folder's local id
            if (file.getState() != UploadFileState.Uploaded) {
                result = startUploading(file);
                if (result != WsResultType.Success)
                    return result;
            }
            // Get sub files
            List<UploadFile> uploadFileList = uploadFileDataSource.getSubUploadFiles(file);
            for (UploadFile uFile : uploadFileList) {
                if (uFile.getFiletype() == UploadFileType.TYPE_FOLDER) {
                }
                if (uFile.getState() != UploadFileState.Uploaded) {
                    // Update the sub files remote parent id
                    uFile.setRemote_parent_id(file.getRemote_id());
                    result = startUploading(uFile);
                    if (result == WsResultType.Success) {
                        if (uFile.getFiletype() == UploadFileType.TYPE_FOLDER) {
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
        if (file.getFiletype() == UploadFileType.TYPE_FILE) {
            if (file.getState() == UploadFileState.NotUpload) {
                result = this.checkedFileInfo_wrapper(file);
                if (result < 0)
                    return result;
                // Update to database
                uploadFileDataSource.updateUploadFile(file);
            }
            //Upload block one by one
            buffer = new byte[UPLOAD_BLOCK_SIZE];
            File r_file = new File(file.getFilepath());
            int count = 0;
            try {
                while (true) {
                    FileInputStream stream = new FileInputStream(r_file);
                    Log.d(TAG, "Array size:" + buffer.length + "\n" + "uploadedsize: " + (int)file.getUploadedsize());
                    count = stream.read(buffer, (int)file.getUploadedsize(), UPLOAD_BLOCK_SIZE);
                    if (count != -1) {
                        result = uploadFile_wrapper(file, buffer, count);
                        if (result != WsResultType.Success)
                            return result;
                        // Increase index
                        file.setUploadedsize(file.getUploadedsize() + count);
                        // Upload completed
                        if (file.getUploadedsize() == file.getFilesize()) {
                            file.setState(UploadFileState.Uploaded);
                        }
                        // Update to database
                        uploadFileDataSource.updateUploadFile(file);
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
        } else if (file.getFiletype() == UploadFileType.TYPE_FOLDER) {
            result = createFolder_wrapper(file);
            if (result == WsResultType.Success) {
                // Update database
                file.setState(UploadFileState.Uploaded);
                uploadFileDataSource.updateUploadFile(file);
            }
        }
        return result;
    }

    private int uploadFile_wrapper(UploadFile file, byte[] buf, int size) {
        int result = 0;
        WsSyncFile wsFile = new WsSyncFile();
        wsFile.ID = file.getRemote_id();
        if (file.getFilesize() == (file.getUploadedsize() + size)) {
            wsFile.IsFinally = true;
            byte[] r_buf = new byte[size];
            System.arraycopy(buf, 0, r_buf, 0, size);
            buf = r_buf;
        }
        wsFile.Blocks = new SyncFileBlock();
        wsFile.Blocks.OffSet = file.getUploadedsize();
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
        result = ClientWS.getInstance(this).checkedFileInfo(wsFile);
        // Update UploadFile.remote_id
        if (result == CheckedFileInfoType.RESULT_QUICK_UPLOAD) {
            file.setRemote_id(wsFile.ID);
            file.setState(UploadFileState.Uploaded);
            file.setUploadedsize(file.getFilesize());
        } else if (result == CheckedFileInfoType.RESULT_CHECK_SUCCESS) {
            file.setRemote_id(wsFile.ID);
            file.setState(UploadFileState.Uploading);
        }
        return result;
    }

    private int createFolder_wrapper(UploadFile file) {
        int result;
        WsFolder wsFolder = new WsFolder();
        wsFolder.FatherID = file.getRemote_parent_id();
        File r_file = new File(file.getFilepath());
        wsFolder.Name = r_file.getName();
        result = ClientWS.getInstance(this).createFolder(wsFolder);
        return result;
    }
}















