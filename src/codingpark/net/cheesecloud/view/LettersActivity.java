package codingpark.net.cheesecloud.view;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.view.FragmentHomeItme.ViewHolder;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsMessageInfo;

public class LettersActivity extends ListActivity {
	private LinearLayout listcontainer;
	private ProgressBar loading;
	private LayoutInflater mInflater;
	public static String sendermessage="REPLYMAIL_RECIPIENT";
	private int bian=0;
	private ArrayList<WsMessageInfo> lettersInfo;
	private String mEndPoint ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice);
        lettersInfo= (ArrayList<WsMessageInfo>) getIntent().getExtras().get("LETTERSINFO");
        SharedPreferences prefs = getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);
        mEndPoint = prefs.getString(AppConfigs.SERVER_ADDRESS, ClientWS.DEFAULT_ENDPOINT);
        initView();
        initHandler();
	}
	public void initView(){
		TextView textView1 =(TextView) findViewById(R.id.textView1);
		textView1.setText("收信箱");
		TextView ib_playback =(TextView) findViewById(R.id.ib_playback);
		ib_playback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isfinsh();
			}
	    });
		loading = (ProgressBar) findViewById(R.id.loading);
		listcontainer = (LinearLayout) findViewById(R.id.listcontainer);
		mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		titleLayout = (RelativeLayout)findViewById(R.id.titleLayout);
	}
	public void initHandler(){
		if(lettersInfo!=null&&lettersInfo.size()>0){
			setListAdapter(new LettersAdapter(LettersActivity.this,0,lettersInfo));
		}
	}
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		bian=1;
		View view =this.getLayoutInflater().inflate(R.layout.notice_messageinfo_lyout,null);
		TextView tv_title=(TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(lettersInfo.get(position).getAddresser());
		
		TextView tv_message_context =(TextView) view.findViewById(R.id.tv_message_context);
		tv_message_context.setText(lettersInfo.get(position).getContext());
		Button button_reply= (Button) view.findViewById(R.id.button_reply);
		button_reply.setVisibility(View.VISIBLE);
		button_reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("回复邮件");
				Intent intent =new Intent(LettersActivity.this,ReplyMail.class);
				intent.putExtra(MyConstances.mIsHeiteSenderTitle, false);
				intent.putExtra(LettersActivity.sendermessage, (Serializable)lettersInfo.get(position));
				System.out.println("wsMessageInfo --->"+wsMessageInfo.getRecipients().ID);
				intent.putExtra("SDFSDFS", wsMessageInfo.getRecipients().ID);
				LettersActivity.this.startActivity(intent);
			}
		});
		
		listView = (ListView)view.findViewById(R.id.notice_list);
		myMessage = new MyMessage(view);
		if(lettersInfo.get(position).getCount()>0){
			//加载网络数据
			methid(lettersInfo.get(position).getID());
			
		}else{
			newpopuWindow(view);
		}
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
	        		convertView=View.inflate(LettersActivity.this, R.layout.notive_list_item, null);
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
	    	 Intent openIntent =new Intent(LettersActivity.this,ImagePagerActivity.class);
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
	 		 Intent intentFile=new Intent(LettersActivity.this,OpenFiles.class);
	 		 Bundle bundleFile = new Bundle();
	 		 bundleFile.putSerializable("getCloudFile", cloudFile);
	 		 intentFile.putExtras(bundleFile);
    		 startActivity(intentFile);
	    	break;
	    case ContentntIsFile.TAB_File_IS_VIEW:
	    	//视频
	    	System.out.println("视频类型");
			//点击播放
			Intent intentView=new Intent(LettersActivity.this,VideoPlayer.class );
			intentView.putExtra("ShowThings", wsMessageInfo.getFiles().get(position).ID);
			LettersActivity.this.startActivity(intentView);
	    	break;
		}
	}
	
	public void newpopuWindow(View view){
		Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
		popuWindow = new PopupWindow(view,
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popuWindow.setHeight( getWindowManager().getDefaultDisplay().getHeight()-statusBarHeight-(int)getResources().getDimension(R.dimen.title_bar_height));
        popuWindow.setWidth (getWindowManager().getDefaultDisplay().getWidth());
		popuWindow.setOutsideTouchable(true);
		popuWindow.setBackgroundDrawable(new ColorDrawable(R.color.white));
		popuWindow.showAtLocation(listcontainer, Gravity.BOTTOM+Gravity.LEFT, 0, 0);
	}
	class MyMessage extends Handler{
		public View view;
		public MyMessage(View view){
			this.view=view;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//填充listView
		    listView.setAdapter(new LettersAdapt());
			newpopuWindow(view);
		}
	};
	private WsMessageInfo wsMessageInfo;
	private List<Bitmap> listBitmap =null;
	public void methid(final String fileNameId){
		 new Thread(){
			@Override
			public void run() {
				listBitmap=new ArrayList<Bitmap>();
				Message message= new Message();
				try {
					 wsMessageInfo=ClientWS.getInstance(LettersActivity.this).getWepMsgByID(fileNameId);
					 for(int i=0; i<wsMessageInfo.getFiles().size();i++){
						 Bitmap bitmap;
						 if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType("."+wsMessageInfo.getFiles().get(i).Extend)||ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType("."+wsMessageInfo.getFiles().get(i).Extend)) {
							 //音乐 ，文档
							bitmap = BitmapFactory.decodeResource(getResources(), ThumbnailCreator.getDefThumbnailsByName(wsMessageInfo.getFiles().get(i).FullName));
						 }else{
								//视频，图片
								URL imagURL = new URL("Http://58.116.52.8:8977/Images/Prev/"+ wsMessageInfo.getFiles().get(i).phyInfo.getPhyName()+ ".jpg");
								HttpURLConnection conn = (HttpURLConnection) imagURL.openConnection();
								if(200==conn.getResponseCode()){
									InputStream inputStream = conn.getInputStream();
									bitmap = BitmapFactory.decodeStream(inputStream);
								}else{
									bitmap = BitmapFactory.decodeResource(getResources(), ThumbnailCreator.getDefThumbnailsByName(wsMessageInfo.getFiles().get(i).FullName));
								}
						 }
						 if(bitmap!=null){
							listBitmap.add(bitmap);
						 }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				 myMessage.sendMessage(message);
				super.run();
			}
		 }.start();
	}

	public void setLoadingViewVisible(boolean istrue){
		loading.setVisibility(istrue?View.VISIBLE:View.GONE);
		listcontainer.setVisibility(istrue?View.GONE:View.VISIBLE);
	}
	
	private PopupWindow popuWindow;
	private RelativeLayout titleLayout;
	private SharedPreferences.Editor editor;
	private MyMessage myMessage;
	private ImageView preview_imag;
	private int statusBarHeight;
	private Button bt_attachment;
	private ListView listView;
	//加载数据
	public void showListView(ArrayList<WsMessageInfo> messageInfo){
		this.setListAdapter(new LettersAdapter(LettersActivity.this,R.layout.letters_iten,messageInfo));
	}
	
	class LettersAdapter extends ArrayAdapter{
		private LayoutInflater mInflater   = null;
		private  ArrayList<WsMessageInfo>  listMessageinfo;
		public LettersAdapter(Context context, int resource,ArrayList<WsMessageInfo> messageInfo) {
			super(context, resource);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.listMessageinfo =messageInfo;
		}
		@Override
		public int getCount() {
			return listMessageinfo.size();
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.letters_iten, parent, false);
	            holder = new ViewHolder();
	            holder.tv_emlie = (TextView)convertView.findViewById(R.id.tv_emlie);
	            holder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
	            holder.context = (TextView)convertView.findViewById(R.id.context);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder)convertView.getTag();
	        }
	        WsMessageInfo messageInfo = listMessageinfo.get(position);
	        holder.tv_emlie.setText(messageInfo.getAddresser());
	        holder.tv_time.setText(messageInfo.getCreateDate().substring(0, 10));
	        holder.context.setText(messageInfo.getContext());
	        return convertView;
	    }
	}
	    private class ViewHolder {
	        public TextView tv_emlie    = null;
	        public TextView tv_time    = null;
	        public TextView context   = null;
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
    		if(popuWindow!=null){
    			popuWindow.dismiss();
    		}
    		bian=0;
    	}
	}
	@Override
	protected void onDestroy() {
		if (popuWindow!=null&&popuWindow.isShowing()) {  
            popuWindow.dismiss();  
        }  
		super.onDestroy();
	}
}