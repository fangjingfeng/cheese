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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.codingpark.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Locale;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.OnSelectUploadChangedListener;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;
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
    private int currentColor = 0xFF2f95f7;

    private Fragment mActiveFragment        = null;
    private ArrayList<String> mSelectedFiles    = null;

    // Bottom bar items
    private Button select_upload_path_bt    = null;
    private Button upload_bt                = null;
    private CloudFile dqforderInfo;
    private String forderName ;

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Selected Activity return results!");
        if (resultCode == RESULT_OK) {
            remote_folder_id = data.getStringExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID);
            forderName = data.getStringExtra(SelectPathActivity.RESULR_SELECTES_REMOTE_FOLDER_NAME);
            update_file_path.setText(forderName);
            //refresh_bottom_bar();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
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
        dqforderInfo = (CloudFile) getIntent().getSerializableExtra(MyConstances.GetFotlerObject);
        remote_folder_id = dqforderInfo.getRemote_id();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        initUI();
        initHandler();
        
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);

        share_foot = (LinearLayout) findViewById(R.id.share_foot);
        
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private  TranslateAnimation animationUp = null;
    private  TranslateAnimation animationDowm = null;
    @Override
    public void onSelectUploadChanged(ArrayList<String> list) {
        mSelectedFiles = list;
        upload_bt.setText(this.getResources().getString(
                R.string.upload_activity_bottom_bar_upload_bt)
                + "(" + mSelectedFiles.size() + ")");
        if(mSelectedFiles.size()>0){
        	share_foot.setVisibility(View.VISIBLE);
        	if(animationUp==null){
        		System.out.println("padingName zou le"+share_foot.getHeight());
        		animationUp = new TranslateAnimation(0, 0,screenHeight, share_foot.getHeight());
        		animationUp.setDuration(500);
                share_foot.setAnimation(animationUp);
                animationUp.startNow(); 
        	}
        }else{
        	animationDowm = new TranslateAnimation(0, 0,screenHeight-share_foot.getHeight(), screenHeight);
        	animationDowm.setDuration(800);
            share_foot.setAnimation(animationDowm);
            animationDowm.startNow(); 
            share_foot.setVisibility(View.GONE);
            animationUp=null;
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }
                td.startTransition(200);
            }
            oldBackground = ld;
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
        update_file_path = (TextView) findViewById(R.id.update_file_path);
        update_file_path.setText(dqforderInfo.getFilePath());
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
                r_intent.putExtra("ISDISPYAFILE", false);
                SelectUploadActivity.this.startActivityForResult(r_intent, 0, null);
            }
        });
        
        upload_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectUploadActivity.this, "开始上传", Toast.LENGTH_SHORT).show();
                if(mSelectedFiles.size()!=0){
                	Intent intent = new Intent();
                    intent.putStringArrayListExtra(RESULT_SELECTED_FILES_KEY, mSelectedFiles);
                    intent.putExtra("DESPYLEUPDATE", 1); 
                    
                    intent.putExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID,remote_folder_id);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                	Toast.makeText(SelectUploadActivity.this, "请选择要上传的文件", 0).show();
                }
            }
        });
    }

    private void refresh_bottom_bar() {
        if (remote_folder_id == null || (remote_folder_id.isEmpty())) {
            remote_folder_id = AppConfigs.current_remote_user_id;
        }
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


	private TextView update_file_path;

	private LinearLayout share_foot;

	private int screenHeight;

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
        mSelectedFiles.clear();
        return false;
    }
}
