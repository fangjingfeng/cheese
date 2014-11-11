package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.UploadService;
import codingpark.net.cheesecloud.model.UploadFileDataSource;


public class MainActivity extends Activity implements OnFragmentInteractionListener {

    private static final String TAG                 = "MainActivity";
    private static String remote_parent_id          = "";

    // Application preferences key
    public static final String PREFS_NAME           = "ManagerPrefsFile";	//user preference file name
    public static final String PREFS_HIDDEN         = "hidden";
    public static final String PREFS_COLOR          = "color";
    public static final String PREFS_THUMBNAIL      = "thumbnail";
    public static final String PREFS_SORT           = "sort";

    // Bottom tab button
    private Button upload_bt            = null;
    private Button copy_bt              = null;
    private Button paste_bt             = null;
    private Button more_bt              = null;


    // Tab activity headers
    /**
     * Tab 0    -->     home
     * Tab 1    -->     contact
     * Tab 2    -->     setting
     */
    private ImageView tab_home_iv       = null;
    private ImageView tab_contact_iv    = null;
    private ImageView tab_setting_iv    = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter  = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager            = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide action bar
        if(android.os.Build.VERSION.SDK_INT < 11) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }

        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Initial UI and add widget listener
        initUI();
        initHandler();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_new_dir:
                return true;

            case R.id.action_search:
                return true;

            case R.id.action_logout:
                finish();
                return true;
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                this.startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Upload Activity return results!");
        if (resultCode == RESULT_OK) {
            final ArrayList<String> selectFiles = data.getStringArrayListExtra(SelectUploadActivity.RESULT_SELECTED_FILES_KEY);
            remote_parent_id   = data.getStringExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID);
            Log.d(TAG, "User selected upload file: \n" + selectFiles.toString());
            Log.d(TAG, "User selected remote parent id: " + remote_parent_id);

            new ScanFilesTask(selectFiles).execute();
            /*
            Log.d(TAG, "*****Test CheckedFileInfo and UploadFile******");
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    if (selectFiles.size() <= 0)
                        return;
                    File file = new File(selectFiles.get(0));
                    if (file.exists()) {
                        if (file.isFile()) {
                            ClientWS.getInstance(MainActivity.this).test_checkedFileInfo(selectFiles.get(0));
                            ClientWS.getInstance(MainActivity.this).test_uploadFile(selectFiles.get(0));
                        } else if (file.isDirectory()) {
                            ClientWS.getInstance(MainActivity.this).test_createFolder(selectFiles.get(0));
                        }

                    }
                }
            });
            t.start();
            */
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initUI() {
        tab_home_iv = (ImageView)findViewById(R.id.tab_home);
        tab_contact_iv = (ImageView)findViewById(R.id.tab_contact);
        tab_setting_iv = (ImageView)findViewById(R.id.tab_setting);

        // Begin, home tab selected
        tab_home_iv.setSelected(true);

        // Initial bottom bar button
        upload_bt = (Button)findViewById(R.id.menu_bottom_upload_bt);
        copy_bt = (Button)findViewById(R.id.menu_bottom_copy_bt);
        paste_bt = (Button)findViewById(R.id.menu_bottom_paste_bt);
        more_bt = (Button)findViewById(R.id.menu_bottom_more_bt);

    }

    private void initHandler() {
        TopTabHeaderListener r_listener = new TopTabHeaderListener();
        tab_home_iv.setOnClickListener(r_listener);
        tab_contact_iv.setOnClickListener(r_listener);
        tab_setting_iv.setOnClickListener(r_listener);

        /**
         * Listen ViewPager page changed action
         * Action:
         *      1. Update tab header state
         */
        mViewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        switch (i) {
                            case 0:
                                Log.d(TAG, "Switch to home tab!");
                                tab_home_iv.setSelected(true);
                                tab_contact_iv.setSelected(false);
                                tab_setting_iv.setSelected(false);
                                break;
                            case 1:
                                Log.d(TAG, "Switch to contact tab!");
                                tab_home_iv.setSelected(false);
                                tab_contact_iv.setSelected(true);
                                tab_setting_iv.setSelected(false);
                                break;
                            case 2:
                                Log.d(TAG, "Switch to setting tab!");
                                tab_home_iv.setSelected(false);
                                tab_contact_iv.setSelected(false);
                                tab_setting_iv.setSelected(true);
                                break;
                            default:
                                return;
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                }
        );

        // Set upload_bt click listener, start local filesystem browser activity
        upload_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                Intent i = new Intent(MainActivity.this, UploadSelectActivity.class);
                MainActivity.this.startActivityForResult(i, 0);
                */
                // TODO Replace UploadSelectActivity to SelectUploadActivity
                Intent i = new Intent(MainActivity.this, SelectUploadActivity.class);
                MainActivity.this.startActivityForResult(i, 0);
            }
        });

        more_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "more button clicked!");
                Intent intent = new Intent(MainActivity.this, TransferStateActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.d(TAG, "Fragment interaction id:" + id);
    }

    /**
     * A OnClickListener to listen top header tab click event,
     * Action:
     *      1. Switch the tab corresponding fragment
     *      2. Update tab header state.
     *
     */
    private class TopTabHeaderListener implements View.OnClickListener{

        private static final String L_TAG       = "TopTabHeaderListener";

        @Override
        public void onClick(View v) {
            ImageView iv = (ImageView)v;
            switch (iv.getId()) {
                case R.id.tab_home:
                    Log.d(L_TAG, "Home tab clicked!");
                    tab_home_iv.setSelected(true);
                    tab_contact_iv.setSelected(false);
                    tab_setting_iv.setSelected(false);
                    mViewPager.setCurrentItem(0, true);
                    break;
                case R.id.tab_contact:
                    Log.d(L_TAG, "Contact tab clicked!");
                    tab_home_iv.setSelected(false);
                    tab_contact_iv.setSelected(true);
                    tab_setting_iv.setSelected(false);
                    mViewPager.setCurrentItem(1, true);
                    break;
                case R.id.tab_setting:
                    Log.d(L_TAG, "Setting tab clicked!");
                    tab_home_iv.setSelected(false);
                    tab_contact_iv.setSelected(false);
                    tab_setting_iv.setSelected(true);
                    mViewPager.setCurrentItem(2, true);
                    break;
                default:
                    return;
            }
        }
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
            switch (position) {
                case 0:
                    return FragmentHome.newInstance("");
                case 1:
                    return FragmentContact.newInstance(MainActivity.this, "");
                case 2:
                    return FragmentSetting.newInstance(MainActivity.this, "");
                default:
                    return FragmentHome.newInstance("");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "";
                    //return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return "";
                    //return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return "";
                    //return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }



    private class ScanFilesTask extends AsyncTask<Void, Void, Integer> {

        public static final int SCAN_SUCCESS    = 0;
        public static final int SCAN_FAILED     = 1;

        private ArrayList<String> mFileList         = null;
        private UploadFileDataSource mDataSource    = null;

        public ScanFilesTask(ArrayList<String> fileList) {
            mFileList = fileList;
            mDataSource = new UploadFileDataSource(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            mDataSource.open();
        }

        protected Integer doInBackground(Void... params) {
            for (String path: mFileList) {
                Log.d(TAG, "scan: " + path);
                scan(new File(path), -1, remote_parent_id);
            }
            return SCAN_SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case SCAN_SUCCESS:
                    Toast.makeText(MainActivity.this, "扫描插入完成", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Scan complete, send upload action to UploadService!");
                    UploadService.startActionUpload(MainActivity.this);
                    break;
                case SCAN_FAILED:
                    break;
                default:
                    break;
            }
            mDataSource.close();
        }


        private void scan(File file, long l_parent_id, String r_parent_id) {
            Log.d(TAG, "Scan");
            long id = mDataSource.addUploadFile(file, l_parent_id, r_parent_id);
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
                File subFile = null;
                for (int i = 0; i < fileArray.length; i++) {
                    subFile = fileArray[i];
                    if (subFile.isFile()) {
                        mDataSource.addUploadFile(subFile, id, "");
                    } else {
                        scan(subFile, id, "");
                    }
                }
            }
            return;
        }
    }

}
