package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.UploadFile;
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
public class TransferStateActivity extends Activity implements ActionBar.TabListener, OnTransFragmentInteractionListener {
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
    private int mActivePagePos                  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_state);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
        initHanlder();
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
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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

    private void initHanlder() {
        trans_control_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO handle download
                Log.d(TAG, "start/pause all");
                int curr_positive_state = Integer.valueOf(trans_control_bt.getTag().toString());
                switch (curr_positive_state) {
                    case CONTROL_BUTTON_PAUSE_ACTIVE:
                        UploadService.startActionPauseAll(TransferStateActivity.this);
                        break;
                    case CONTROL_BUTTON_START_ACTIVE:
                        UploadService.startActionResumeAll(TransferStateActivity.this);
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
                UploadService.startActionCancelAll(TransferStateActivity.this);
            }
        });

        trans_clear_all_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO clear all uploaded record from local table
                Log.d(TAG, "Clear all record");
                UploadService.startActionClearAll(TransferStateActivity.this);
            }
        });
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

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
    public void refreshDownloadBottomBar() {

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
}
