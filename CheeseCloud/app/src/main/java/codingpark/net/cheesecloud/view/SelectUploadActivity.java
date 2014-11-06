package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.codingpark.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Locale;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.eumn.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.FileManager;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.OnSelectUploadChangedListener;
import codingpark.net.cheesecloud.model.UploadFile;
import codingpark.net.cheesecloud.wsi.WsFolder;

public class SelectUploadActivity extends Activity implements OnSelectUploadChangedListener {

    private static final String TAG                     = SelectUploadActivity.class.getSimpleName();

    public static final String RESULT_SELECTED_FILES_KEY= "selected_files_path_list";
    public static final String RESULT_REMOTE_PARENT_ID  = "remote_parent_id";
    public static String remote_folder_id               = "";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter      = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager                            = null;

    private final Handler handler = new Handler();
    private PagerSlidingTabStrip tabs;

    private Drawable oldBackground = null;
    private int currentColor = 0xFF3F9FE0;

    private Fragment mActiveFragment        = null;
    private ArrayList<String> mSelectedFiles    = null;

    // Bottom bar items
    private Button select_upload_path_bt    = null;
    private Button upload_bt                = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initial ActionBar
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.upload_activity_action_bar_title);
        setContentView(R.layout.activity_select_upload);

        mSelectedFiles = new ArrayList<String>();

        initUI();
        initHandler();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        // Create pager margin
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Associate the custom tab and the pager
        tabs.setViewPager(mViewPager);
        changeColor(currentColor);
        refresh_bottom_bar();
    }


    /**
     * This will check if the user is at root directory. If so, if they press back
     * again, it will close the application.
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        int position = mViewPager.getCurrentItem();
        Log.d(TAG, "Position:" + position);
        // Current is not root directory, click back key indicate return up directory
        if(keycode == KeyEvent.KEYCODE_BACK) {
            if (!((OnKeyDownListener) mActiveFragment).onBackKeyDown()) {
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle action bar event
        // 1. R.id.home: Action Bar up button clicked
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Selected Activity return results!");
        if (resultCode == RESULT_OK) {
            remote_folder_id = data.getStringExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID);
            Log.d(TAG, "User selected remote folder id: \n" + remote_folder_id);
            refresh_bottom_bar();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onSelectUploadChanged(ArrayList<String> list) {
        mSelectedFiles = list;
        upload_bt.setText(this.getResources().getString(
                R.string.upload_activity_bottom_bar_upload_bt)
                + "(" + mSelectedFiles.size() + ")");
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d(TAG, "getItem: " + position);
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = FragmentSelectUploadImage.newInstance("", "");
                    //getFragmentManager().beginTransaction().add(frag, String.valueOf(position) + "kk").commit();
                    return frag;
                case 1:
                    frag = FragmentSelectUploadVideo.newInstance("", "");
                    //getFragmentManager().beginTransaction().add(frag, String.valueOf(position) + "mm").commit();
                    return frag;
                case 2:
                    frag = FragmentSelectUploadFiles.newInstance("", "");
                    //getFragmentManager().beginTransaction().add(frag, String.valueOf(position) + "nn").commit();
                    return frag;
                default:
                    frag = FragmentSelectUploadImage.newInstance("", "");
                    //getFragmentManager().beginTransaction().add(frag, String.valueOf(position) + "ll").commit();
                    return frag;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(TAG, "getPageTitle: " + position);
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_images).toUpperCase(l);
                case 1:
                    return getString(R.string.title_video).toUpperCase(l);
                case 2:
                    return getString(R.string.title_files).toUpperCase(l);
            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d(TAG, "Set primary fragment: " + position);
            mActiveFragment = (Fragment)object;
            if (object instanceof FragmentSelectUploadImage) {
                Log.d(TAG, "Current: " + FragmentSelectUploadImage.class.getSimpleName());
            } else if (object instanceof FragmentSelectUploadVideo) {
                Log.d(TAG, "Current: " + FragmentSelectUploadVideo.class.getSimpleName());
            } else if (object instanceof FragmentSelectUploadFiles) {
                Log.d(TAG, "Current: " + FragmentSelectUploadFiles.class.getSimpleName());
            }

        }
    }

    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }
        currentColor = newColor;
    }
    /**
     * Initial UploadActivity UI elements
     */
    private void initUI() {
        // Initial UploadActivity bottom bar UI elements(Button)
        select_upload_path_bt = (Button)findViewById(R.id.select_upload_location_bt);
        upload_bt = (Button)findViewById(R.id.start_upload_bt);

    }

    /**
     * Initial UploadActivity UI elements event handler
     */
    private void initHandler() {

        // Initial UploadActivity bottom bar button click handler
        select_upload_path_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Select upload path button clicked!");
                Intent r_intent = new Intent(SelectUploadActivity.this, SelectPathActivity.class);
                SelectUploadActivity.this.startActivityForResult(r_intent, 0, null);
            }
        });
        upload_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Upload button clicked, start uploading!");
                Toast.makeText(SelectUploadActivity.this, "开始上传", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putStringArrayListExtra(RESULT_SELECTED_FILES_KEY, mSelectedFiles);
                intent.putExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID, remote_folder_id);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void refresh_bottom_bar() {
        if (remote_folder_id == null || (remote_folder_id.isEmpty())) {
            remote_folder_id = AppConfigs.current_remote_user_id;
        }
        Log.d(TAG, "@@@@@@@@@@@@@@@The current remote user id" + AppConfigs.current_remote_user_id);
        Log.d(TAG, "@@@@@@@@@@@@@@@The folder id: " + remote_folder_id);
        new RefreshBottomBarTask().execute();
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    private int getFolderInfo_wrapper(UploadFile folder) {
        int result = WsResultType.Success;
        WsFolder wsFolder = new WsFolder();
        wsFolder.ID = folder.getRemote_id();
        result = ClientWS.getInstance(this).getFolderInfo(wsFolder);
        if (result == WsResultType.Success) {
            folder.setFilePath(wsFolder.Name);
        }
        return result;
    }

    private class RefreshBottomBarTask extends AsyncTask<Void,Void,Integer> {

        private UploadFile folder   = null;

        public RefreshBottomBarTask() {
            folder = new UploadFile();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = WsResultType.Success;
            folder.setRemote_id(remote_folder_id);
            result = getFolderInfo_wrapper(folder);
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case WsResultType.Success:
                    select_upload_path_bt.setText(folder.getFilePath());
                default:
                    return;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
