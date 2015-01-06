package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;

/**
 * Home page, show welcome information and provide login and
 * about action entrance.
 * Login entrance: Start LoginActivity
 * About entrance: Start HelpActivity
 */
public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";

    private Button about_bt         = null;
    private Button loginin_bt       = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);

        // Hide action bar and status bar
        // TODO Android 4.1 later valid, 4.1 before need solve by other method
        //if(android.os.Build.VERSION.SDK_INT >= 16) {
            // Welcome page don't need status bar and action bar
            //View decorView = getWindow().getDecorView();
            // Hide the status bar.
            //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            //decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            //ActionBar actionBar = getActionBar();
            //actionBar.hide();
        //}

        setContentView(R.layout.activity_welcome);

        // Initial sign in and login in button
        about_bt = (Button)findViewById(R.id.welcome_about_bt);
        loginin_bt = (Button)findViewById(R.id.welcome_login_bt);

        // Set about button click listener
        about_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Welcome about button clicked!");
                Intent intent = new Intent(WelcomeActivity.this, HelpActivity.class);
                WelcomeActivity.this.startActivity(intent);
                //WelcomeActivity.this.finish();
            }
        });

        // Set login in button click listener
        loginin_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Welcome login in button clicked!");
                // Start LoginActivity to handle login process
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        });

        // Judge user is login, False: need enter password again; True: auto login in
        boolean login = sp.getBoolean(AppConfigs.PREFS_LOGIN, false);
        if (login) {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            WelcomeActivity.this.startActivity(intent);
            WelcomeActivity.this.finish();
        }

    }

}
