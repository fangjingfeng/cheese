package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
    private int count =0;

	private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
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
        
        SharedPreferences sp1 = this.getSharedPreferences("testshortcut", MODE_PRIVATE);
	  	editor = sp1.edit();
	  	// 读取SharedPreferences对象中键为count的值
	  	int readCount = sp1.getInt("count", 0);
	  	if (readCount>0) {
	  		
	  	}else{
	  		//createShortCut();
	  	}
    }

    /**
  	 * 创建快捷方式
  	 */
  	private void createShortCut() {
  		Intent intent = new Intent();
  		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
  		//  1\ 干什么事 2、叫什么名3\长什么样
  		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "校园云盘"); // 叫什么名
  		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.cheesecloud_icon)); // 长什么样
        Intent doWhat = new Intent(this,WelcomeActivity.class);// 此处不能用显式意图，只能用隐式意图
  		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhat);// 快捷方式，是干什么事的
  		intent.putExtra("duplicate", false); // 不允许 图标重复 
  		editor.putInt("count", count);
  		sendBroadcast(intent);
  	}
}
