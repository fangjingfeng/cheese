package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.view.imegeutils.ImageLoader;
import codingpark.net.cheesecloud.wsi.FileInfo;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsMessageInfo;

/**
 * The class used to list all folder in web server, user select one folder,
 * then return the folder information to {@link SelectUploadActivity}.
 * It upload files to the destination.
 * When user click select_path_ok_bt, the class call mPathStack.peek(), add the result to intent
 * which will received by UploadActivity. Default destination folder is null, so when UploadActivity
 * receive a null folder, it will use the user id as the destination folder id(My Cloud Folder).
 */
public class SelectPathActivity extends ListActivity implements View.OnClickListener, PullFileListTask.OnPullDataReadyListener {
    private static final String TAG     = SelectPathActivity.class.getSimpleName();

    private Button select_path_cancel_bt    = null;
    private Button select_path_ok_bt        = null;

    public static final String RESULT_SELECTED_REMOTE_FOLDER_ID    = "selected_remote_folder_id";
    public static final String RESULR_SELECTES_REMOTE_FOLDER_NAME    = "selected_remote_folder_name";
    private ArrayList<CloudFile> mFolderList           = null;
    private Stack<CloudFile> mPathStack                = null;
    // Path bar, use to show current directory path
    private LinearLayout path_bar_container = null;

    private LinearLayout mListContainer                 = null;
    private ProgressBar mLoadingView                    = null;
    private ArrayList<CloudFile> mFileList              = null;
    private ArrayList<CloudFile> mFileFolderList        = null; 
    private ImageLoader imageLoader;
    private boolean isDisplayFIle  = false;
    //用于保存选中的文件对象
    private ArrayList<CloudFile> selectFileClouFile  = null;

	private TextView sendFile;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.select_path_activity_action_bar_title);
        ImageView view = (ImageView)findViewById(android.R.id.home);
        view.setPadding(0, 0, 0, 0);
        isDisplayFIle =getIntent().getBooleanExtra("ISDISPYAFILE", false);
        System.out.println(":"+getIntent().getBooleanExtra("ISDISPYAFILE", false));
        
        selectFileClouFile = new ArrayList<CloudFile>();
        // Initial path list
        mFolderList = new ArrayList<CloudFile>();
        mFileList=new ArrayList<CloudFile>();
        mPathStack = new Stack<CloudFile>();
        mFileFolderList =new ArrayList<CloudFile>();
        // Initial mPathStack with ROOT_ID when not select any folder
        CloudFile file = new UploadFile();
        file.setRemote_id(CheeseConstants.ROOT_ID);
        file.setFilePath("磁盘");
        mPathStack.push(file);
        imageLoader=new ImageLoader(this);
        
        initUI();
        //initActionBar();
        initHandler();
        refreshPathBar();
        refreshList();
        refreshBottomBar();
    }

    private void refreshList() {
        setLoadingViewVisible(true);
        if(mPathStack.size()>1){
        	SelectPathItemyAdapter mAdapter=new SelectPathItemyAdapter();
        	setListAdapter(mAdapter);
            new PullFileListTask(this, mAdapter, mPathStack.peek(), mFileList, mFolderList, this).execute();
        }else{
        	SelectPathAdapter mAdapter = new SelectPathAdapter();
        	setListAdapter(mAdapter);
            new PullFileListTask(this, mAdapter, mPathStack.peek(), null, mFolderList, this).execute();
        }
    }

    private void initUI() {
    	 mLoadingView = (ProgressBar)findViewById(R.id.loading);
         mListContainer = (LinearLayout)findViewById(R.id.listcontainer);
         
         sendFile = (TextView) findViewById(R.id.textView);
         
         select_path_cancel_bt = (Button) findViewById(R.id.select_upload_path_cancel_bt);
         select_path_ok_bt = (Button) findViewById(R.id.select_upload_path_ok_bt);
         path_bar_container = (LinearLayout) findViewById(R.id.pathBarContainer);
         
    }

    private void initHandler() {
        select_path_cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel select path action!");
                setResult(RESULT_CANCELED);
                SelectPathActivity.this.finish();
            }
        });
        select_path_ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Select this path!");
                // Return selected remote folder id to UploadActivity
                if(isDisplayFIle){
                	ArrayList<WsFile> listWsFile = new ArrayList<WsFile>();
                	Intent intents = new Intent();
                	if(selectFileClouFile!=null && selectFileClouFile.size()>0){
                		for(CloudFile cloud:selectFileClouFile ){
                			listWsFile.add(wsFileAddCloudFile(cloud));
           			 	}
                		Bundle  bundle  = new Bundle();
                		bundle.putSerializable(MyConstances.PutSendFile,listWsFile);
                		intents.putExtras(bundle);
                	}else{
                		Toast.makeText(SelectPathActivity.this,"选择文件为空" ,0).show();
                	}
                	setResult(RESULT_OK, intents);
                	System.out.println("name - - zhoule  >");
                	
                }else{
                	Intent intent = new Intent();
                	if (mPathStack.peek() != null) {
                        String id = mPathStack.peek().getRemote_id();
                        if (id.equals(CheeseConstants.ROOT_ID)) {
                            intent.putExtra(RESULT_SELECTED_REMOTE_FOLDER_ID,AppConfigs.current_remote_user_id);
                        }else{
                            intent.putExtra(RESULT_SELECTED_REMOTE_FOLDER_ID, id);
                            intent.putExtra(RESULR_SELECTES_REMOTE_FOLDER_NAME, mPathStack.peek().getFilePath());
                        }
                    }else {
                        intent.putExtra(RESULT_SELECTED_REMOTE_FOLDER_ID,AppConfigs.current_remote_user_id);
                    }
                	setResult(RESULT_OK, intent);
                }
                SelectPathActivity.this.finish();
            }
        });
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_path, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	CloudFile file = mFileFolderList.get(position);
    	if(isDisplayFIle){
    		mPathStack.push(file);
	        refreshPathBar();
	        refreshList();
	        refreshBottomBar();
    	}else{
    		//判断是否是文件夹？
    		if(file.getFileType()== CloudFileType.TYPE_FOLDER){
       		 // 1. Refresh bottom bar select path button text
       	        mPathStack.push(file);
       	        refreshPathBar();
       	        refreshList();
       	        refreshBottomBar();
    		}
    	}
    }

    private void refreshBottomBar() {
    	if(!isDisplayFIle){
    		 if (mPathStack.size() > 1)
    	            select_path_ok_bt.setText(
    	                    getString(R.string.select_path_activity_ok_bt_prefix_string)
    	                            + mPathStack.peek().getFilePath());
    	        else
    	            select_path_ok_bt.setText(
    	                    getString(R.string.select_path_activity_ok_bt_prefix_string));
    	}else{
    		select_path_ok_bt.setText("确定");
    	}
       
    }
    private void refreshPathBar() {
    	
        Log.d(TAG, "Start refresh path bar");
        int pathBarCount = path_bar_container.getChildCount();
        int pathStackCount = mPathStack.size();
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
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
        }
    }

    /**
     * Listen path bar item click event, when user click the item, list
     * view would be switch to the folder which the item stand for.
     * @param v The view(path bar item)
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "Item clicked!");
        int index = Integer.valueOf(v.getTag().toString());
        while (index < (mPathStack.size() - 1))
            mPathStack.pop();
        refreshPathBar();
        refreshList();
        refreshBottomBar();
    }

    private void setLoadingViewVisible(boolean visible){
        if(null != mLoadingView && null != mListContainer){
            mListContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
            mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPullDataReady(int result) {
    	System.out.println("onPullDataReady ---- "+result);
        setLoadingViewVisible(false);
    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        ImageView icon;
        TextView rightView;
    }

    public class SelectPathAdapter extends ArrayAdapter<CloudFile> {

        public SelectPathAdapter() {
            super(SelectPathActivity.this, R.layout.upload_item_layout, mFolderList);
        }
        @Override
        public void notifyDataSetChanged() {
        	mFileFolderList.clear();
        	mFileFolderList.addAll(mFolderList);
        	super.notifyDataSetChanged();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) SelectPathActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.select_path_item_layout, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.file_thumb);
                // Update icon src
                holder.icon.setImageResource(R.drawable.tab_home_cloud_disk_item_icon_normal_img);
                holder.rightView = (TextView)convertView.findViewById(R.id.fileNameView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.rightView.setText(mFolderList.get(position).getFilePath());
            return convertView;
        }
    }
    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolderItem {
        ImageView icon;
        TextView folerName;
        ImageView isHedden;
        CheckBox select_sender_file;
    }

    public class SelectPathItemyAdapter extends ArrayAdapter<CloudFile> {
        public SelectPathItemyAdapter() {
            super(SelectPathActivity.this, R.layout.upload_item_layout, mFileFolderList);
        }
        
        @Override
        public void notifyDataSetChanged() {
        	mFileFolderList.clear();
        	mFileFolderList.addAll(mFolderList);
        	mFileFolderList.addAll(mFileList);
        	super.notifyDataSetChanged();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolderItem holderIten;
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) SelectPathActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.update_foler_item, parent, false);
                holderIten = new ViewHolderItem();
                holderIten.icon = (ImageView)convertView.findViewById(R.id.file_thumb);
                holderIten.isHedden=(ImageView)convertView.findViewById(R.id.is_file_hidden);
                holderIten.folerName = (TextView)convertView.findViewById(R.id.file_name_view);
                holderIten.select_sender_file =(CheckBox) convertView.findViewById(R.id.select_sender_file);
                convertView.setTag(holderIten);
            } else {
            	holderIten = (ViewHolderItem)convertView.getTag();
            }
            final CloudFile cloudFile = mFileFolderList.get(position);
            holderIten.folerName.setText(cloudFile.getFilePath());
            if (cloudFile.getFileType() == CloudFileType.TYPE_FOLDER) {
            	holderIten.icon.setImageResource(R.drawable.folder);
            	holderIten.isHedden.setVisibility(View.GONE);
            	holderIten.folerName.setTextColor(getResources().getColor(R.color.black));
            	holderIten.select_sender_file.setVisibility(View.GONE);
            } else {
            	if(!isDisplayFIle){
            		holderIten.isHedden.setVisibility(View.VISIBLE);
                	holderIten.folerName.setTextColor(getResources().getColor(R.color.file_ishedden));
                	holderIten.select_sender_file.setVisibility(View.GONE);
            	}else{
            		holderIten.select_sender_file.setVisibility(View.VISIBLE);
            		holderIten.select_sender_file.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
							//checkBox 点击事件监听
							if(isChecked){
								System.out.println("name --->选中");
								selectFileClouFile.add(cloudFile);
							}else{
								System.out.println("name --->取消选中");
								selectFileClouFile.remove(cloudFile);
							}
							refreshUI(selectFileClouFile);
						}
            	    });
            	}
            	if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType(cloudFile.getFilePath()) || ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType(cloudFile.getFilePath())){
            		holderIten.icon.setImageResource(ThumbnailCreator.getDefThumbnailsByName(cloudFile.getFilePath()));
            	}else{
            		String imgName=cloudFile.getThumb_uri_name()+".jpg";
            		String url= "Http://58.116.52.8:8977/Images/Prev/"+imgName;
            		imageLoader.DisplayImage(url, holderIten.icon);
            	}
            }
            return convertView;
        }
    }
    public void refreshUI(ArrayList<CloudFile> selectFileClouFile){
    	sendFile.setText("已经选择文件："+selectFileClouFile.size()+"个");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
    		System.out.println("mPathStack.size():"+mPathStack.size());
    		if(mPathStack.size()>1){
    			mPathStack.remove(mPathStack.size()-1);
    			refreshPathBar();
    	        refreshList();
    	        refreshBottomBar();
    	        return true;
    		}else{
    			finish();
    		}
    	}  
    	return false;
    }
}
