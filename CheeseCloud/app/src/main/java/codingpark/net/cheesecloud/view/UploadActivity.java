package codingpark.net.cheesecloud.view;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import codingpark.net.cheesecloud.Configs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.FileManager;
import codingpark.net.cheesecloud.handle.UploadHandler;
import codingpark.net.cheesecloud.utils.CatalogList;

/**
 *
 */
public final class UploadActivity extends ListActivity {

    private FileManager mFileMgr = null;
    private UploadHandler mHandler                      = null;
    private UploadHandler.UploadListAdapter mTable      = null;
    private CatalogList mCataList                       = null;

    private SharedPreferences mSettings                 = null;

    private String TAG                      = "UploadActivity";

    // Top bar items
    private ImageButton upload_disk_bt      = null;
    private ImageButton upload_image_bt     = null;
    private ImageButton upload_movie_bt     = null;
    private ImageButton upload_back_bt      = null;

    // Path bar, use to show current directory path
    private LinearLayout path_bar_container = null;

    // Bottom bar items
    private Button select_upload_path_bt    = null;
    private Button upload_bt                = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Don't hide actionbar, need it to display menu
        if(android.os.Build.VERSION.SDK_INT < 11) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
        */

        // Initial ActionBar
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.upload_activity_action_bar_title);

        setContentView(R.layout.activity_upload);

        /* Get system preferences: hide/thumb/color/sort */
        mSettings           = getSharedPreferences(Configs.PREFS_NAME, 0);
        boolean hide        = mSettings.getBoolean(Configs.PREFS_HIDDEN, false);
        boolean thumb       = mSettings.getBoolean(Configs.PREFS_THUMBNAIL, true);
        int sort            = mSettings.getInt(Configs.PREFS_SORT, 1);

        // 1. Initial FileManager utility
        // 2. Set FileManager utility work parameter
        mFileMgr = new FileManager(this);
        mFileMgr.setShowHiddenFiles(hide);
        mFileMgr.setSortType(sort);

        // Initial CatalogList
        mCataList = new CatalogList(this);

        // 1. Initial EventHandler
        // 2. Set EventHandler work parameter(text color/show thumbnail)
        // 3. Create ListAdapter
        mHandler = new UploadHandler(UploadActivity.this, mFileMgr, mCataList);
        mHandler.setShowThumbnails(thumb);
        mTable = mHandler.new UploadListAdapter();

        /*
         * sets the ListAdapter for our ListActivity and
         * gives our EventHandler class the same adapter
         */
        mHandler.setListAdapter(mTable);
        setListAdapter(mTable);
        getListView().setOnItemLongClickListener(mHandler);
        
        // Initial Path bar
        path_bar_container = (LinearLayout)findViewById(R.id.pathBarContainer);
        mHandler.setUpdatePathBar(path_bar_container);

        mHandler.updateContent(mFileMgr.switchToRoot());
        getFocusForButton(R.id.header_disk_button);

        initUI();
        initHandler();
    }

    /**
     * Initial UploadActivity UI elements
      */
    private void initUI() {
        // Initial UploadActivity header tool bar UI elements(ImageButton)
        upload_disk_bt = (ImageButton)findViewById(R.id.header_disk_button);
        upload_image_bt = (ImageButton)findViewById(R.id.image_button);
        upload_movie_bt = (ImageButton)findViewById(R.id.movie_button);
        upload_back_bt = (ImageButton)findViewById(R.id.back_button);

        // Initial UploadActivity bottom bar UI elements(Button)
        select_upload_path_bt = (Button)findViewById(R.id.select_upload_location_bt);
        upload_bt = (Button)findViewById(R.id.upload_bt);

    }

    /**
     * Initial UploadActivity UI elements event handler
     */
    private void initHandler() {
        // Initial UploadActivity top bar button click handler
        upload_disk_bt.setOnClickListener(mHandler);
        upload_image_bt.setOnClickListener(mHandler);
        upload_movie_bt.setOnClickListener(mHandler);
        upload_back_bt.setOnClickListener(mHandler);

        // Initial UploadActivity bottom bar button click handler
        select_upload_path_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Select upload path button clicked!");
                Intent r_intent = new Intent(UploadActivity.this, SelectPathActivity.class);
                UploadActivity.this.startActivityForResult(r_intent, 0, null);
            }
        });
        upload_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Upload button clicked, start uploading!");
                Toast.makeText(UploadActivity.this, "开始上传", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getFocusForButton(int id)
    {
        View v = findViewById(id);
        mHandler.setInitView(v);
        v.setSelected(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar event
        // 1. R.id.home: Action Bar up button clicked
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  To add more functionality and let the user interact with more
     *  file types, this is the function to add the ability.
     */
    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        final String item = mHandler.getFilePath(position);
        File file = new File(item);

        if (file.isDirectory()) {
            if(file.canRead()) {
                mHandler.updateContent(mFileMgr.switchToNextDir(item));

            } else {
                Toast.makeText(this, "Can't read folder due to permissions",
                        Toast.LENGTH_SHORT).show();
            }
            if(mFileMgr.isRoot()) {
            }else {
            }
        } else if (file.isFile()) {
            Log.d(TAG, "Select file: " + item);
        }
    }


    /**
     * This will check if the user is at root directory. If so, if they press back
     * again, it will close the application.
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        String current = mFileMgr.getCurrentDir();

        // Current is not root directory, click back key indicate return up directory
        if(keycode == KeyEvent.KEYCODE_BACK &&
                !(mFileMgr.isRoot()) ) {
            /*
            if(mHandler.isMultiSelected()) {
                mTable.killMultiSelect(true);
                Toast.makeText(UploadActivity.this, getResources().getString(R.string.Multi_select_off), Toast.LENGTH_SHORT).show();
            }
            */

            mHandler.updateContent(mFileMgr.switchToPreviousDir());
            // TODO Judge current directory is root, refresh header bar button status
            if(mFileMgr.isRoot()){
            }else{
            }
            return true;

        }
        // Current is root directory, click back key indicate cancel selected and return home
        else if(keycode == KeyEvent.KEYCODE_BACK &&
                mFileMgr.isRoot() ) {
            finish();
            return false;

        }
        return false;
    }

}
