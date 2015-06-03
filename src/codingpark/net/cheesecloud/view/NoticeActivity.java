package codingpark.net.cheesecloud.view;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.view.LettersActivity.LettersAdapt;
import codingpark.net.cheesecloud.view.LettersActivity.MyMessage;
import codingpark.net.cheesecloud.view.LettersActivity.ViewHolderContent;
import codingpark.net.cheesecloud.view.NoticeActivity.MyMessageHandler;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsMessage;
import codingpark.net.cheesecloud.wsi.WsMessageInfo;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NoticeActivity extends ListActivity implements View.OnClickListener{
	
	private ProgressBar loading;
	private LinearLayout listcontainer;
	private PopupWindow popuWindow;
	private LayoutInflater mInflater;
	private int bian=0;
	private ListView listView;
	private MyMessageHandler myMessageHandller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice);
		
		//初始化UI
		initUi();
		//初始化逻辑
		initHandler();
	}
	public void initUi(){
		TextView ib_playback =(TextView) findViewById(R.id.ib_playback);
		ib_playback.setOnClickListener(this);
		TextView textView1 =(TextView) findViewById(R.id.textView1);
		textView1.setText("我的通告");
		loading = (ProgressBar) findViewById(R.id.loading);
		listcontainer = (LinearLayout) findViewById(R.id.listcontainer);
		
		mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void initHandler(){
		 nectionInfo = (ArrayList<WsMessageInfo>) getIntent().getExtras().getSerializable("NECTIONINFO");
		 System.out.println("nectionInfo =="+nectionInfo.size());
		//填充数据
		 methids(nectionInfo);
	}
	public void methids(ArrayList<WsMessageInfo> arrayMessage){
		this.setListAdapter(new NoticeAdapter(NoticeActivity.this,R.layout.notice_listitem,arrayMessage));
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ib_playback:
			isfinsh();
			break;
		}
	}
	
	//listView点击事件
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		bian=1;
		View view =this.getLayoutInflater().inflate(R.layout.notice_messageinfo_lyout,null);
		TextView tv_title=(TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(nectionInfo.get(position).getTitle());
		TextView tv_message_context=(TextView) view.findViewById(R.id.tv_message_context);
		tv_message_context.setText(nectionInfo.get(position).getContext());
		
		listView = (ListView)view.findViewById(R.id.notice_list);
		myMessageHandller = new MyMessageHandler(view);
		if(nectionInfo.get(position).getCount()>0){
			//加载网络数据
			methid(nectionInfo.get(position).getID());
		}else{
			newpopuWindow(view);
		}
	}
	class MyMessageHandler extends Handler{
		private View view;
		public MyMessageHandler(View view){
			this.view = view;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("methid __listBitmap :"+listBitmap.size());
			if(listBitmap.size() > 0){
				//填充listView
			    listView.setAdapter(new LettersAdapt());
			}
			newpopuWindow(view);
		}
	} 
	private WsMessageInfo wsMessageInfo;
	private List<Bitmap> listBitmap =null;
	private ArrayList<WsMessageInfo> nectionInfo;
	public void methid(final String fileNameId){
		 new Thread(){
			@Override
			public void run() {
				listBitmap=new ArrayList<Bitmap>();
				Message message= new Message();
				try {
					 System.out.println("nectionInfo.get(position).getID() --0-->"+fileNameId);
					 wsMessageInfo=ClientWS.getInstance(NoticeActivity.this).getWepMsgByID(fileNameId);
					 for(int i=0; i<wsMessageInfo.getFiles().size();i++){
						 Bitmap bitmap;
						 if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType("."+wsMessageInfo.getFiles().get(i).Extend)||ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType("."+wsMessageInfo.getFiles().get(i).Extend)) {
							 //音乐 ，文档
							bitmap = BitmapFactory.decodeResource(getResources(), ThumbnailCreator.getDefThumbnailsByName(wsMessageInfo.getFiles().get(i).FullName));
							System.out.println(" 音乐 "); 
						 }else{
							//视频，图片2bcc5e9e-4987-42
							URL imagURL = new URL("Http://58.116.52.8:8977/Images/Prev/"+ wsMessageInfo.getFiles().get(i).phyInfo.getPhyName()+ ".jpg");
							HttpURLConnection conn = (HttpURLConnection) imagURL.openConnection();
							InputStream inputStream = conn.getInputStream();
							bitmap = BitmapFactory.decodeStream(inputStream);
							System.out.println("wangluo "); 
						 }
						 if(bitmap!=null){
							listBitmap.add(bitmap);
							System.out.println("listBitmap -->"+listBitmap.size());
						 }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				myMessageHandller.sendMessage(message);
				super.run();
			}
		 }.start();
	}
	
	 public class ViewHolderContent {
		 ImageView fujian_imag_icon ;
		 TextView notive_title;
		 TextView fujian_preview;
	 }
	
	public class LettersAdapt extends BaseAdapter{
		    @Override
	        public int getCount() {
	            // TODO Auto-generated method stub
	            return wsMessageInfo.getFiles().size();
	        }
	        @Override
	        public Object getItem(int arg0) {
	            return arg0;
	        }
	 
	        @Override
	        public long getItemId(int arg1) {
	            return arg1;
	        }
	 
	        @Override
	        public View getView(final int position, View convertView, ViewGroup parent) {
	        	ViewHolderContent holder;
	        	if(convertView==null){
	        		holder=new ViewHolderContent();
	        		convertView=View.inflate(NoticeActivity.this, R.layout.notive_list_item, null);
	        		holder.fujian_imag_icon=(ImageView)convertView.findViewById(R.id.fujian_imag_icon);
	        		holder.notive_title=(TextView)convertView.findViewById(R.id.notive_title);
	        		holder.fujian_preview=(TextView)convertView.findViewById(R.id.fujian_preview);
	        		convertView.setTag(holder);
	        	}else{
	        		 holder=(ViewHolderContent) convertView.getTag();
	        	}
	        	//Bitmap bitmap=ListBitmap.get(position);
	        	WsFile wsFile=wsMessageInfo.getFiles().get(position);
	        	//holder.fujian_imag_icon.setImageBitmap(bitmap);
	        	holder.notive_title.setText(wsFile.FullName);
	        	System.out.println("listBitmap :"+listBitmap.size());
	        	if(listBitmap.size()>0){
	        		holder.fujian_imag_icon.setImageBitmap(listBitmap.get(position));
	        	}
	        	holder.fujian_preview.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PreviewAttachment(position);
					}
				});
	        	return convertView;
	        }
	}
	
	//附件预览
	public void PreviewAttachment(int position ){
		 CloudFile cloudFile =new CloudFile();
		 cloudFile.setFilePath(wsMessageInfo.getFiles().get(position).FullName);
		 cloudFile.setCreateDate(wsMessageInfo.getFiles().get(position).CreatDate);
		 cloudFile.setRemote_id(wsMessageInfo.getFiles().get(position).ID);
		//cloudFile.setMd5(newVal);
		 cloudFile.setFileSize(wsMessageInfo.getFiles().get(position).SizeB);
		  //判断是 文件的类型
	    switch (ContentntIsFile.isFileType("."+wsMessageInfo.getFiles().get(position).Extend)) {
	    case ContentntIsFile.TAB_File_IS_IMAGER1:
			//图片
	    	System.out.println("图片");
	    	 Intent openIntent =new Intent(NoticeActivity.this,ImagePagerActivity.class);
	 		 Bundle bundleImage = new Bundle();
	 		 bundleImage.putSerializable("getCloudFile", cloudFile);
	 		 openIntent.putExtras(bundleImage);
	 		 startActivity(openIntent);
			break;
	    case ContentntIsFile.TAB_File_IS_MUSIC:
	    	//音乐
	    	System.out.println("音乐");
			break;
	    case ContentntIsFile.TAB_File_IS_file:
			//文件类型
	    	System.out.println("文件类型");
	    	//wsMessageInfo.getFiles().get(position).ID;
	    	 System.out.println("文档");
	 		 Intent intentFile=new Intent(NoticeActivity.this,OpenFiles.class);
	 		 Bundle bundleFile = new Bundle();
	 		 bundleFile.putSerializable("getCloudFile", cloudFile);
	 		 intentFile.putExtras(bundleFile);
    		 startActivity(intentFile);
	    	break;
	    case ContentntIsFile.TAB_File_IS_VIEW:
	    	//视频
	    	System.out.println("视频类型");
			//点击播放
			Intent intentView=new Intent(NoticeActivity.this,VideoPlayer.class );
			intentView.putExtra("ShowThings", wsMessageInfo.getFiles().get(position).ID);
			NoticeActivity.this.startActivity(intentView);
	    	break;
		}
	}
	public void newpopuWindow(View view){
		Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
		popuWindow = new PopupWindow(view,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popuWindow.setHeight( getWindowManager().getDefaultDisplay().getHeight()-statusBarHeight-(int)getResources().getDimension(R.dimen.title_bar_height));
        popuWindow.setWidth (getWindowManager().getDefaultDisplay().getWidth());
		popuWindow.setOutsideTouchable(true);
		popuWindow.setBackgroundDrawable(new ColorDrawable(R.color.white));
		popuWindow.showAtLocation(listcontainer, Gravity.BOTTOM+Gravity.LEFT, 0, 0);

	}
	 
	 public void setLoadingViewVisible(boolean istrue){
		 loading.setVisibility(istrue?View.VISIBLE:View.GONE);
		 listcontainer.setVisibility(istrue?View.GONE:View.VISIBLE);
		 
	 }
	
	class NoticeAdapter extends ArrayAdapter{
		private LayoutInflater mInflater   = null;
		private ArrayList<WsMessageInfo> arrayMessage;
		public NoticeAdapter(Context context, int resource, ArrayList<WsMessageInfo> arrayMessage) {
			super(context, resource);
			
			View viwe = getLayoutInflater().inflate(R.layout.notice_messageinfo_lyout, null);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.arrayMessage=arrayMessage;
		}

		@Override
		public int getCount() {
			return arrayMessage.size();
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.notice_listitem, parent, false);
	            holder = new ViewHolder();
	            holder.number = (TextView)convertView.findViewById(R.id.number);
	            holder.title = (TextView)convertView.findViewById(R.id.title);
	            holder.time = (TextView)convertView.findViewById(R.id.time);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder)convertView.getTag();
	        }
	        WsMessageInfo myMessage = arrayMessage.get(position);
	        // Icon
	        
	        switch (position) {
			case 0:
				holder.number.setBackgroundColor(getResources().getColor(R.color.news));
				//holder.number.setText(position+1);
				break;
			case 1:
				holder.number.setBackgroundColor(getResources().getColor(R.color.tow));
				//holder.number.setText(position+1);
				break;
			case 2:
				holder.number.setBackgroundColor(getResources().getColor(R.color.three));
				//holder.number.setText(position+1);
				break;
			default:
				holder.number.setBackgroundColor(getResources().getColor(R.color.four));
				//holder.number.setText(position+1);
				break;
			}
	        holder.number.setText((position+1)+"");
	        holder.title.setText(myMessage.getTitle());
	        holder.time.setText(myMessage.getCreateDate().substring(0, 10));
	        // Arrow needn't change
	        return convertView;
	    }

	    private class ViewHolder {
	        public TextView number    = null;
	        public TextView title    = null;
	        public TextView time   = null;
	    }
	    
	}
	
	//返回监听
		public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	              isfinsh();
	              return true;
	          }
	          return onKeyDown(keyCode,event);
	      }
		public void isfinsh(){
			if(bian==0){
	    		bian=1;
	    		finish();
	    	}else{
	    		popuWindow.dismiss();
	    		bian=0;
	    	}
		}
}
