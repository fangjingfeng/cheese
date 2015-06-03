package codingpark.net.cheesecloud.view;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.utils.SharePrefUitl;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class VideoPlayer extends Activity  {
	private Button bt;  //用于开始和暂停的按钮
	private SurfaceView pView;   //绘图容器对象，用于把视频显示在屏幕上
	private String url;   //视频播放地址
	private MediaPlayer mediaPlayer;    //播放器控件
	private int postSize;    //保存义播视频大小
	private SeekBar seekbar;   //进度条控件
	private boolean flag = true;   //用于判断视频是否在播放中
	private RelativeLayout rl;   
	private boolean display;   //用于是否显示其他按钮
	private Button backButton;   //返回按钮
	private View view;   //ProgressBar 
	private upDateSeekBar update;   //更新进度条用
	private String videoID;
	private String wepURL;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   //全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
		Intent file =getIntent();
		if(file!=null){
			videoID=file.getStringExtra("ShowThings");
		}
		 wepURL =SharePrefUitl.getStringData(this, "wepurl", "http://58.116.52.8:8977");
		new GetVideoAddress().execute();
	}
	private void init() {
		mediaPlayer = new MediaPlayer();   //创建一个播放器对象
		update = new upDateSeekBar();  //创建更新进度条对象
		setContentView(R.layout.main);   //加载布局文件
		backButton = (Button) findViewById(R.id.back);  //返回按钮
		seekbar = (SeekBar) findViewById(R.id.seekbar);  //进度条
		bt = (Button) findViewById(R.id.play);
		bt.setEnabled(false); //刚进来，设置其不可点击
		pView = (SurfaceView) findViewById(R.id.mSurfaceView);
		pView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   //不缓冲
		pView.getHolder().setKeepScreenOn(true);   //保持屏幕高亮
		pView.getHolder().addCallback(new surFaceView());   //设置监听事件
		rl = (RelativeLayout) findViewById(R.id.rl2);
		view = findViewById(R.id.pb);  
	}

	class PlayMovie extends Thread {   //播放视频的线程
		int post = 0;

		public PlayMovie(int post) {
			this.post = post;

		}

		@Override
		public void run() {
			Message message = Message.obtain();
			try {
				Log.i("hck", "runrun  "+url);
				mediaPlayer.reset();    //回复播放器默认
				mediaPlayer.setDataSource(url);   //设置播放路径
				mediaPlayer.setDisplay(pView.getHolder());  //把视频显示在SurfaceView上
				mediaPlayer.setOnPreparedListener(new Ok(post));  //设置监听事件
				mediaPlayer.prepare();  //准备播放
			} catch (Exception e) {
				message.what = 2;
			}
			super.run();
		}
	}

	class Ok implements OnPreparedListener {
		int postSize;

		public Ok(int postSize) {
			this.postSize = postSize;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.i("hck", "play");
			Log.i("hck", "post " + postSize);
			view.setVisibility(View.GONE);  //准备完成后，隐藏控件
			bt.setVisibility(View.GONE);
			rl.setVisibility(View.GONE);
			bt.setEnabled(true);  
			display = false;
			if (mediaPlayer != null) { 
				mediaPlayer.start();  //开始播放视频
			} else {
				return;
			}
			if (postSize > 0) {  //说明中途停止过（activity调用过pase方法，不是用户点击停止按钮），跳到停止时候位置开始播放
				Log.i("hck", "seekTo ");
				mediaPlayer.seekTo(postSize);   //跳到postSize大小位置处进行播放
			}
			new Thread(update).start();   //启动线程，更新进度条
		}
	}

	private class surFaceView implements Callback {     //上面绑定的监听的事件

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {   //创建完成后调用
			if (postSize > 0 && url!= null) {    //说明，停止过activity调用过pase方法，跳到停止位置播放
				new PlayMovie(postSize).start();
				flag = true;
				int sMax = seekbar.getMax();
				int mMax = mediaPlayer.getDuration();
				seekbar.setProgress(postSize * sMax / mMax);
				postSize = 0;
				view.setVisibility(View.GONE);
			}
			else {
				new PlayMovie(0).start();   //表明是第一次开始播放
			}
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { //activity调用过pase方法，保存当前播放位置
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				postSize = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
				flag = false;
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	private void setListener() {
		mediaPlayer
				.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						System.out.println("走了 --- -- ->setOnBufferingUpdateListener ");
					}
				});

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //视频播放完成
					@Override
					public void onCompletion(MediaPlayer mp) {
						flag = false;
						bt.setBackgroundResource(R.drawable.btn_play);
					}
				});

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				System.out.println("setOnPreparedListener ---> 走了！");
			}
		});
	/**
	 * 如果视频在播放，则调用mediaPlayer.pause();，停止播放视频，反之，mediaPlayer.start()  ，同时换按钮背景
	 */
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					bt.setBackgroundResource(R.drawable.btn_play);
					mediaPlayer.pause();
					postSize = mediaPlayer.getCurrentPosition();
				} else {
					if (flag == false) {
						flag = true;
						new Thread(update).start();
					}
					mediaPlayer.start();
					bt.setBackgroundResource(R.drawable.btn_pause);

				}
			}
		});
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int value = seekbar.getProgress() * mediaPlayer.getDuration()  //计算进度条需要前进的位置数据大小
						/ seekbar.getMax();
				mediaPlayer.seekTo(value);

			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
	/**
	 * 点击屏幕，切换控件的显示，显示则应藏，隐藏，则显示
	 */
		pView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (display) {
					bt.setVisibility(View.GONE);
					rl.setVisibility(View.GONE);
					display = false;
				} else {
					rl.setVisibility(View.VISIBLE);
					bt.setVisibility(View.VISIBLE);
					pView.setVisibility(View.VISIBLE);
					/**
					 * 设置播放为全屏
					 */
					ViewGroup.LayoutParams lp = pView.getLayoutParams();
					lp.height = LayoutParams.FILL_PARENT;
					lp.width = LayoutParams.FILL_PARENT;
					pView.setLayoutParams(lp);
					display = true;
				}

			}
		});
		
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
				}
				mediaPlayer = null;
				VideoPlayer.this.finish();

			}
		});
	}
/**
* 更新进度条
*/
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer == null) {
				flag = false;
			} else if (mediaPlayer.isPlaying()) {
				flag = true;
				int position = mediaPlayer.getCurrentPosition();
				int mMax = mediaPlayer.getDuration();
				int sMax = seekbar.getMax();
				seekbar.setProgress(position * sMax / mMax);
			} else {
				return;
			}
		};
	};

	class upDateSeekBar implements Runnable {
		@Override
		public void run() {
			mHandler.sendMessage(Message.obtain());
			if (flag) {
				mHandler.postDelayed(update, 1000);
			}
		}
	}

	class GetVideoAddress extends AsyncTask<String , String , String >{
	  	private String getresultId;
	  	@Override
	  	protected void onPreExecute() {
	  		
	  		super.onPreExecute();
	  	}
			@Override
			protected String doInBackground(String... params) {
				getresultId =ClientWS.getInstance(VideoPlayer.this).getFileConvertInfoByID(videoID);
				return null;
			}
			@Override
			protected void onPostExecute(String result) {
				if(MyConstances.isReqesteSuccessful.equals(getresultId)){
					Toast.makeText(VideoPlayer.this, "网络加载失败！", 0).show();
				}else{
					url= wepURL+"Images/Prev/"+getresultId+".mp4";
					System.out.println("URL-_->"+url);
					init();
					setListener();
				}
				super.onPostExecute(result);
			}
	  }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		System.gc();
	}
}

