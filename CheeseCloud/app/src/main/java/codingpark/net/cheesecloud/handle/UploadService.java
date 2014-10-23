package codingpark.net.cheesecloud.handle;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

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

    }

}
