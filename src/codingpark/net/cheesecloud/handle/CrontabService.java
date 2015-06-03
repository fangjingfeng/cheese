package codingpark.net.cheesecloud.handle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.wsi.WsGuidOwner;

/**
 * Created by ethanshan on 15-1-7.
 * "Crontab" name is from linux schedule task configure. CrontabService
 * is responsible for execute task repeated.
 * Schedule task:
 *  1) Call login web service every 2 minutes
 */
public class CrontabService extends Service {

    private static final String TAG = CrontabService.class.getSimpleName();

    /**
     * The time internal to call login web service
     */
    private static final int RENEWAL_INTERNAL_SECONDS = 60000;

    /**
     * As start renewal schedule task, use LOGIN_USERNAME_KEY to fetch login username from
     * received Intent.
     */
    public static final String LOGIN_USERNAME_KEY       = "codingpark.net.cheesecloud.handle.login_username_key";

    /**
     * As start renewal schedule task, use LOGIN_PASSWORD_KEY to fetch login password from
     * received Intent.
     */
    public static final String LOGIN_PASSWORD_KEY       = "codingpark.net.cheesecloud.handle.login_password_key";

    /**
     * Relogin command
     */
    public static final String START_RENEWAL_ACTION         = "codingpark.net.cheesecloud.handle.START_RENEWAL_ACTION";

    /**
     * Stop CrontabService action
     */
    public static final String STOP_SERVICE_ACTION          = "codingpark.net.cheesecloud.handle.STOP_SERVICE_ACTION";

    private Thread renewalThread = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	String action=null;
    	if(intent!=null){
    		action= intent.getAction();
	        String username = intent.getStringExtra(LOGIN_USERNAME_KEY);
	        String password = intent.getStringExtra(LOGIN_PASSWORD_KEY);
	        // Start relogin execute task
	        if (action.equals(START_RENEWAL_ACTION)) {
	            if (renewalThread == null || renewalThread.getState() == Thread.State.TERMINATED)
	                renewalThread = new Thread(new LoginRunnable(username, password));
	            if (renewalThread.getState() == Thread.State.NEW)
	                renewalThread.start();
	        }
	        // Stop relogin task
	        else if (action.equals(STOP_SERVICE_ACTION)) {
	            if (renewalThread != null) {
	                renewalThread.interrupt();
	                try {
	                    renewalThread.join();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                stopSelf();
	            }
	        }
    	}
        return super.onStartCommand(intent, flags, startId);
    }

    public static void stopCrontabService(Context context)
    {
        Intent intent = new Intent(context, CrontabService.class);
        intent.setAction(STOP_SERVICE_ACTION);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static int mFailTime        = 0;


    private class LoginRunnable implements Runnable {

        public static final int MAX_RETRY_TIME      = 3;

        private String mUsername    = "";
        private String mPassword    = "";

        /**
         * The constructor
         * @param username The user name(Email)
         * @param password The user password
         */
        public LoginRunnable(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (Thread.currentThread().isInterrupted())
                        return;
                    int result = ClientWS.getInstance(CrontabService.this).userLogin(mUsername, mPassword, new WsGuidOwner());
                    Log.d(TAG, "LoginRunnable: result = " + result);
                    if (result == WsResultType.Success) {
                        Thread.sleep(RENEWAL_INTERNAL_SECONDS);
                    } else {
                        // TODO As renewal retry time reach limit, need broadcast error message
                        if (mFailTime >= MAX_RETRY_TIME)
                            return;
                        else
                            mFailTime++;
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
