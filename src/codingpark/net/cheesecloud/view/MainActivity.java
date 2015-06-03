package codingpark.net.cheesecloud.view;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.CrontabService;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnSettingListener;
import codingpark.net.cheesecloud.handle.UploadService;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.utils.MyUtils;


/**
 * MainActivity is first show when user login success.
 */
public class MainActivity extends Activity implements OnFragmentInteractionListener,OnSettingListener {
    private static final String TAG                 = "MainActivity";
    // UI
    // Bottom tab button
    private Button upload_bt            = null;
    private Button copy_bt              = null;
    private Button paste_bt             = null;
    private Button more_bt              = null;
    
    private String updateUrl                                    = null;
    private String updateDesc                                   = null;
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
     * The go back button click state tag
     */
    private boolean doubleBackToExitPressedOnce                 = false;
    /**
     * The internal time of double click(Double click exit mechanism)
     */
    private static final int INTERNAL_DOUBLE_CLICK_EXIT_TIME    = 2000;

    /**
     * The remote folder id(Upload folder)
     */
    private static String remote_parent_id                      = "";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    
    private FragmentContact fragmentContext;
	private FragmentSetting fragmentSetting;
	private FragmentHome fragmentHome; 
    
    private SectionsPagerAdapter mSectionsPagerAdapter  = null;
    private PackageManager pm;
    private PackageInfo packageInfo;
    private int versionCode;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager            = null;
    
  //定义一个变量用于表示是否退出
    private boolean isExit ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏actiom bar 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        pm = getPackageManager();
        try {
        	packageInfo = pm.getPackageInfo(getPackageName(), 0);
        	versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Initial UI and add widget listener
        initUI();
        initHandler();
        
      //判断是否新的版本发布
        checkUpdate();
    }
   
    @Override
    protected void onResume() {
        doubleBackToExitPressedOnce = false;
        super.onResume();
    }

    @Override
    public void finish() {
        UploadService.stopUploadService(MainActivity.this);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Upload Activity return results!"+resultCode);
        if (resultCode == RESULT_OK) {
            final ArrayList<String> selectFiles = data.getStringArrayListExtra(SelectUploadActivity.RESULT_SELECTED_FILES_KEY);
            remote_parent_id   = data.getStringExtra(SelectPathActivity.RESULT_SELECTED_REMOTE_FOLDER_ID);
            for(int i=0;i<selectFiles.size();i++){
            	System.out.println(":"+selectFiles.get(i));
            }
            Log.d(TAG, "User selected upload file: \n" + selectFiles.toString());
            Log.d(TAG, "User selected remote parent id: " + remote_parent_id);
           new ScanUploadFilesTask(selectFiles).execute();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initial UI elements data
     */
    private void initUI() {
        tab_home_iv = (ImageView)findViewById(R.id.tab_home);
        tab_contact_iv = (ImageView)findViewById(R.id.tab_contact);
        tab_setting_iv = (ImageView)findViewById(R.id.tab_setting);

        // Begin, home tab selected
        tab_home_iv.setSelected(true);

        // Initial bottom bar button
        upload_bt = (Button)findViewById(R.id.menu_bottom_upload_bt);
        copy_bt = (Button)findViewById(R.id.menu_bottom_download_bt);
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
         * 1. Update tab header state
         */
        mViewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {}
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

    @Override
    public void onFragmentInteraction(String id) {
        Log.d(TAG, "Fragment interaction id:" + id);
    }

    @Override
    public void logout() {
        // 1. Stop 
    	DownloadService.stopUploadService(MainActivity.this);
        // 2. Stop UploadService
        UploadService.stopUploadService(MainActivity.this);
        // Stop CrontabService
        CrontabService.stopCrontabService(MainActivity.this);
        // 3. Jump to WelcomeActivity
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, WelcomeActivity.class);
        this.startActivity(intent);
        // 4. Finish self
        finish();
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
            fragmentContext = FragmentContact.newInstance(MainActivity.this, "");
        	fragmentSetting = FragmentSetting.newInstance("");
        	fragmentHome = FragmentHome.newInstance(MainActivity.this,"");
        	
            switch (position) {
                case 0:
                    return fragmentHome;
                case 1:
                    return fragmentContext;
                case 2:
                    return fragmentSetting;
                default:
                    return fragmentHome;
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
    }


    /**
     * Scan the selected files and folders, and then insert the record to
     * upload_files table recursively.
     */
    private class ScanUploadFilesTask extends AsyncTask<Void, Void, Integer> {

        public static final int SCAN_SUCCESS    = 0;
        public static final int SCAN_FAILED     = 1;

        private ArrayList<String> mFileList         = null;
        private UploadFileDataSource mDataSource    = null;

        public ScanUploadFilesTask(ArrayList<String> fileList) {
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
                    UploadService.startActionUploadAll(MainActivity.this);
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
                result = ClientWS.getInstance(MainActivity.this).createFolderUpload_wrapper(uFile);
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
            // 1. create record on server
            // 2. insert record on local table
            else if (file.isFile()){
                // Just handle exist and size > 0 file
                if (file.exists() && file.length() > 0) {
                    result = ClientWS.getInstance(MainActivity.this).checkedFileInfo_wrapper(uFile);
                    if (result == CheckedFileInfoResultType.RESULT_CHECK_SUCCESS ||
                            result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD) {
                        mDataSource.addUploadFile(uFile);
                    }
                }
            }
            return;
        }
    }

    
    
    /*
 	 * 返回键
 	 * (non-Javadoc)
 	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
 	 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            exit();  
            return false;  
        } else {  
            return super.onKeyDown(keyCode, event);  
        } 
    }
    
    Handler mHandler = new Handler() {  
  	  
        @Override  
        public void handleMessage(Message msg) {  
            // TODO Auto-generated method stub   
            super.handleMessage(msg);  
            isExit = false;  
        }  
  
    }; 
    
    public void exit(){  
	    if (!isExit) {  
	        isExit = true;  
	        Toast.makeText(getApplicationContext(),getResources().getText(R.string.double_click_exit_hint_msg), Toast.LENGTH_SHORT).show();  
	        mHandler.sendEmptyMessageDelayed(0, 1000);  
	    } else {  
	        Intent intent = new Intent(Intent.ACTION_MAIN);  
	        intent.addCategory(Intent.CATEGORY_HOME);  
	        startActivity(intent);  
	        System.exit(0);  
	    }  
    }  
    
    
    /**
	 * 检查应用升级
	 */
	private void checkUpdate() {
		new Thread(){
			public void run() {
				long startTime = System.currentTimeMillis();
				Message message = Message.obtain();
				try {
					URL url =new URL(getResources().getString(R.string.update_url));
					
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					
					int responseCode = conn.getResponseCode();					
					if(responseCode == 200){
						InputStream inputStream = conn.getInputStream();
						String responStr = MyUtils.converStream2String(inputStream);
						// 根据json字符串创建json对象
						JSONObject jsonObject = new JSONObject(responStr);
						
						int serverCode = jsonObject.getInt("code");
						updateUrl = jsonObject.getString("update_url");
						updateDesc = URLDecoder.decode(jsonObject.getString("desc"), "UTF-8");
						if(versionCode != serverCode&&serverCode>versionCode){ 
							// 弹出对话框，提示升级
							message.what = SHOWUPDATEDIALOG;
						}
					}else{
						//联网出错
						// 如果返回码不正确的话  // ctrl + 句号 可以在所有的  TODO 之的切换
						Toast.makeText(MainActivity.this, "出错了，错误码：2004，请联系客服",0);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					
					long now = System.currentTimeMillis();
					
					if(now - startTime <2000){
						SystemClock.sleep(startTime+2000 - now);
					}
					
					handler.sendMessage(message);
				}
			}
		}.start();
	}
    
	private final int SHOWUPDATEDIALOG = 2;
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWUPDATEDIALOG:
				showConfirmUpdateDialog();
				break;
			default:
				break;
			}
		};
	};
	
	
	protected void showConfirmUpdateDialog() {
		/*
		 * 创建对话框的上下文对象，不能使用  ApplicationContext  而只能使用Activity 
		 */
		AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
		adb.setTitle("确认升级");
		adb.setMessage(updateDesc);
		//adb.setCancelable(true); // 该设置，点后退按键时，不取消对话框 
		// 给对话框添加取消的监听事件，
		adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				//跳至主页面
				//gotoHome();
			}
		});
		
		adb.setNegativeButton("现在升级", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// adb 中的按钮，点击后，会自动隐藏对话框  
				//dialog.dismiss();
				System.out.println("现在开始升级");
				downloadFile();
			}
		});
		
		adb.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//跳至主页面
				//gotoHome();
			}
		});
		
		adb.show();
		
	}
	
	/**
	 * 下载更新APK 
	 */
	protected void downloadFile() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(updateUrl, Environment.getExternalStorageDirectory().getAbsolutePath()+"/CheeseCloud.apk", new RequestCallBack<File>() {
			/**
			 *  下载成功后回调 
			 */ 
			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
	//			  <intent-filter>
	//                <action android:name="android.intent.action.VIEW" />
	//                <category android:name="android.intent.category.DEFAULT" />
	//                <data android:scheme="content" />
	//                <data android:scheme="file" />
	//                <data android:mimeType="application/vnd.android.package-archive" />
	//            </intent-filter>
				// 下载成功后，安装文件
				File apkFile = responseInfo.result;
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
//				intent.setType("application/vnd.android.package-archive");
//				intent.setData(Uri.fromFile(apkFile));
				intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//				startActivity(intent);
				startActivityForResult(intent, 99);
				
			}
			
			/**
			 * 下载失败的时候回调
			*/
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				/* *  
				 * 创建Toast时，即可以使用 applicationContext  也可以使用 Activity
				 */
				Toast.makeText(getApplicationContext(), "更新下载失败 ", 0).show();
				/*System.out.println(msg);
				error.printStackTrace();*/
				//gotoHome();
			}
			
			@Override
			/**
			 * 正在下载的时候，回调
			 */
			public void onLoading(long total, long current, boolean isUploading) {
				//tv_loading.setText(current+" / "+total);
				System.out.println("current---"+current +":"+"total---"+total);
				super.onLoading(total, current, isUploading);
			}

			
		});
	};
	
}
