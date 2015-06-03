package codingpark.net.cheesecloud.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.CrontabService;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.OnSettingListener;
import codingpark.net.cheesecloud.handle.UploadService;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.utils.MyUtils;

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
public class CloudFilesActivity_1s extends Activity implements OnFragmentInteractionListener,OnSettingListener{
   
	private static final String TAG         = CloudFilesActivity_1s.class.getSimpleName();
    /**
     * The user select disk object(CloudFile) key(Used by Intent.getExtra)
     */
    public static final String SELECT_DISK_KEY      = "select_disk_key";
    private static CloudFile mRootDisk      = null;
    private Context ctx       = CloudFilesActivity_1s.this;
    
    // UI elements
    private LayoutInflater mInflater                    = null;
    private LinearLayout mListContainer                 = null;
    private ProgressBar mLoadingView                    = null;
    private ActionMode mActionMode                      = null;
   
    SectionsPagerAdapter mSectionsPagerAdapter  = null;
    private FragmentContact fragmentContext    =  null ;
    private FragmentSetting fragmentSetting   =  null;
    private Fragment mActiveFragment        = null;
    
   /**
     * The go back button click state tag
     */
    private boolean doubleBackToExitPressedOnce                 = false;
 
    private static String remote_parent_id                      = "";
    
    private FragmentHomeItme fragmentHomeItme;

    private LinearLayout mForter_bottom_content  =null;
    private ViewPager mViewPager                 =null;
    //UI 控件 选项卡
    private ImageView tab_home_iv       = null;
    private ImageView tab_contact_iv    = null;
    private ImageView tab_setting_iv    = null;
    // Bottom tab button
    private Button upload_bt            = null;
    private Button copy_bt              = null;
    private Button paste_bt             = null;
    private Button more_bt              = null;
    private Button new_bt               =null;

    private TextView tv_loading;
    
    private LinearLayout ll_content;

    private Intent recIntent;

 	private RelativeLayout top_select_button;
 	
 	private SharedPreferences sp;
 	private PackageInfo packageInfo;
 	private int versionCode=0;
   //包资源管理器
 	private PackageManager pm;
	private MyCallInterface mc;

 	public void setCallfuc(MyCallInterface mc)  
    {  
       this.mc= mc;  
    } 
 	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //获取版本号
        pm = getPackageManager();
        try {
        	packageInfo = pm.getPackageInfo(getPackageName(), 0);
        	versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        mListContainer = (LinearLayout)findViewById(R.id.listcontainer);
        mLoadingView = (ProgressBar)findViewById(R.id.loading);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        recIntent = getIntent();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        sp = getSharedPreferences(MyConstances.SP_NAME, MODE_PRIVATE);
        //初始化UI
        initUI();
        //ui逻辑
        initHandler();
    }
    private void initUI() {
        tab_home_iv = (ImageView)findViewById(R.id.tab_home);
        tab_contact_iv = (ImageView)findViewById(R.id.tab_contact);
        tab_setting_iv = (ImageView)findViewById(R.id.tab_setting);
        tab_home_iv.setSelected(true);
        top_select_button = (RelativeLayout) findViewById(R.id.top_select_button);
        mForter_bottom_content=(LinearLayout) findViewById(R.id.mian_bottom_content);
    }

    private void initHandler() {
        TopTabHeaderListener r_listener = new TopTabHeaderListener();
        tab_home_iv.setOnClickListener(r_listener);
        tab_contact_iv.setOnClickListener(r_listener);
        tab_setting_iv.setOnClickListener(r_listener);
        /**
         * Listen ViewPager page changed action
         * Action:
         * 1. Update tab header state
         */
        mViewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {
                    	Log.d("mViewPager", "mViewPager 滑动监听");	
                    	mc.viewPagerListener();
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
    }
   
    /**
     * 头标签单击事件
     * A OnClickListener to listen top header tab click event,
     * Action:
     *      1. Switch the tab corresponding fragment
     *      2. Update tab header state.
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
                    System.out.println("点击了返回首页~~~");
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
    @Override
    public void onFragmentInteraction(String id) {
        Log.d(TAG, "Fragment interaction id:" + id);
    }
   

    @Override
    protected void onResume() {
        doubleBackToExitPressedOnce = false;
        super.onResume();
    }

    @Override
    public void finish() {
        UploadService.stopUploadService(CloudFilesActivity_1s.this);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Upload Activity return results!");
        if (resultCode == RESULT_OK) {
            final ArrayList<String> selectFiles = data.getStringArrayListExtra(SelectUploadActivity.RESULT_SELECTED_FILES_KEY);
            remote_parent_id   = data.getStringExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID);
            Log.d(TAG, "User selected upload file: \n" + selectFiles.size());
            Log.d(TAG, "User selected remote parent id: " + remote_parent_id);
            int handler =data.getIntExtra("DESPYLEUPDATE", 0);
            
            new ScanUploadFilesTask(selectFiles).execute();
            
            Intent intents=new Intent(this,TransferStateActivity.class);
            intents.putExtra("DESPYLE", handler);
            startActivity(intents);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        	fragmentHomeItme = FragmentHomeItme.newInstance(CloudFilesActivity_1s.this,recIntent,top_select_button,"");
            fragmentContext = FragmentContact.newInstance(CloudFilesActivity_1s.this, "");
            fragmentSetting = FragmentSetting.newInstance("");
         
            switch (position) {
                case 0:
                    return fragmentHomeItme;
                case 1:
                    return fragmentContext;
                case 2:
                    return fragmentSetting;
                default:
                    return fragmentContext;
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
                case 1:
                    return "";
                    //return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return "";
                    //return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
        
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d(TAG, "Set primary fragment: " + position);
            mActiveFragment = (Fragment)object;
            if (object instanceof FragmentHomeItme) {
                Log.d(TAG, "Current: " + FragmentHomeItme.class.getSimpleName());
            } else if (object instanceof FragmentContact) {
                Log.d(TAG, "Current: " + FragmentContact.class.getSimpleName());
            } else if (object instanceof FragmentSetting) {
                Log.d(TAG, "Current: " + FragmentSetting.class.getSimpleName());
            }
        }
    }
    
    /**
     * Scan the selected files and folders, and then insert the record to
     * 扫描选定的文件和文件夹，然后插入记录
     * upload_files table recursively.
     */
    private class ScanUploadFilesTask extends AsyncTask<Void, Integer, Integer> {

        public static final int SCAN_SUCCESS    = 0;
        public static final int SCAN_FAILED     = 1;

        private ArrayList<String> mFileList         = null;
        private UploadFileDataSource mDataSource    = null;

        public ScanUploadFilesTask(ArrayList<String> fileList) {
            mFileList = fileList;
            mDataSource = new UploadFileDataSource(CloudFilesActivity_1s.this);
        }

        @Override
        protected void onPreExecute() {
            mDataSource.open();
        }

        protected Integer doInBackground(Void... params) {
        	int i=0;
        	for (String path: mFileList) {
                Log.d(TAG, "scan: " + path);
                scan(new File(path), -1, remote_parent_id);
                i++;
                publishProgress(i); 
            }
            return SCAN_SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            System.out.println("onProgressUpdate ===＠＠ ="+values[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
        	 Toast.makeText(CloudFilesActivity_1s.this, "扫描插入完成", Toast.LENGTH_SHORT).show();
             Log.d(TAG, "Scan complete, send upload action to UploadService!");
             UploadService.startActionUploadAll(CloudFilesActivity_1s.this);
            switch (result) {
                case SCAN_SUCCESS:
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
            UploadFile uFile = UploadFileDataSource.createUploadFile(file, l_parent_id, r_parent_id);
            int result = WsResultType.Success; // Web service call result
            long l_id = -1;     // insert to local DB return value
            // If is folder
            // 1. create record on server
            // 2. insert record on local database
            // 3. scan sub files and folders
            if (file.isDirectory()) {
                result = ClientWS.getInstance(CloudFilesActivity_1s.this).createFolderUpload_wrapper(uFile);
                if (result != WsResultType.Success)
                    return;     // Create folder failed, return.
                else {
                    uFile.setState(UploadFileState.UPLOADED);
                    l_id = mDataSource.addUploadFile(uFile);
                    File[] fileArray = file.listFiles();
                    File subFile = null;
                    // Scan file's sub folders and files recursively
                    for (int i = 0; i < fileArray.length; i++) {
                        subFile = fileArray[i];
                        scan(subFile, l_id, uFile.getRemote_id());
                    }
                }
            }
            // If is file;
            // 1. create record on server 在服务器上创建的记录
            // 2. insert record on local table 当地表插入记录
            else if (file.isFile()){
                // Just handle exist and size > 0 file
                if (file.exists() && file.length() > 0) {
                    result = ClientWS.getInstance(CloudFilesActivity_1s.this).checkedFileInfo_wrapper(uFile);
                    if (result == CheckedFileInfoResultType.RESULT_CHECK_SUCCESS ||
                            result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD) {
                        mDataSource.addUploadFile(uFile);
                    }
                }
            }
            return;
        }
    }

    
    
    @Override
    protected void onDestroy() {
    		super.onDestroy();
    		UploadService.stopUploadService(CloudFilesActivity_1s.this);
    }
	 @Override
	 public void logout() {
	  // 1. Stop 
		    DownloadService.stopUploadService(CloudFilesActivity_1s.this);
	        // 2. Stop UploadService
	        UploadService.stopUploadService(CloudFilesActivity_1s.this);
	        // Stop CrontabService
	        CrontabService.stopCrontabService(CloudFilesActivity_1s.this);
	        // 3. Jump to WelcomeActivity
	        Intent intent = new Intent();
	        intent.setClass(CloudFilesActivity_1s.this, WelcomeActivity.class);
	        this.startActivity(intent);
	        // 4. Finish self
	        finish();
	 }
	 	/*
	 	 * 返回键
	 	 * (non-Javadoc)
	 	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 	 */
		@Override
	    public boolean onKeyDown(int keycode, KeyEvent event) {
	        int position = mViewPager.getCurrentItem();
	        if(keycode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem()==0) {
	            if (!((OnKeyDownListener)mActiveFragment).onBackKeyDown()) {
	            	UploadService.stopUploadService(CloudFilesActivity_1s.this);
	                finish();
	                return true;
	            }
	        }else{
	        	 finish();
	        }
	       return false;
	    }
		
		/**
		 * 分发触摸事件给所有注册了MyTouchListener的接口
		 */
	    @Override
	    public boolean dispatchTouchEvent(MotionEvent event) {
	    	if( mViewPager.getCurrentItem()==0) {
	    		((MyTouchListener)mActiveFragment).onTouchEvent(event);
	    	}
	        return super.dispatchTouchEvent(event);
	    }
		
	    
	    public static interface BackHandledInterface {
	        public void onBackHandledInterface(int result);
	    }
}
