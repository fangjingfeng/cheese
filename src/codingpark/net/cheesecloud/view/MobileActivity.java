package codingpark.net.cheesecloud.view;

import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.CreateDirTask;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.handle.CreateDirTask.OnCreateFolderCompletedListener;
import codingpark.net.cheesecloud.view.SelectPathActivity.SelectPathAdapter;
import codingpark.net.cheesecloud.view.SelectPathActivity.SelectPathItemyAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 本类》实现对列表文件的移动功能 
 * @author 123
 */
public class MobileActivity extends ListActivity implements View.OnClickListener, PullFileListTask.OnPullDataReadyListener,OnCreateFolderCompletedListener {
	private static final String TAG     = SelectPathActivity.class.getSimpleName();
	
	private ArrayList<CloudFile> downLoadFile;
	private Stack<CloudFile> mPathStack                = null;
	private ArrayList<CloudFile> mFolderList;
	
	//UI
	private ProgressBar  mLoadingView;
	private LinearLayout mListContainer;
	private Button mobile_target_forder;
	private  Button cancel ;
	private LinearLayout path_bar_container;
	
	private MyHandler myHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mobile);
		downLoadFile = (ArrayList<CloudFile>) getIntent().getSerializableExtra("MOBILEACTIVITY");
        initUI();
        handler();
        
        refreshPathBar();
        refreshList();
        super.onCreate(savedInstanceState);
	}
	
	public void initUI(){
		ImageView add_folder = (ImageView) findViewById(R.id.add_folder);
		//信件文件
		 add_folder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mkdir();
			}
		});
		 
		 TextView ib_playback =(TextView) findViewById(R.id.ib_playback);
		 ib_playback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		 mLoadingView = (ProgressBar)findViewById(R.id.loading);
         mListContainer = (LinearLayout)findViewById(R.id.listcontainer);

         mobile_target_forder = (Button) findViewById(R.id.mobile_target_forder);
         mobile_target_forder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("转移文件~~~zhong ");
				myHandler = new MyHandler();
				//点击转移文件
				new MobileThread(downLoadFile,mPathStack.get(mPathStack.size()-1).getRemote_id()).start();
			}
		});
         cancel = (Button) findViewById(R.id.cancel);
         cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//添加链接
				finish();
			}
		});
         path_bar_container = (LinearLayout) findViewById(R.id.pathBarContainer);
	}
	
	public void handler(){
		mFolderList = new ArrayList<CloudFile>();
	    mPathStack = new Stack<CloudFile>();
	    // Initial mPathStack with ROOT_ID when not select any folder
        CloudFile file = new UploadFile();
        file.setRemote_id(CheeseConstants.ROOT_ID);
        file.setFilePath("磁盘");
        mPathStack.push(file);
        
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 1. Refresh bottom bar select path button text
        CloudFile file = mFolderList.get(position);
        mPathStack.push(file);
        StringBuffer stringBuffer =new StringBuffer();
        refreshPathBar();
        refreshList();
        //refreshBottomBar();
        System.out.println("mPathStack =="+mPathStack.size());
        for(CloudFile cloudfile:mPathStack){
        	System.out.println("getRemote_id =="+cloudfile.getRemote_id());
        	stringBuffer.append("/"+cloudfile.getFilePath());
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
	private Dialog dialog ;
	private EditText mkdir_input;

	private CloudFile folder;
	 private void mkdir() {
         dialog = new Dialog(this);
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
                 new CreateDirTask(MobileActivity.this, null, file, MobileActivity.this).execute();
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
	 
	 private void refreshList() {
	        setLoadingViewVisible(true);
	        if(mPathStack.size()>1){
	        	SelectPathItemyAdapter mAdapter=new SelectPathItemyAdapter();
	        	setListAdapter(mAdapter);
	            new PullFileListTask(this, mAdapter, mPathStack.peek(), null, mFolderList, this).execute();
	        }else{
	        	SelectPathAdapter mAdapter = new SelectPathAdapter();
	        	setListAdapter(mAdapter);
	            new PullFileListTask(this, mAdapter, mPathStack.peek(), null, mFolderList, this).execute();
	        }
	}
	 
	 @Override
	    public void onClick(View v) {
	        Log.d(TAG, "Item clicked!");
	        int index = Integer.valueOf(v.getTag().toString());
	        while (index < (mPathStack.size() - 1))
	            mPathStack.pop();
	        refreshPathBar();
	        refreshList();
	        //refreshBottomBar();
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
	            super(MobileActivity.this, R.layout.upload_item_layout, mFolderList);
	        }
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            ViewHolder holder;
	            if(convertView == null) {
	                LayoutInflater inflater = (LayoutInflater) MobileActivity.this.
	                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	    }

	    public class SelectPathItemyAdapter extends ArrayAdapter<CloudFile> {
	        public SelectPathItemyAdapter() {
	            super(MobileActivity.this, R.layout.upload_item_layout, mFolderList);
	        }
	        
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	        	ViewHolderItem holderIten;
	            if(convertView == null) {
	                LayoutInflater inflater = (LayoutInflater) MobileActivity.this.
	                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                convertView = inflater.inflate(R.layout.update_foler_item, parent, false);
	                holderIten = new ViewHolderItem();
	                holderIten.icon = (ImageView)convertView.findViewById(R.id.file_thumb);
	                holderIten.icon.setImageResource(R.drawable.folder);
	                holderIten.folerName = (TextView)convertView.findViewById(R.id.file_name_view);
	                convertView.setTag(holderIten);
	            } else {
	            	holderIten = (ViewHolderItem)convertView.getTag();
	            }
	            holderIten.folerName.setText(mFolderList.get(position).getFilePath());
	            return convertView;
	        }
	    }
	  //创建文件夹完成后回调
		@Override
		 public void onCreateFolderCompleted(int result) {
	        if (result == WsResultType.Success) {
	            Log.d(TAG, "Create folder completed! refresh list");
	            refreshList();
	        } else {
	           Toast.makeText(this, "创建文件失败！", 0).show();
	        }
	    }
		
		class MyHandler extends Handler {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Toast.makeText(MobileActivity.this, "文件移动成功", 0).show();
				finish();
			}
		}
		
		//String ids, String types, String identity, String parentID
		class MobileThread extends Thread{
			private ArrayList<CloudFile> downLoadFile;
			private String parentID;
			public MobileThread(ArrayList<CloudFile> downLoadFile,String parentID){
				this.downLoadFile = downLoadFile;
				this.parentID = parentID;
			}
			@Override
			public void run() {
				int result = WsResultType.Faild;
				String types = null;
				for(CloudFile file :downLoadFile){
					System.out.println("parentID ="+parentID+": file.getFileType()="+file.getFileType()+ ": file.getRemote_id()="+file.getRemote_id());
					switch (file.getFileType()) {
					case 0:
						types="file";
						break;
					case 1:
						types="folder";
						break;
					}
					result =ClientWS.getInstance(MobileActivity.this).pasteObj(file.getRemote_id(),types,"copy",parentID);
					if(result==WsResultType.Success){
						Message message =new Message();
						myHandler.sendMessage(message);
					}
				}
				super.run();
			}
		}
}
