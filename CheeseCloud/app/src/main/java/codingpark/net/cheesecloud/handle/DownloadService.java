package codingpark.net.cheesecloud.handle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service download file from remote server
 */
public class DownloadService extends Service {
    private static final String ACTION_FOO = "codingpark.net.cheesecloud.handle.action.FOO";
    private static final String ACTION_BAZ = "codingpark.net.cheesecloud.handle.action.BAZ";


    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_FOO);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_BAZ);
        context.startService(intent);
    }


    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                handleActionFoo();
            } else if (ACTION_BAZ.equals(action)) {
                handleActionBaz();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
