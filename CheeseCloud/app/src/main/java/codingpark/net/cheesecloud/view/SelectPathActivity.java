package codingpark.net.cheesecloud.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.ClientWS;

public class SelectPathActivity extends ListActivity {
    private static final String TAG     = "SelectPathActivity";

    private Button select_path_cancel_bt    = null;
    private Button select_path_ok_bt        = null;

    public static final String RESULT_SELECTED_REMOTE_PARENT_ID = "";
    // TODO The value should fetch from server dynamic
    private String remote_parent_id         = "395ED821-E528-42F0-8EA7-C59F258E7435";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);

        // Initial ActionBar
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.select_path_activity_action_bar_title);

        initUI();
        initHandler();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ClientWS.getInstance(SelectPathActivity.this).test_getDisk();
                ClientWS.getInstance(SelectPathActivity.this).test_getFolderList();
            }
        });
        t.start();
    }

    private void initUI() {
        select_path_cancel_bt = (Button) findViewById(R.id.select_upload_path_cancel_bt);
        select_path_ok_bt = (Button) findViewById(R.id.select_upload_path_ok_bt);
    }

    private void initHandler() {
        select_path_cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel select path action!");
                setResult(RESULT_CANCELED);
                SelectPathActivity.this.finish();
            }
        });

        select_path_ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Select this path!");
                // Return selected remote parent id to UploadActivity
                Intent intent = new Intent();
                intent.putExtra(RESULT_SELECTED_REMOTE_PARENT_ID, remote_parent_id);
                setResult(RESULT_OK, intent);
                SelectPathActivity.this.finish();
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
                setResult(RESULT_CANCELED);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

}
