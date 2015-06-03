package codingpark.net.cheesecloud.view;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.zip.Inflater;

import com.lidroid.xutils.BitmapUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.BusinessService;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.CreateDirTask;
import codingpark.net.cheesecloud.handle.DeleteFileTask;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.handle.RenameFileTask;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.utils.SharePrefUitl;
import codingpark.net.cheesecloud.view.MyListView.OnRefreshListener;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.view.imegeutils.ImageLoader;
import codingpark.net.cheesecloud.view.imegeutils.ImageThreadLoad;
import codingpark.net.cheesecloud.view.listener.BackHandledInterface;
import codingpark.net.cheesecloud.wsi.FileInfo;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsGuidOwner;
import codingpark.net.cheesecloud.wsi.WsMessageInfo;

public class FragmentHomeItme extends Fragment implements View.OnClickListener,PullFileListTask.OnPullDataReadyListener,CreateDirTask.OnCreateFolderCompletedListener,
DeleteFileTask.OnDeleteFileCompletedListener, RenameFileTask.OnRenameFileCompletedListener,CloudFilesActivity_1s.BackHandledInterface,OnKeyDownListener,MyTouchListener{
    private static final String TAG         = "FragmentHomeItme";
    public static final String SELECT_DISK_KEY      = "select_disk_key";
    
    private static final String RESULT_PREVIEW_IMAGER_KEY  = "RESULT_PREVIEW_IMAGER_KEY";
    
    protected BackHandledInterface mBackHandledInterface;  
    
    private static CloudFile mRootDisk      = null;

    private static Context mContext                 = null;
    public  int screenHeight;
    //----
    private ArrayList<CloudFile> mDiskList          = null;
    private CloudListAdapter mAdapter        =        null;

    private PopupWindow popuWindow                  = null;
    //=-=-=-=--
 // Folder and File cloud file list, fill up by PullCloudFileTask
    private ArrayList<CloudFile> mFolderList            = null;
    private ArrayList<CloudFile> mFileList              = null;
    // Store files + folders, used by ArrayAdapter, the data is mFolderList + mFileList
    private ArrayList<CloudFile> mFileFolderList        = null;
    // Store user selected files object/存储用户选择文件对象
    private ArrayList<CloudFile> mSelectedFileList = null;
    // Store user selected files index in the ListView/在列表视图存储用户选择文件索引
    private ArrayList<Integer> mSelectedPositions   = null;
    // Remember current folder full path/记住当前文件夹的完整路径
    private Stack<CloudFile> mPathStack                 = null;
    // Path bar, use to show current directory path/导航栏：用于显示当前目录的路径
    private LinearLayout path_bar_container             = null;
    
 // UI elements
    private LayoutInflater mInflater                    = null;
    private RelativeLayout mListContainer                 = null;
    private ProgressBar mLoadingView                    = null;
    private ActionMode mActionMode                      = null;
    
    private static  RelativeLayout top_select_button               = null;
    public static final int RESULT_OK                              = -1;
    
 // Bottom tab button
    private Button upload_bt            = null;
    private Button download_bt          = null;
    private Button paste_bt             = null;
    private Button more_bt              = null;
    private Button new_bt               =null;

    
    //popuWindow  all button 
    private Button button_cancel         = null;
    private TextView tv_celic_box        = null;
    private Button button_all_seleck     = null;
    
    private static String remote_parent_id              = "";
	private MyListView mListcontext                       = null;
	private UploadFileDataSource mDataSource            = null;
	private Intent recIntent                            =  null;
	private LinearLayout mian_bottom_content;
	private LinearLayout button_fenxian;
	private RelativeLayout  mRl_file_null;
	private ImageLoader imageLoader;
	private String  wepURL;
	
	public static FragmentHomeItme newInstance(Context context,Intent recIntent,RelativeLayout top_select_button ,String param2) {
    	FragmentHomeItme fragment = new FragmentHomeItme(context,recIntent,top_select_button);
    	return fragment;
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentHomeItme(Context context) {}
    public FragmentHomeItme(Context context,Intent recIntent,RelativeLayout top_select_button) {
    	this.recIntent=recIntent;
    	this.top_select_button=top_select_button;
        mDiskList = new ArrayList<CloudFile>();
        mContext=context;
        //(CloudFilesActivity_1s)getActivity();
    }
    public FragmentHomeItme(){};
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	setUserVisibleHint(true);
    	super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderList = new ArrayList<CloudFile>();
        mFileList = new ArrayList<CloudFile>();
        mFileFolderList = new ArrayList<CloudFile>();
        mSelectedFileList = new ArrayList<CloudFile>();
        mPathStack = new Stack<CloudFile>();
        mSelectedPositions = new ArrayList<Integer>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(mContext);
        wepURL =SharePrefUitl.getStringData(mContext, "wepurl", "http://58.116.52.8:8977/");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_itme, container, false);
        root_itm_view = (LinearLayout)rootView.findViewById(R.id.root_itm_view);
        initPopupWindow();
        ((CloudFilesActivity_1s) mContext).setCallfuc(new MyVeiwPagerListener());
        mListContainer = (RelativeLayout)rootView.findViewById(R.id.listcontainer);
        mListcontext = (MyListView) rootView.findViewById(R.id.content_list);
        
        mLoadingView = (ProgressBar)rootView.findViewById(R.id.loading);
        
        mian_bottom_content = (LinearLayout) rootView.findViewById(R.id.mian_bottom_content);
        mian_bottom_content.setVisibility(View.VISIBLE);
        upload_bt = (Button)rootView.findViewById(R.id.menu_bottom_upload_bt);
        download_bt = (Button)rootView.findViewById(R.id.menu_bottom_download_bt);
        paste_bt = (Button)rootView.findViewById(R.id.menu_bottom_paste_bt);
        more_bt = (Button)rootView.findViewById(R.id.menu_bottom_more_bt);
        new_bt= (Button)rootView.findViewById(R.id.menu_bottom_new_bt);
        
        button_fenxian = (LinearLayout) rootView.findViewById(R.id.button_fenxian);
        mRl_file_null=(RelativeLayout)rootView.findViewById(R.id.rl_file_null);
        menu_bottom_deleter = (Button) rootView.findViewById(R.id.menu_bottom_deleter);
        //发送 信件
        Button menu_bottom_upload_btsd =(Button) rootView.findViewById(R.id.menu_bottom_upload_btsd);
        menu_bottom_upload_btsd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectedFileList.size()>0){
					//选中发送的内容
					System.out.println("mSelectedFileList===>"+mSelectedFileList.size());
					ArrayList<CloudFile> folders = new ArrayList<CloudFile>();
					ArrayList<WsFile> sendWsFile = new ArrayList<WsFile>();
					for(CloudFile fileList:mSelectedFileList){
						if(fileList.getFileType()==0){
							sendWsFile.add(wsFileAddCloudFile(fileList));
						}else{
							folders.add(fileList);
						}
					}
					if(folders.size()>0){
						Toast.makeText(mContext,folders.get(0).getFilePath()+"...等文件夹不能发送。", 0).show();
					}
					if(sendWsFile.size()>0){
						sendFile(sendWsFile);
					}else{
						Toast.makeText(mContext,"文件夹不能发送请选择文件",0).show();
					}
				}else{
					Toast.makeText(mContext,"请选择要发送的文件。",0).show();
				}
			}
        });
        //共享设置 
        Button menu_bottom_new_btsdd=(Button) rootView.findViewById(R.id.menu_bottom_new_btsdd);
        menu_bottom_new_btsdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				BusinessService.getInstance().getPublicContacts(mContext, false);
			}
        });

        //删除文件
        menu_bottom_deleter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectedFileList.size()>0){
					delFile();
					button_fenxian.setVisibility(View.GONE);
				}else{
					Toast.makeText(mContext, "请选择要删除的文件", 0).show();
				}
			}
        });
        
        new_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mkdir();
				button_fenxian.setVisibility(View.GONE);
			}
        });
        // Set upload_bt click listener, start local filesystem browser activity
        upload_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	button_fenxian.setVisibility(View.GONE);
            	System.out.println("mPathStack- -- >"+mPathStack.get(mPathStack.size()-1).getFilePath());
                Intent i = new Intent(mContext, SelectUploadActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(MyConstances.GetFotlerObject, mPathStack.get(mPathStack.size()-1));
                i.putExtras(bundle);
                ((CloudFilesActivity_1s)mContext).startActivityForResult(i, 0);
            }
        });
        
        //下载处理
        download_bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				button_fenxian.setVisibility(View.GONE);
				ArrayList<CloudFile>  downLoadFile=new ArrayList<CloudFile>();
				Toast.makeText(mContext, "选择文件个数："+mSelectedFileList.size(), 0).show();
				for(CloudFile cloudFile:mSelectedFileList){
					if(cloudFile.getFileType()==0){
						downLoadFile.add(cloudFile);
						//保存到数据库中
					}
				}
				if(downLoadFile.size()>0){
					new DownloadFilesTask(downLoadFile).execute();
					Intent i = new Intent(mContext, TransferStateActivity.class);
					i.putExtra("DESPYLE", 0); 
	                ((CloudFilesActivity_1s)mContext).startActivityForResult(i, 0);
				}else{
					Toast.makeText(mContext, "请选择下载的文件",0).show();
				}
			}
        });
        
        //移动文件操作
        paste_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(mContext, "移动文件",0).show();
				ArrayList<CloudFile>  downLoadFile=new ArrayList<CloudFile>();
				Toast.makeText(mContext, "选择文件个数："+mSelectedFileList.size(), 0).show();
				for(CloudFile cloudFile:mSelectedFileList){
					if(cloudFile.getFileType()==0){
						downLoadFile.add(cloudFile);
					}
					System.out.println("cloudFile ---》"+cloudFile.getRemote_id());
				}
				
				if(downLoadFile.size()>0){
					Intent mobileIntent =new Intent(mContext,MobileActivity.class);
					Bundle bundle =new Bundle();
					bundle.putSerializable("MOBILEACTIVITY",downLoadFile);
					mobileIntent.putExtras(bundle);
	                ((CloudFilesActivity_1s)mContext).startActivity(mobileIntent);
				}else{
					Toast.makeText(mContext, "请选择要移动的文件",0).show();
				}
			}
		});
        more_bt.setOnClickListener(new OnClickListener() {
        	boolean isvisible ; 
            @Override
            public void onClick(View v) {
                if(isvisible){
                	more_bt.setSelected(false);
                	more_bt.setTextColor(getResources().getColor(R.color.white));
                	button_fenxian.setVisibility(View.GONE);
                	isvisible=false;
                }else{
                	more_bt.setSelected(true);
                	more_bt.setTextColor(getResources().getColor(R.color.more_bt_bg_coler));
                	button_fenxian.setVisibility(View.VISIBLE);
                	isvisible=true;
                }
            }
        });
        if(recIntent!=null){
        	mRootDisk = (CloudFile)recIntent.getParcelableExtra(SELECT_DISK_KEY);
        }
        // Initial path bar
        path_bar_container = (LinearLayout)rootView.findViewById(R.id.pathBarContainer);
        
        setPathbar();
        //listView长点击时间处理
        mListcontext.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 1. Refresh CAB
                if (isMultiSelect()) {
                    // In multiple select mode, didn't handle long item click event
                	//在多个选择模式,没有处理长项点击事件
                	System.out.println("isMultiSelect()="+isMultiSelect()+":"+mSelectedFileList.size());
                } else {
                    // Add selected data and refresh UI 添加选中的数据和更新UI
                    mAdapter.addMultiPosition(position);
                    mAdapter.notifyDataSetChanged();
                    // Show CAB
                    refreshCAB();
                }
                return true;
            }
        });
        //ListView 短点击事件
        mListcontext.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				 CloudFile file = mFileFolderList.get(position-1);
	             if (isMultiSelect()) {
	                 mAdapter.addMultiPosition(position-1);
	                 mAdapter.notifyDataSetChanged();
	                 refreshCAB();
	             } else {
	                 if (file.getFileType() == CloudFileType.TYPE_FILE) {
	                	 switch(ContentntIsFile.isFileType(file.getFilePath())) {
	                	 	case ContentntIsFile.TAB_File_IS_IMAGER1:
	                	 		//图片
	                	 		//System.out.println("图片");
	                	 		 Intent openIntent =new Intent(mContext,ImagePagerActivity.class);
	                	 		 Bundle bundleImage = new Bundle();
	                	 		 String firstPath = wepURL+"Images/Prev/"+file.getThumb_uri_name()+".jpg";
	                	 		 ArrayList<String> imagePath =new ArrayList<String>();
	                	 		 int clicimage = 0;
	                	 		 int com = 0;
	                	 		 for(CloudFile cloudimager:mFileList){
	                	 			 if(ContentntIsFile.TAB_File_IS_IMAGER1==ContentntIsFile.isFileType(cloudimager.getFilePath())){
	                            		String url= wepURL+"Images/Prev/"+cloudimager.getThumb_uri_name()+"-original.jpg";
	                            		if(file.getThumb_uri_name().equals(cloudimager.getThumb_uri_name())){
	                            			clicimage =com;
	                            			System.out.println(file.getThumb_uri_name() +":"+ cloudimager.getThumb_uri_name());
	                            		}
	                            		com++;
	                            		imagePath.add(url);
	                	 			 }
	                	 		 }
	                	 		 openIntent.putStringArrayListExtra("getfirstPathList", imagePath);
	                	 		 openIntent.putExtra("CLICKIMAGE", clicimage);
	                	 		 openIntent.putExtras(bundleImage);
	                	 		 mContext.startActivity(openIntent);
	                	 		break;
	                	 	case ContentntIsFile.TAB_File_IS_MUSIC:
	                	 		//音乐
	                	 		System.out.println("音乐 ----");
	                            String musicName=mFileFolderList.get(position-1).getFilePath();
	                            ArrayList<CloudFile> musicList=new ArrayList<CloudFile>();
	                            Bundle bundlemusic = new Bundle();
	                            Intent musicIntent =new Intent(mContext,OpenMusic.class);
	                            int index=0;
	                            for(CloudFile cloudFile:mFileFolderList){
		                           String misicType=cloudFile.getFilePath().substring(cloudFile.getFilePath().lastIndexOf(".") + 1).toLowerCase();
		                           if("mp3".equals(misicType)){
		                              musicList.add(cloudFile);
		                              index++;
		                              if(mFileFolderList.get(position-1)==cloudFile){
		                            	  System.out.println("index -->"+index);
		                            	  bundlemusic.putInt("OpenMusicIndex",index);
		                              }
		                           }
		                        }
	                            bundlemusic.putSerializable("OpenMusic", (Serializable)musicList);
	                            musicIntent.putExtras(bundlemusic);
	                            mContext.startActivity(musicIntent);
	                	 		break;
	                	 	case ContentntIsFile.TAB_File_IS_file:
	                	 		//文档
	                	 		 System.out.println("文档 "+mFileFolderList.get(position-1));
	                	 		 
	                	 		 Intent intentFile=new Intent(mContext,OpenFiles.class);
	                	 		 Bundle bundleFile = new Bundle();
	                	 		 bundleFile.putSerializable("getCloudFile", (Serializable)mFileFolderList.get(position-1));
	                	 		 intentFile.putExtras(bundleFile);
    	                		 mContext.startActivity(intentFile);
	                	 		break;
	                	 	case ContentntIsFile.TAB_File_IS_VIEW:
	                	 		//视频
	                	 		Intent intentView=new Intent(mContext,VideoPlayer.class );
	                			intentView.putExtra("ShowThings", mFileFolderList.get(position-1).getRemote_id());
		                		mContext.startActivity(intentView);
	                	 		break;
	                	 	case ContentntIsFile.TAB_File_IS_Full:
	                	 		//其他
	                	 		System.out.println("其他");
	                	 		break;
						default:
							break;
						}
	                 } else if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
	                     mPathStack.push(file);
	                     refreshPathBar();
	                     refreshList(true);
	                 }
	             }
			}
        	
        });
        mAdapter = new CloudListAdapter(mContext, R.layout.cloud_item_layout);
        mListcontext.setonRefreshListener(new OnRefreshListener() {
        	
			public void onRefresh() {
				refreshList(false);
			}
		});
        mListcontext.setAdapter(mAdapter);
        refreshPathBar();
        refreshList(true);
        return rootView;
    }
    
    public void sendFile(final ArrayList<WsFile> sendWsFile){
    	CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
    	final WsMessageInfo sendWsMessafeInfo = new WsMessageInfo();
    	sendWsMessafeInfo.Files=sendWsFile;
		builder.setTitle("请选择要发送文件的类型");
		builder.setPositiveButton("发送消息", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent sendIntent = new Intent(mContext,ReplyMail.class);
				sendWsMessafeInfo.setAddresser("mrmsadmin@cheese.com");
				WsGuidOwner recipients = new WsGuidOwner();
				recipients.Email="2576971998@qq.com";
				sendWsMessafeInfo.setRecipients(recipients);
				sendIntent.putExtra(MyConstances.mIsHeiteSenderTitle, true);
				sendIntent.putExtra(LettersActivity.sendermessage, (Serializable)sendWsMessafeInfo);
				mContext.startActivity(sendIntent);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("发送邮件",
				new android.content.DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int which) {
						Intent sendIntent = new Intent(mContext,ReplyMail.class);
						sendWsMessafeInfo.setAddresser("mrmsadmin@cheese.com");
						WsGuidOwner recipients = new WsGuidOwner();
						recipients.Email="2576971998@qq.com";
						sendWsMessafeInfo.setRecipients(recipients);
						sendIntent.putExtra(MyConstances.mIsHeiteSenderTitle, false);
						sendIntent.putExtra(LettersActivity.sendermessage, (Serializable)sendWsMessafeInfo);
						mContext.startActivity(sendIntent);
						dialog.dismiss();
					}
					
				});

		builder.create().show();
    }

    public WsFile wsFileAddCloudFile(CloudFile cloudFile){
    	WsFile wsFile  =new WsFile();
    	wsFile.CreatDate=cloudFile.getCreateDate();
    	wsFile.MD5=cloudFile.getMd5();
    	wsFile.ID=cloudFile.getRemote_id();
    	wsFile.FullName=cloudFile.getFilePath();
    	wsFile.SizeB=cloudFile.getFileSize();
    	FileInfo phyInfo = new FileInfo();
    	phyInfo.setPhyName(cloudFile.getThumb_uri_name());
    	wsFile.phyInfo =phyInfo;
		return wsFile;
    }
	
	private void onOptionsItemSelec() {
		System.out.println(":---:");
	}
	
    private void initPopupWindow() {  
    	View view = View.inflate(mContext, R.layout.fragement_set_actionbar, null); 
        //popuWindow = new PopupWindow(mContext);
        popuWindow = new PopupWindow(mInflater.inflate(R.layout.fragement_set_actionbar, null),
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        screenHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
        popuWindow.setContentView(view);
        popuWindow.setWidth(((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth());
        if(screenHeight>800){
        	popuWindow.setHeight(110);
        }else{
        	popuWindow.setHeight(73);
        }

        //popuWindow.setBackgroundDrawable(R.drawable.cheesecloud_icon);
        // 这里设置显示PopuWindow之后在外面点击是否有效。如果为false的话，那么点击PopuWindow外面并不会关闭PopuWindow。  
        //popuWindow.setOutsideTouchable(true);//不能在没有焦点的时候使用  
        button_cancel = (Button) view.findViewById(R.id.button_cancel);  
        tv_celic_box = (TextView) view.findViewById(R.id.tv_celic_box);  
        button_all_seleck = (Button) view.findViewById(R.id.button_all_seleck);
        button_cancel.setOnClickListener(new OnClickListener(){
		   @Override
		   public void onClick(View v) {
		    // TODO Auto-generated method stub
			   //Toast.makeText(mContext, "button_cancel", 0).show();
			   popuWindow.dismiss();
			   mSelectedFileList.clear();
			   mSelectedPositions.clear();
			   mAdapter.notifyDataSetChanged();
			   refreshCAB();
		   }
         
        });
		button_all_seleck.setOnClickListener(new OnClickListener() {
			boolean select ;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(mContext, "button_all_seleck", 0).show();
				if(select){
					//取消权限设置
					button_all_seleck.setText("全选");
					mSelectedFileList.clear();
					mSelectedPositions.clear();
					select=false;
					mAdapter.notifyDataSetChanged();
				}else{
					//全选设置
					//mSelectedFileList=mFileFolderList;
					mSelectedFileList.clear();
					mSelectedFileList.addAll(mFileFolderList);
					for(int i=0;i<mFileFolderList.size();i++){
						mSelectedPositions.add(i);
					}
					button_all_seleck.setText("全不选");
					select=true;
					mAdapter.notifyDataSetChanged();
				}
				refreshCAB();
			}

		});
    }  
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
            	Toast.makeText(mContext, "销毁 Activity", Toast.LENGTH_SHORT).show();
            	return true;
            case R.id.ab_menu_create_folder:
                Toast.makeText(mContext, "Create folder", Toast.LENGTH_SHORT).show();
                mkdir();
                return true;
            case R.id.ab_menu_upload:
                Toast.makeText(mContext, "Upload", Toast.LENGTH_SHORT).show();
                //upload();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    
    
    private boolean isMultiSelect() {
        return mSelectedFileList.size() > 0;
    }
    

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mContext = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
       // getActivity().getApplication().observable.addObserver(this);
    }
    
    private void setLoadingViewVisible(boolean visible){
        if(null != mLoadingView && null != mListContainer){
            mListContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
            mian_bottom_content.setVisibility(visible ? View.GONE : View.VISIBLE);
            mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if (popuWindow!=null&&popuWindow.isShowing()) {  
            popuWindow.dismiss();  
        }  
    }
    
    @Override
    public void onPullDataReady(int result) {
    	System.out.println("onPullDataReady  zhou le -- "+result);
        setLoadingViewVisible(false);
        mListcontext.onRefreshComplete();
        if(MyConstances.Retrun_file_is_null==result){
        	mRl_file_null.setVisibility(View.VISIBLE);
		}else{
			mRl_file_null.setVisibility(View.GONE);
	    }
    }

    @Override
   	public void onClick(View v) {
   		Log.d(TAG, "Item clicked!");
           int index = Integer.valueOf(v.getTag().toString());
           while (index < (mPathStack.size() - 1)) {
               mPathStack.pop();
               mAdapter.clearMultiSelect();
           }
           refreshPathBar();
           refreshList(true);
           refreshCAB();
   	}
       private void mkdir() {
           dialog = new Dialog(mContext);
           dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
           dialog.setContentView(R.layout.single_input_layout);
           TextView titleView = (TextView)dialog.findViewById(R.id.single_input_dialog_title);
           titleView.setText(this.getString(R.string.cfa_make_dir_dialog_title));
           ImageView mkdir_icon = (ImageView)dialog.findViewById(R.id.input_icon);
           mkdir_icon.setImageResource(R.drawable.folder);
           mkdir_input = (EditText) dialog.findViewById(R.id.input_inputText);
           mkdir_input.setText(R.string.cfa_make_dir_dialog_def_dirName);
           Button mkdir_cancel = (Button) dialog.findViewById(R.id.input_cancel_b);
           Button mkdir_create = (Button) dialog.findViewById(R.id.input_confirm_b);
           mkdir_create.setText(this.getString(R.string.input_layout_create));
           mkdir_create.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   if (mkdir_input.getText().length() < 1) {
                       dialog.dismiss();
                   }
                   String name = mkdir_input.getText().toString();
                   CloudFile file = new CloudFile();
                   file.setFilePath(name);
                   file.setRemote_parent_id(mPathStack.peek().getRemote_id());
                   new CreateDirTask(mContext, null, file, FragmentHomeItme.this).execute();
                   // TODO According name call Web Service create folder API to create target folder
                   dialog.dismiss();
                   setLoadingViewVisible(true);
               }
           });
           mkdir_cancel.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   dialog.dismiss();
               }
           });
           dialog.show();
       }
    

    private void setPathbar() {
        mPathStack.push(mRootDisk);
    }
    private View lanmu;
	private LinearLayout root_itm_view;
	private View rootView;
	private Button menu_bottom_deleter;
	private EditText mkdir_input;
	private Dialog dialog;
	private void refreshCAB() {
        if (isMultiSelect()) {
        	if(screenHeight>800){
        		popuWindow.showAtLocation(rootView, Gravity.TOP+Gravity.LEFT, 0, 45);
        	}else{
        		popuWindow.showAtLocation(rootView, Gravity.TOP+Gravity.LEFT, 0, 30);
        	}
        	String title = getString(R.string.cab_title);
            tv_celic_box.setText(String.format(title, mSelectedFileList.size()));
        } else {
        	if(popuWindow!=null&&popuWindow.isShowing()){
        		popuWindow.dismiss();
        	}
        }
    }
	//加载列表
    private void refreshList(boolean isPull) {
    	if(isPull){
    		setLoadingViewVisible(true);
    	}
        new PullFileListTask(mContext, mAdapter, mPathStack.peek(), mFileList, mFolderList,this).execute();
    }
    
  
    private void refreshPathBar() {
        Log.d(TAG, "Start refresh path bar::"+mPathStack.size());
        int pathBarCount = path_bar_container.getChildCount();
        int pathStackCount = mPathStack.size();
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if (pathBarCount < pathStackCount) {
            // Add extra path to pathBar
            for (int i = pathBarCount; i < pathStackCount; i++) {
                TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
                textView.setTag(i);
                String path = mPathStack.get(i).getFilePath();
                Log.d(TAG, "path " + i + " is " + path);
                textView.setText(path);
                textView.setOnClickListener(this);
                path_bar_container.addView(textView);
            }
        } else if (pathBarCount > pathStackCount) {
            // Remove extra path from pathBar
            for (int i = pathBarCount; i > pathStackCount ; i--) {
                path_bar_container.removeViewAt(i - 1);
            }
        }else if(pathBarCount<1){
        	
        }
    }

    public class ViewHolder {
        public ImageView fileThumb;
        TextView fileNameView;
        TextView fileSizeView;
        TextView fileDateView;
        CheckBox multiSelectCheckBox;
        RelativeLayout rl_root_view;
    }

    private ImageThreadLoad mImageLoader;
    private class CloudListAdapter extends ArrayAdapter<CloudFile> {
        private ItemCheckedListener mCheckedListener    = null;
        private boolean mBusy = false;

    	public void setFlagBusy(boolean busy) {
    		this.mBusy = busy;
    	}
        
        private CloudListAdapter(Context context, int resource) {
            super(context, resource);
            mCheckedListener = new ItemCheckedListener();
            mImageLoader = new ImageThreadLoad();
        }

        @Override
        public int getCount() {
            return mFileFolderList.size();
        }

        @Override
        public void notifyDataSetChanged() {
            mFileFolderList.clear();
            mFileFolderList.addAll(mFolderList);
            mFileFolderList.addAll(mFileList);
            super.notifyDataSetChanged();
            setLoadingViewVisible(false);
            System.out.println("mFolderList :"+mFolderList.size()+"  mFileList:"+mFileList.size()+" mFileFolderList :"+mFileFolderList.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.cloud_item_layout, parent, false);
                holder = new ViewHolder();
                holder.rl_root_view=(RelativeLayout) convertView.findViewById(R.id.rl_root_view);
                holder.fileThumb = (ImageView)convertView.findViewById(R.id.file_thumb);
                holder.fileNameView = (TextView)convertView.findViewById(R.id.file_name_view);
                holder.fileSizeView = (TextView)convertView.findViewById(R.id.file_size_view);
                holder.fileDateView = (TextView)convertView.findViewById(R.id.file_date_view);
                holder.multiSelectCheckBox = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                holder.multiSelectCheckBox.setOnCheckedChangeListener(mCheckedListener);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            // Update holder content
            CloudFile file = mFileFolderList.get(position);
            // multiSelectCheckBox
            holder.multiSelectCheckBox.setTag(position);
            if (mSelectedPositions != null && mSelectedPositions.contains(position)){
                holder.multiSelectCheckBox.setChecked(true);
            	holder.rl_root_view.setBackgroundColor(getResources().getColor(R.color.list_item_select_bj));
            }else{
                holder.multiSelectCheckBox.setChecked(false);
                holder.rl_root_view.setBackgroundColor(getResources().getColor(R.color.white));
            }

            if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
                holder.fileThumb.setImageResource(R.drawable.folder);
                holder.fileSizeView.setVisibility(View.GONE);
            } else {
            	holder.fileSizeView.setVisibility(View.VISIBLE);
            	if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType(file.getFilePath()) || ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType(file.getFilePath())){
            		holder.fileThumb.setImageResource(ThumbnailCreator.getDefThumbnailsByName(file.getFilePath()));
            	}else{
            		String imgName=file.getThumb_uri_name()+".jpg";
            		String url= wepURL+"Images/Prev/"+imgName;
            		System.out.println("url __-->"+url);
            		imageLoader.DisplayImage(url, holder.fileThumb);
            	}
                //holder.fileSizeView.setText(FlowConverter.Convert() + "  ");
                holder.fileSizeView.setText(FlowConverter.Convert(file.getFileSize()*1024));
            }
            // fileNameView
            holder.fileNameView.setText(file.getFilePath());
            // fileDateView
            holder.fileDateView.setText(file.getCreateDate());
            return convertView;
        }
        
        public void addMultiPosition(int index) {
            CloudFile file = mFileFolderList.get(index);
            if (mSelectedPositions.contains(index)) {
                mSelectedPositions.remove(Integer.valueOf(index));
                mSelectedFileList.remove(file);
            } else {
                mSelectedPositions.add(index);
                mSelectedFileList.add(file);
            }
        }
        /**
         * 这将关闭多选和隐藏多选按钮
         * This will turn off multi-select and hide the multi-select buttons at the
         * bottom of the view.
         */
        
        public void clearMultiSelect() {
            // TODO Handle multiple select
            if(mSelectedPositions != null && !mSelectedPositions.isEmpty())
                mSelectedPositions.clear();

            if(mSelectedFileList != null && !mSelectedFileList.isEmpty())
                mSelectedFileList.clear();
        }
        /**
         * This class listening ListView item's select CheckBox checked event.
         * When user checked a item, class add this item's index to {@link #mSelectedPositions},
         * and add path which the item stand for to {@link #mSelectedFileList}
         */
        private class ItemCheckedListener implements CompoundButton.OnCheckedChangeListener{
            //private static final String TAG     = "ItemSelectedListener";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Index: " + buttonView.getTag() + "\nChecked: " + isChecked);
                int r_index = Integer.valueOf(buttonView.getTag().toString());
                if (isChecked) {
                    if (!mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.add(r_index);
                        mSelectedFileList.add(mFileFolderList.get(r_index));
                    }
                } else {
                    if (mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.remove((Integer)r_index);
                        mSelectedFileList.remove(mFileFolderList.get(r_index));
                    }
                }
                refreshCAB();
                notifyDataSetChanged();
            }
        }
    }

    private void delFile() {
        // TODO the mSelectedFileList will be cleared sometime, so new a ArrayList, add item one by one
        ArrayList<CloudFile> mFiles = new ArrayList<CloudFile>();
        for (CloudFile file : mSelectedFileList)
            mFiles.add(file);
        new DeleteFileTask(mContext, null, mFiles, FragmentHomeItme.this).execute();
        setLoadingViewVisible(true);
    }
    
    /*private void upload() {
        Intent intent = new Intent();
        intent.setClass(this, SelectUploadActivity.class);
        startActivityForResult(intent, 0);
    }*/
    /**
     * Scan selected file list, and insert the files and sub files
     * to download_files table recursively.
     */
    private class DownloadFilesTask extends AsyncTask<Void, Void, Integer> {

        public static final int SCAN_SUCCESS    = 0;
        public static final int SCAN_FAILED     = 1;

        private ArrayList<CloudFile> mFileList          = null;
        private DownloadFileDataSource mDataSource      = null;

        public DownloadFilesTask(ArrayList<CloudFile> fileList) {
            mFileList = fileList;
            mDataSource = new DownloadFileDataSource(mContext);
        }

        @Override
        protected void onPreExecute() {
            mDataSource.open();
        }

        protected Integer doInBackground(Void... params) {
            for (CloudFile file: mFileList) {
                Log.d(TAG, "scan: " + file.getFilePath());
                System.out.println("PhyName :"+file.getThumb_uri_name());
                scan(file);
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
                    Toast.makeText(mContext, "扫描插入完成", Toast.LENGTH_SHORT).show();
                    DownloadService.startActionStartAll(mContext);
                    break;
                case SCAN_FAILED:
                    break;
                default:
                    break;
            }
            mDataSource.close();
        }
        
        
        private void scan(CloudFile file) {
            Log.d(TAG, "Scan");
            int result = WsResultType.Success; // Web service call result
            if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
                // Concat the the full path
                // TODO Scan sub directory
                ArrayList<CloudFile> fileList = new ArrayList<CloudFile>();
                ArrayList<CloudFile> folderList = new ArrayList<CloudFile>();
                result = ClientWS.getInstance(mContext).getFolderList_wrapper(file, fileList, folderList);
                if (result == WsResultType.Success) {
                    for (CloudFile tmp_file : fileList)
                        scan(tmp_file);
                    for (CloudFile tmp_file : folderList)
                        scan(tmp_file);
                }
            }
            // If is file;
            // 1. insert record on local table
            else if (file.getFileType() == CloudFileType.TYPE_FILE){
                DownloadFile r_file = DownloadFileDataSource.convertToDownloadFile(file);
                // The filePath property pull from server just have the file name,
                // in there, we add the parent folder path in the header
                r_file.setState(DownloadFileState.WAIT_DOWNLOAD);
                System.out.println("r_file >> name "+r_file.getThumb_uri_name());
                if (mDataSource.addDownloadFile(r_file)) {
                    Log.d(TAG, "Scan download files: add " + r_file.getFilePath() + " to download_files table success!");
                }
            }
            return;
        }
    }
    
    //创建文件夹完成后回调
	@Override
	 public void onCreateFolderCompleted(int result) {
        if (result == WsResultType.Success) {
            Log.d(TAG, "Create folder completed! refresh list");
            refreshList(true);
        } else {
            Log.d(TAG, "Create folder failed: " + result);
            mAdapter.notifyDataSetChanged();
        }
    }
	//重命名完成后回调
	@Override
	public void onRenameFileCompleted(int result) {
		// TODO Auto-generated method stub
		 mAdapter.notifyDataSetChanged();
	}

	//删除文件完成回调
	@Override
    public void onDeleteFileCompleted(int result) {
        if (result == WsResultType.Success) {
            Log.d(TAG, "Delete files/folders completed! refresh list");
            refreshList(true);
            mSelectedFileList.clear();
        } else {
            Log.d(TAG, "Delete files/folders failed: " + result);
            mAdapter.notifyDataSetChanged();
        }
    }
	
    //ViewPager监听
	private class MyVeiwPagerListener implements MyCallInterface{

		@Override
		public void viewPagerListener() {
			// TODO Auto-generated method stub
			if(popuWindow!=null&& popuWindow.isShowing()){
				mSelectedFileList.clear();
				mSelectedPositions.clear();
				mAdapter.notifyDataSetChanged();
	    		refreshCAB();
	    	}
		}
	}
	
	@Override
    public boolean onBackKeyDown() {
		if(mPathStack.size()>1){
			 mPathStack.remove(mPathStack.size()-1);
			 refreshPathBar();
			 refreshList(true);
			 refreshCAB();
			 return true;
		}else{
			return false;
		}
    }

	@Override
	public void onBackHandledInterface(int result) {
        refreshPathBar();
	}
	
	//屏幕触摸事件回调接口
	@Override
	public void onTouchEvent(MotionEvent event) {
		
	}
	
}