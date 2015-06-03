package codingpark.net.cheesecloud.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.CrontabService;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnSettingListener;
import codingpark.net.cheesecloud.handle.OnTransFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.UploadService;


/**
 * This activity show the upload/download file state to user.
 * Include 2 section:
 * 1. Download Section
 *  This section contain a list view to show downloading/downloaded
 *  state record.
 * 2. Upload Section
 *  This section contain a list view to show uploading/uploaded state
 *  record.
 */
public class TransferStateActivity extends Activity implements ActionBar.TabListener, OnTransFragmentInteractionListener ,OnSettingListener{
    private static final String TAG             = TransferStateActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Send message to UploadService/DownloadService to start/pause transfer(All)
     */
    private Button trans_control_bt             = null;
    /**
     * Send message to UploadService/DownloadService to cancel transfer(All)
     */
    private Button trans_cancel_all_bt = null;
    /**
     * Clear all transfer(Download/Upload) record from local upload_table/download_table(All)
     * Only all record transfer completed, this widget is visible
     */
    private Button trans_clear_all_bt           = null;

    private static final int CONTROL_BUTTON_PAUSE_ACTIVE    = 0;
    private static final int CONTROL_BUTTON_START_ACTIVE    = 1;

    private static final int DOWNLOAD_LIST_PAGE_ID  = 0;
    private static final int UPLOAD_LIST_PAGE_ID    = 1;
    private int mActivePagePos                  = DOWNLOAD_LIST_PAGE_ID;
    
    private String remote_parent_id             = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_state);
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int nsds = getIntent().getIntExtra("DESPYLE", 0);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        System.out.println("TransferStateActivity :setCurrentItem--->"+nsds);
        mViewPager.setCurrentItem(nsds);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        initUI();
        initHandler();
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.d(TAG, "Click back arrow");
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        Log.d(TAG, "current selected tab position: " + tab.getPosition());
        mActivePagePos = tab.getPosition();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d(TAG, "onTabUnselected:" + tab.getPosition());
        if (tab.getPosition() == 0)
            mActivePagePos = 1;
        else
            mActivePagePos = 0;
    }



    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d(TAG, "onTabReselected:" + tab.getPosition());
        mActivePagePos = tab.getPosition();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initUI() {
        trans_control_bt = (Button)findViewById(R.id.trans_control_bt);
        trans_cancel_all_bt = (Button)findViewById(R.id.trans_cancel_bt);
        trans_clear_all_bt = (Button)findViewById(R.id.trans_clear_rec_bt);
    }

    private void initHandler() {
        trans_control_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "start/pause all");
                int curr_positive_state = Integer.valueOf(trans_control_bt.getTag().toString());
                switch (curr_positive_state) {
                    case CONTROL_BUTTON_PAUSE_ACTIVE:
                        if (mActivePagePos == DOWNLOAD_LIST_PAGE_ID) {
                            DownloadService.startActionPauseAll(TransferStateActivity.this);
                        } else if (mActivePagePos == UPLOAD_LIST_PAGE_ID){
                            UploadService.startActionPauseAll(TransferStateActivity.this);
                        }
                        break;
                    case CONTROL_BUTTON_START_ACTIVE:
                        if (mActivePagePos == DOWNLOAD_LIST_PAGE_ID) {
                            DownloadService.startActionResumeAll(TransferStateActivity.this);
                        } else if (mActivePagePos == UPLOAD_LIST_PAGE_ID) {
                            UploadService.startActionResumeAll(TransferStateActivity.this);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        trans_cancel_all_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel all");
                if (mActivePagePos == DOWNLOAD_LIST_PAGE_ID) {
                    DownloadService.startActionCancelAll(TransferStateActivity.this);
                } else if (mActivePagePos == UPLOAD_LIST_PAGE_ID) {
                    UploadService.startActionCancelAll(TransferStateActivity.this);
                }
            }
        });

        trans_clear_all_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clear all record");
                if (mActivePagePos == DOWNLOAD_LIST_PAGE_ID) {
                    DownloadService.startActionClearAll(TransferStateActivity.this);
                } else if (mActivePagePos == UPLOAD_LIST_PAGE_ID) {
                    UploadService.startActionClearAll(TransferStateActivity.this);
                }
            }
        });

    }

    @Override
    public void onFragmentInteraction(String id) {}

    @Override   
    public void refreshUploadBottomBar(ArrayList<UploadFile> waitUploadFile, ArrayList<UploadFile> uploadingFile, ArrayList<UploadFile> pauseUploadFile, ArrayList<UploadFile> uploadedFile) {
        if (mActivePagePos != UPLOAD_LIST_PAGE_ID)
            return;
        Log.d(TAG, "Receive update bottom bar request from FragmentUploadList");
        if (waitUploadFile.size() > 0 || uploadingFile.size() > 0 || pauseUploadFile.size() > 0) {
            trans_control_bt.setVisibility(View.VISIBLE);
            trans_cancel_all_bt.setVisibility(View.VISIBLE);
            trans_clear_all_bt.setVisibility(View.GONE);

            if (waitUploadFile.size() > 0 || uploadingFile.size() > 0) {
                trans_control_bt.setText(R.string.transfer_state_activity_control_pause_all_bt_text);
                trans_control_bt.setTag(CONTROL_BUTTON_PAUSE_ACTIVE);
            } else {
                trans_control_bt.setText(R.string.transfer_state_activity_control_start_all_bt_text);
                trans_control_bt.setTag(CONTROL_BUTTON_START_ACTIVE);
            }
        } else {
            trans_control_bt.setVisibility(View.GONE);
            trans_cancel_all_bt.setVisibility(View.GONE);
            trans_clear_all_bt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshDownloadBottomBar(ArrayList<DownloadFile> waitDownloadFile, ArrayList<DownloadFile> uploadingFile, ArrayList<DownloadFile> pauseDownloadFile, ArrayList<DownloadFile> uploadedFile) {
        if (mActivePagePos != DOWNLOAD_LIST_PAGE_ID)
            return;
        Log.d(TAG, "Receive update bottom bar request from FragmentDownloadList");
        if (waitDownloadFile.size() > 0 || uploadingFile.size() > 0 || pauseDownloadFile.size() > 0) {
            trans_control_bt.setVisibility(View.VISIBLE);
            trans_cancel_all_bt.setVisibility(View.VISIBLE);
            trans_clear_all_bt.setVisibility(View.GONE);

            if (waitDownloadFile.size() > 0 || uploadingFile.size() > 0) {
                trans_control_bt.setText(R.string.transfer_state_activity_control_pause_all_bt_text);
                trans_control_bt.setTag(CONTROL_BUTTON_PAUSE_ACTIVE);
            } else {
                trans_control_bt.setText(R.string.transfer_state_activity_control_start_all_bt_text);
                trans_control_bt.setTag(CONTROL_BUTTON_START_ACTIVE);
            }
        } else {
            trans_control_bt.setVisibility(View.GONE);
            trans_cancel_all_bt.setVisibility(View.GONE);
            trans_clear_all_bt.setVisibility(View.VISIBLE);
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
            switch (position) {
                case 0:
                    return FragmentDownloadList.newInstance(position);
                case 1:
                    return FragmentUploadList.newInstance(position);
                default:
                    return FragmentDownloadList.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            // Download + Upload
            return 2;
        }

        
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.transfer_state_activity_download_section_title).toUpperCase(l);
                case 1:
                    return getString(R.string.transfer_state_activity_upload_section_title).toUpperCase(l);
            }
            return null;
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
    	System.out.println("------------------------------------TransferStateActivity  -- onActivityResult  ");
        if (resultCode == RESULT_OK) {
            final ArrayList<String> selectFiles = data.getStringArrayListExtra(SelectUploadActivity.RESULT_SELECTED_FILES_KEY);
            remote_parent_id = data.getStringExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID);
            
            Log.d(TAG, "User selected upload file: \n" + selectFiles.toString());
            Log.d(TAG, "User selected remote parent id: " + remote_parent_id);

            System.out.println("------zcxzsadczacsz------------------------------TransferStateActivity  -- onActivityResult  ");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
	 public void logout() {
	  // 1. Stop 
		    DownloadService.stopUploadService(TransferStateActivity.this);
	        // 2. Stop UploadService
	        UploadService.stopUploadService(TransferStateActivity.this);
	        // Stop CrontabService
	        CrontabService.stopCrontabService(TransferStateActivity.this);
	        // 3. Jump to WelcomeActivity
	        Intent intent = new Intent();
	        intent.setClass(TransferStateActivity.this, WelcomeActivity.class);
	        this.startActivity(intent);
	        // 4. Finish self
	        finish();
	 
	 }
}
