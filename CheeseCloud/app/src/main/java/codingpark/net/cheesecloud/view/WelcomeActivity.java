package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.LocalDatabase;


public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";

    private Button about_bt = null;
    private Button loginin_bt       = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Android 4.1 later valid, 4.1 before need solve by other method
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            // Welcome page don't need status bar and action bar
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            //decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            //ActionBar actionBar = getActionBar();
            //actionBar.hide();
        }

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
