package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.ClientWS;

public class SelectPathActivity extends Activity {
    private static final String TAG     = "SelectPathActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);

        // Initial ActionBar
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.select_path_activity_action_bar_title);

        Button test_userLogin_bt = (Button)findViewById(R.id.test_userLogin_bt);
        test_userLogin_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "*****Test UserLogin******");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClientWS.getInstance().test_userLogin();
                    }
                });
                t.start();
            }
        });

        Button test_checkedFileInfo_bt = (Button)findViewById(R.id.test_checkedFileInfo_bt);
        test_checkedFileInfo_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "*****Test CheckedFileInfo******");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClientWS.getInstance().test_checkedFileInfo("/sdcard/wiki.amr");
                    }
                });
                t.start();
            }
        });

        Button test_uploadFile_bt = (Button)findViewById(R.id.test_uploadFile_bt);
        test_uploadFile_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "*****Test UploadFile******");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClientWS.getInstance().test_uploadFile("/sdcard/wiki.amr");
                    }
                });
                t.start();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_path, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
