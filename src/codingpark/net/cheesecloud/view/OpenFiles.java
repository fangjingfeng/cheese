package codingpark.net.cheesecloud.view;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;
import codingpark.net.cheesecloud.utils.CallOtherOpeanFile;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.utils.MyUtils;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.wsi.SyncFileBlock;
import codingpark.net.cheesecloud.wsi.WsSyncFile;

public class OpenFiles extends Activity{
	public static final int DOWNLOAD_BLOCK_SIZE             = CheeseConstants.KB;
	private ImageView icon; 
	private String docName ;
	private CloudFile cloudFile;
	private LinearLayout l_left_zone;
	private Button downC1lic;
	private FrameLayout botton;
	private TextView downloadStatus;
	private ProgressBar progressBar;
	private TextView job_downloading;
	private ImageView stop_download;
	private Handler handler;
	private File r_file;
	private MyBroadcast broadcastReceiver;
	private DownloadFile downLoadFile;
	private DownloadFileDataSource downloadFileDataSource;
	private boolean isDownLoad ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.download); 
		Bundle  bundle=getIntent().getExtras();
		//int Cannot=(Integer) bundle.get("getCloudFile");
		Intent file =getIntent();
		if(file.getSerializableExtra("getCloudFile")!=null){
			cloudFile=(CloudFile)file.getSerializableExtra("getCloudFile");
			docName=cloudFile.getFilePath();
		}
		File picFileDir = new File(Environment.getExternalStorageDirectory().toString()+File.separator +"testCamera");//仅创建路径的File对象
		  if(!picFileDir.exists()){
			   //如果路径不存在就先创建路径
		       picFileDir.mkdir();
		}
		String fileName = MyUtils.md5(cloudFile.getFilePath());
		r_file = new File(picFileDir,fileName);
		downloadFileDataSource = new DownloadFileDataSource(this);
	    downloadFileDataSource.open();
	   initUI(); 
	   handler();
	   super.onCreate(savedInstanceState);
	}

	//初始化ui
	public void initUI(){
		icon = (ImageView) findViewById(R.id.icon);
		l_left_zone = (LinearLayout) findViewById(R.id.left_zone);
		TextView title_left_txt=(TextView)findViewById(R.id.title_left_txt);
		title_left_txt.setText("返回");
		TextView title=(TextView)findViewById(R.id.title);
		title.setText("文件信息");
		downC1lic = (Button)findViewById(R.id.downClic);
		downC1lic.setBackgroundResource(R.drawable.btn_blue);
		TextView fileName =(TextView)findViewById(R.id.fileName);
		fileName.setText(cloudFile.getFilePath());
		TextView file_size =(TextView)findViewById(R.id.file_size);
		file_size.setText(FlowConverter.Convert(cloudFile.getFileSize()*1024));
		TextView file_mtime =(TextView)findViewById(R.id.file_mtime);
		file_mtime.setText(cloudFile.getCreateDate());
		botton = (FrameLayout)findViewById(R.id.bottom);
		//---
		downloadStatus = (TextView)findViewById(R.id.downloadStatus);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		job_downloading = (TextView)findViewById(R.id.job_downloading);
		stop_download = (ImageView)findViewById(R.id.stop_download);
		
		job_downloading.setText("0B/"+FlowConverter.Convert(cloudFile.getFileSize()*1024));
        progressBar.setMax((int) cloudFile.getFileSize()*1024);
        downC1lic.setBackgroundResource(R.drawable.btn_blue_pressed);
	}
	
	//打开文件
	public void handler(){
		broadcastReceiver=new MyBroadcast();
        IntentFilter filter = new IntentFilter("codingpark.net.cheesecloud.handle.ACTION_DOWNLOAD_STATE_CHANGE");  
        //注册广播接收器  
        registerReceiver(broadcastReceiver, filter);
		String imagerId =ContentntIsFile.getFileType(r_file);
	    int imgid = getResources().getIdentifier(imagerId, "drawable", "codingpark.net.cheesecloud");
		icon.setBackgroundResource(imgid);
		  //-----  Actionbar返回按钮监听
		  l_left_zone.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	finish();
	            }
	        });
		  //---- 点击下和预览加载操作
		  if(downloadFileDataSource.sqleDownLoadFile(cloudFile.getRemote_id(),cloudFile.getMd5())){
			  isDownLoad=true;
			  downC1lic.setText("点击预览");
		  }else{
			  isDownLoad=false;
			  downC1lic.setText(getResources().getString(R.string.loadingFile));
		  }
		  
		  downC1lic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				  if(isDownLoad){
					  //点击预览
					  openFile(r_file);
				  }else{
					//显示进度
					  downloadStatus.setVisibility(View.VISIBLE);
					  progressBar.setVisibility(View.VISIBLE);
					  job_downloading.setVisibility(View.VISIBLE);
					  stop_download.setVisibility(View.VISIBLE);
					  //点击下载。
					  statIntent();
				  }
			}
		  });
	}
	
	//开启服务下载文件
	public void statIntent(){
		System.out.println("开启服务下载文件");
		cloudFile.setState(DownloadFileState.DOWNLOADING);
		downloadFileDataSource.addDownloadFile(downloadFileDataSource.convertToDownloadFile(cloudFile));
		Intent serviceIntent=new Intent();
  		serviceIntent.setClass(this, DownloadService.class);
  		serviceIntent.setAction(DownloadService.ACTION_START_ALL_DOWNLOAD);
  		this.startService(serviceIntent);
	 }

	 //广播接受者
	 public class MyBroadcast extends BroadcastReceiver {
		 @Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			downLoadFile = (DownloadFile) bundle.getSerializable(DownloadService.EXTRA_DOWNLOAD_FILE);
			if(downLoadFile!=null){
				System.out.println("downLoadFile::::"+downLoadFile.getChangedSize());
                job_downloading.setText(FlowConverter.Convert(downLoadFile.getChangedSize())+"/"+FlowConverter.Convert(cloudFile.getFileSize()*1024));
                progressBar.setProgress((int) downLoadFile.getChangedSize());
				if(downLoadFile.getState()== 3){
					//下载完成 --
					 openFile(r_file);
				}
			}
		}
	 }
	public void openFile(File filePath){
		CallOtherOpeanFile openFile=new CallOtherOpeanFile();
		openFile.openFile(OpenFiles.this, filePath);
	}

	@Override
	protected void onDestroy() {
		 super.onDestroy();
		 unregisterReceiver(broadcastReceiver);
	}
}
