package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.UploadFile;

/**
 * The class display my cloud and resource library folder and files.
 * CloudFilesActivity have 2 mode:
 * MY_CLOUD_LIST_MODE: Initial the my cloud disk as root folder, and display the
 * subdirectory one by one.
 * RESOURCELIB_LIST_MODE: Display all disks at initial exclude my cloud disk.
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 18:12:41
 */
public class CloudFilesActivity extends ListActivity {
    private static final String TAG         = CloudFilesActivity.class.getSimpleName();

    /**
     * The key of CloudFilesActivity display files mode
     */
    public static final String LIST_MODE_KEY        = "list_mode_key";
    /**
     * List user owned cloud folder files
     */
    public static final int MY_CLOUD_LIST_MODE      = 0;
    /**
     * List cloud shared disks
     */
    public static final int RESOURCELIB_LIST_MODE   = 1;
    /**
     * Current list mode
     */
    private static int mListMode            = MY_CLOUD_LIST_MODE;

    public static final String NULL_ID      = "/";


    private ArrayList<String> mFolderNameList           = null;
    private ArrayList<CloudFile> mFolderList            = null;
    private Stack<CloudFile> mPathStack                 = null;
    // Path bar, use to show current directory path
    private LinearLayout path_bar_container = null;
    // List adapter
    private CloudListAdapter mAdapter                       = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        // Set action bar show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_cloud_files);

        // Get the initial list mode
        Intent recIntent = getIntent();
        mListMode = recIntent.getIntExtra(LIST_MODE_KEY, MY_CLOUD_LIST_MODE);

        // Initial list adapter
        //mAdapter = new CloudListAdapter(this, )
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_files, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Handle action bar event
        // 1. R.id.home: Action Bar up button clicked
        switch (id) {
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

    private class CloudListAdapter extends ArrayAdapter<CloudFile> {

        public CloudListAdapter(Context context, int resource, CloudFile[] objects) {
            super(context, resource, objects);
        }
    }
}
