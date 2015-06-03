package codingpark.net.cheesecloud.view;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.MediaEngine;
import codingpark.net.cheesecloud.handle.MediaService;
import codingpark.net.cheesecloud.wsi.SyncFileBlock;
import codingpark.net.cheesecloud.wsi.WsSyncFile;

public class OpenMusic extends Activity implements OnClickListener{
	private ArrayList<CloudFile> mFileFolderList;
	private File r_file;
	private WsSyncFile syncFile;
	private int indext=0;
	private TextView ib_playback;
	private ImageView iv_playcircle;
	private TextView textView1;
	private TextView currTimeTextView;
	private TextView totalTimeTextView;
	private TextView tv_buffering;
	private SeekBar progressSeekBar;
	private ImageButton pausebtn;
	private ImageButton prevbtn;
	private ImageButton nextbtn;
	private NotificationManager manager;
	private MediaEngine mEngine;
	
	private static OpenMusic sActivity;
	
	public static Handler handlers =new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConstances.MUSIC_PROGRESS:
				//开始播放进度  更新UI
				 Bundle data = msg.getData();
                 int duration = data.getInt("duration");
                 int currentPos = data.getInt("currentPos");

                 String strDuration = MediaEngine.timeFormat(duration);
                 String strCurrentPos = MediaEngine.timeFormat(currentPos);
                 
                 System.out.println("duration--->"+duration+":"+"currentPos ---->"+currentPos);
                 sActivity.totalTimeTextView.setText(strDuration);
                 sActivity.currTimeTextView.setText(strCurrentPos);
                 sActivity.progressSeekBar.setMax(duration);
                 sActivity.progressSeekBar.setProgress(currentPos);
				break;
				
			default:
				break;
			}
		};
	};
	private SharedPreferences stack_play;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.music_play_acticity);
	    Intent musicIntent =this.getIntent();
	    if(musicIntent.getExtras().get("OpenMusic")!=null){
	    	mFileFolderList = (ArrayList<CloudFile>) musicIntent.getSerializableExtra("OpenMusic");
	    	indext = musicIntent.getIntExtra("OpenMusicIndex", -1);
	    }
	    sActivity=this;
	    stack_play = getSharedPreferences("state", Activity.MODE_PRIVATE);
	    init();
	    handler();
	    DownLoadfile  downloadFile= new DownLoadfile(mFileFolderList.get(indext-1));
	    downloadFile.execute();
		super.onCreate(savedInstanceState);
	}

	public void init(){
		ib_playback = (TextView)findViewById(R.id.ib_playback);
		iv_playcircle = (ImageView)findViewById(R.id.iv_playcircle);
		textView1 = (TextView)findViewById(R.id.textView1);
		currTimeTextView = (TextView)findViewById(R.id.currTimeTextView);
		totalTimeTextView = (TextView)findViewById(R.id.totalTimeTextView);
		
		tv_buffering=(TextView)findViewById(R.id.tv_buffering);
		progressSeekBar = (SeekBar)findViewById(R.id.progressSeekBar);
		pausebtn = (ImageButton)findViewById(R.id.pausebtn);
		prevbtn = (ImageButton)findViewById(R.id.prevbtn);
		prevbtn = (ImageButton)findViewById(R.id.nextbtn);
		
	}
	
	public void handler(){
		ib_playback.setOnClickListener(this);
		iv_playcircle.setOnClickListener(this);
		pausebtn.setOnClickListener(this);
		prevbtn.setOnClickListener(this);
		prevbtn.setOnClickListener(this);
		
		//进度监听
		progressSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				//System.out.println("进度条发生了改变");
			}
			//触摸时调用
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				//System.out.println("触摸中");
			}
			//完成一个触摸手势时调用
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				//System.out.println("触摸完成");
				startMediaService(null, MyConstances.MUSIC_SEEK, progressSeekBar.getProgress());
			}
		});
		
	}
	//按钮的点击监听
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_playback:
			//返回处理
			finish();
			break;
		case R.id.iv_playcircle:
			//播放模式设置
			break;
		case R.id.pausebtn:
			// 播放、暂停
			switch (mEngine.mCurrentState) {
			case MyConstances.OPTION_STOP:
				startMediaService(mEngine.getCurrentMusic().path,
						MyConstances.OPTION_PLAY);
				pausebtn.setBackgroundResource(R.drawable.musi_cplay_bg);
				break;
			case MyConstances.OPTION_PLAY:
			case MyConstances.OPTION_CONTINUE:
				startMediaService(null, MyConstances.OPTION_PAUSE);
				pausebtn.setBackgroundResource(R.drawable.music_play);
				break;
			case MyConstances.OPTION_PAUSE:
				startMediaService(null, MyConstances.OPTION_CONTINUE);
				pausebtn.setBackgroundResource(R.drawable.musi_cplay_bg);
				break;

			default:
				break;
			}
			break;
		case R.id.prevbtn:
			//上一曲播放
			 if (mEngine.mCurrentPos > 0) {
                 mEngine.mCurrentPos--;
                 startMediaService(mEngine.getCurrentMusic().path,
                		 MyConstances.OPTION_PLAY);
             }
			break;
		case R.id.nextbtn:
			//下一曲播放
			if (mEngine.mCurrentPos < mEngine.getMusicList().size() - 1) {
                mEngine.mCurrentPos++;

                startMediaService(mEngine.getCurrentMusic().path,
                		MyConstances.OPTION_PLAY);
                //ivPlay.setImageResource(R.drawable.appwidget_pause);
            }
			break;	
		default:
			break;
		}
	}
	
	//子线程在加载
	public class  DownLoadfile extends AsyncTask<String, Integer, Void>{  
		private CloudFile file;
		private SyncFileBlock syncBlock;
		private Bitmap bitmap = null;  
		public DownLoadfile(CloudFile cloudFile){
			this.file = cloudFile;
		}
		@Override  
		protected void onPreExecute() {  
		super.onPreExecute();
			File picFileDir = new File(Environment.getExternalStorageDirectory().toString()+File.separator +"testMusic");//仅创建路径的File对象
			  if(!picFileDir.exists()){
				   //如果路径不存在就先创建路径
			       picFileDir.mkdir();
			  }
			r_file = new File(picFileDir,file.getFilePath());
			syncFile= new WsSyncFile();
		    syncBlock = new SyncFileBlock();
		    syncBlock.SourceSize = file.getFileSize()*DownloadService.DOWNLOAD_BLOCK_SIZE;
		    syncFile.Blocks = syncBlock;
		    syncFile.ID = file.getRemote_id();
		    syncBlock.OffSet = file.getChangedSize();
		    tv_buffering.setVisibility(View.VISIBLE);
		}  
		//主要完成耗时操作  
		@Override  
		protected Void doInBackground(String... arg0){  
			int  result =0;
	        syncFile.Blocks.UpdateData = null;
	        try {  
				result = ClientWS.getInstance(OpenMusic.this).downloadFile(syncFile);
				//保存到sd中 -- 先获得文件的总长度，
	            int count = syncFile.Blocks.UpdateData.length;
				if (count > 0) {
	                RandomAccessFile stream = new RandomAccessFile(r_file, "rw");
					stream.seek(file.getChangedSize());
	                stream.write(syncFile.Blocks.UpdateData, 0, count);
	                stream.close(); 
	                // Increase index
	                file.setChangedSize(file.getChangedSize() + count);
	                // 下载完成
	                if (syncFile.IsFinally) {
	                    file.setState(DownloadFileState.DOWNLOADED);
	                }
	                // 更新到数据库
	                /* downloadFileDataSource.updateDownloadFile(file);
                	sendChangedBroadcast(file, EVENT_DOWNLOAD_BLOCK_SUCCESS);*/
				} 
	        } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
			return null;
		}  
		//更新进度条  
		@Override  
		protected void onProgressUpdate(Integer... values) {  
			super.onProgressUpdate(values); 
			//tv_buffering.setText("正在缓冲..."+values[0]+"%");
			System.out.println("进度--->"+values[0]);
		}  
		@Override  
		protected void onPostExecute(Void result) {  
			super.onPostExecute(result);
			tv_buffering.setVisibility(View.INVISIBLE);
			//执行播放操作
			startMediaService(r_file.getPath(), MyConstances.OPTION_PLAY, progressSeekBar.getProgress());
		}  
	} 
	 
	 // 启动服务进行音乐控制
    private void startMediaService(String path, int option) {
        startMediaService(path, option, -1);
    }
    
    private void startMediaService(String path, int option, int seek) {
    	System.out.println("startMediaService---->"+path);
        Intent intent = new Intent();
        intent.putExtra("path", path);
        intent.putExtra("option", option);
        intent.putExtra("seek", seek);
        intent.setClass(this, MediaService.class); 
        System.out.println("打开startService");
        startService(intent);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	        switch (keyCode) {
	            case KeyEvent.KEYCODE_BACK:
	                showNotify();
	                // 程序打开HOME界面
	                Intent intent = new Intent();
	                intent.setAction(Intent.ACTION_MAIN);
	                intent.addCategory(Intent.CATEGORY_HOME);
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 新的任务占开启
	                startActivity(intent);
	                return true;
	            case KeyEvent.KEYCODE_MENU:
	                // menu键被点
	            	finish();
	                break;
	            default:
	                break;
	        }
	        return super.onKeyDown(keyCode, event);
	  }

	private void showNotify() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification();
        notification.icon = R.drawable.cheesecloud_icon;
        notification.flags = Notification.FLAG_ONGOING_EVENT;// 让通知常驻

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, new Intent(this,
                OpenMusic.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, "校园云盘音乐播放器", "正在播放音乐", contentIntent);
        manager.notify(1, notification);
    }
}
