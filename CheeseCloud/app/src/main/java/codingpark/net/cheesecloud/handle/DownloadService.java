package codingpark.net.cheesecloud.handle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import codingpark.net.cheesecloud.CheeseConstants;

/**
 * The service download file from remote server
 */
public class DownloadService extends Service {


    /**
     * The download block size in byte unit
     * Default size 100KB
     */
    public static final int DOWNLOAD_BLOCK_SIZE             = 100 * CheeseConstants.KB;

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
     * user can click clear all button , then trigger send ACTION_CLEAR_ALL_DOWNLOAD
     * action to DownloadService, DownloadService will stop current download thread
     * and remove the  download record which state is downloaded  in download table.
     * In addition, DownloadService update wait list.
     */
    public static final String ACTION_CLEAR_ALL_DOWNLOAD = "codingpark.net.cheesecloud.handle.ACTION_CLEAR_ALL_DOWNLOAD";
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
            if (ACTION_CANCEL_ALL_DOWNLOAD.equals(action)) {
                return;
            } else if (ACTION_CANCEL_ONE_DOWNLOAD.equals(action)) {
                return;
            } else if (ACTION_CLEAR_ALL_DOWNLOAD.equals(action)) {
                return;
            } else if (ACTION_PAUSE_ALL_DOWNLOAD.equals(action)) {
                return;
            } else if (ACTION_START_ALL_DOWNLOAD.equals(action)) {
                return;
            } else if (ACTION_START_ALL_DOWNLOAD.equals(action)) {
                return;
            }
        }
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
     * Call this function to send ACTION_CLEAR_ALL_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionClearAll(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_CLEAR_ALL_DOWNLOAD);
        context.startService(intent);
    }

    /**
     * Call this function to send ACTION_PAUSE_ALL_DOWNLOAD to DownloadService.
     *
     * @param context    The application context
     */
    public static void startActionPauseAll(Context context){
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

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
