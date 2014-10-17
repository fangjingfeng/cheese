package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.LocalDatabase;


public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";

    private ImageButton signin_bt        = null;
    private ImageButton loginin_bt       = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Android 4.1 later valid, 4.1 before need solve by other method
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            // Welcome page don't need status bar and action bar
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }

        setContentView(R.layout.activity_welcome);

        // Initial sign in and login in button
        signin_bt = (ImageButton)findViewById(R.id.welcome_sign_bt);
        loginin_bt = (ImageButton)findViewById(R.id.welcome_login_bt);

        // Set sign in button click listener
        signin_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Welcome sign in button clicked!");
                // TODO Handle sign in action
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        });

        // Set login in button click listener
        loginin_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Welcome login in button clicked!");
                // TODO Handle login in action
                LocalDatabase ldb = new LocalDatabase(WelcomeActivity.this);
                ldb.getWritableDatabase();
                ldb.close();
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        });
    }

}
