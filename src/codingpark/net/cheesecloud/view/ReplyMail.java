package codingpark.net.cheesecloud.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.utils.DateUtils;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.view.FragmentHomeItme.ViewHolder;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.view.imegeutils.ImageLoader;
import codingpark.net.cheesecloud.wsi.FileInfo;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsMessage;
import codingpark.net.cheesecloud.wsi.WsMessageInfo;

public class ReplyMail extends Activity implements OnClickListener{
	private EditText mail_content;
	private WsMessageInfo messageInfo ;
	private String recipient;
	private String sender;
	private String dateTime;
	private String recipientID;
	private SenderHandMesage senderHandMesage;
	private ListView enclosureList;
	private ImageLoader imageLoader;
	private EnclosureAdapter enclosureAdapter;
	private boolean mIsHeiteSenderTitle;
	private EditText mail_title; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_replymail);
		
		Intent intent =getIntent();
		if(intent.getSerializableExtra(LettersActivity.sendermessage)!=null){
			messageInfo=(WsMessageInfo) intent.getSerializableExtra(LettersActivity.sendermessage);
			
			mIsHeiteSenderTitle =intent.getBooleanExtra(MyConstances.mIsHeiteSenderTitle, false);
			
			recipientID = intent.getStringExtra("SDFSDFS");
			System.out.println("recipientID : "+recipientID);
		}
		sender = messageInfo.getAddresser();
		recipient = messageInfo.getRecipients().Email;
		initUI();
		initHandle();
	}
	public void initUI(){
		TextView ib_playback =(TextView) findViewById(R.id.ib_playback);
		ib_playback.setOnClickListener(this);
		TextView button_right =(TextView) findViewById(R.id.button_right);
		button_right.setOnClickListener(this);
		
		TextView sender_mail =(TextView)findViewById(R.id.sender_mail);
		sender_mail.setText(sender);
		
		TextView recipient_mail =(TextView)findViewById(R.id.recipient_mail);
		recipient_mail.setText(recipient);
		
		Button add_attachment =(Button) findViewById(R.id.add_attachment);
		mail_title = (EditText) findViewById(R.id.mail_title);
		add_attachment.setOnClickListener(this);
		Button add_user =(Button) findViewById(R.id.add_user);
		add_user.setOnClickListener(this);
		mail_content = (EditText) findViewById(R.id.mail_content);
		enclosureList = (ListView) findViewById(R.id.enclosure);
	}
	
	public void isHitEmailTitle(boolean emailTitle){
		if(emailTitle){
			mail_title.setVisibility(View.VISIBLE);
		}else{
			mail_title.setVisibility(View.GONE);
		}
	}
	
	public void initHandle(){
		isHitEmailTitle(mIsHeiteSenderTitle);
		dateTime = DateUtils.getDateTime();
		imageLoader = new ImageLoader(ReplyMail.this);
		if(messageInfo.Files!=null && messageInfo.Files.size()>0){
			enclosureAdapter = new EnclosureAdapter();
			enclosureList.setAdapter(enclosureAdapter);
			setListViewHeightBasedOnChildren(enclosureList);
		}
	}
	class ViewHolder{
		ImageView icon;
        TextView tvFileName;
        Button delete;
	}
	class EnclosureAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return messageInfo.Files.size();
		}

		@Override
		public Object getItem(int position) {
			return messageInfo.Files.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				holder = new ViewHolder();
				convertView =View.inflate(ReplyMail.this, R.layout.sendmessageinfo, null);
				holder.delete= (Button) convertView.findViewById(R.id.delete_send_file);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.tvFileName = (TextView) convertView.findViewById(R.id.file_name);
				convertView.setTag(holder);
			}else{
				 holder = (ViewHolder)convertView.getTag();
			}
			WsFile wsFile = messageInfo.Files.get(position);
			holder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					messageInfo.Files.remove(position);
					notifyDataSetChanged();
				}
			});
			holder.tvFileName.setText(wsFile.FullName);
        	if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType(wsFile.FullName) || ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType(wsFile.FullName)){
        		holder.icon.setImageResource(ThumbnailCreator.getDefThumbnailsByName(wsFile.FullName));
        	}else{
        		String imgName=wsFile.phyInfo.getPhyName()+".jpg";
        		String url= "Http://58.116.52.8:8977/Images/Prev/"+imgName;
        		imageLoader.DisplayImage(url, holder.icon);
        	}
			return convertView;
		}
		
	}
	
	
	 public void setListViewHeightBasedOnChildren(ListView listView) {   
	        // 获取ListView对应的Adapter   
	        ListAdapter listAdapter = listView.getAdapter();   
	        if (listAdapter == null) {   
	            return;   
	        }   
	   
	        int totalHeight = 0;   
	        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   
	            // listAdapter.getCount()返回数据项的数目   
	            View listItem = listAdapter.getView(i, null, listView);   
	            // 计算子项View 的宽高   
	            listItem.measure(0, 0);    
	            // 统计所有子项的总高度   
	            totalHeight += listItem.getMeasuredHeight();    
	        }   
	   
	        ViewGroup.LayoutParams params = listView.getLayoutParams();   
	        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
	        // listView.getDividerHeight()获取子项间分隔符占用的高度   
	        // params.height最后得到整个ListView完整显示需要的高度   
	        listView.setLayoutParams(params);   
	 }   
	 
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode == RESULT_OK) {
			 ArrayList<WsFile> selectCoudFiles = (ArrayList<WsFile>) data.getSerializableExtra(MyConstances.PutSendFile);
			 if( messageInfo.Files==null){
				 messageInfo.Files=selectCoudFiles;
				 enclosureAdapter = new EnclosureAdapter();
				 enclosureList.setAdapter(enclosureAdapter);
				 setListViewHeightBasedOnChildren(enclosureList);
			 }else{
				 messageInfo.Files.addAll(selectCoudFiles);
				 setListViewHeightBasedOnChildren(enclosureList);
				 enclosureAdapter.notifyDataSetChanged();
			 }
	        }
		super.onActivityResult(requestCode, resultCode, data);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_playback:
			finish();
			break;
		case R.id.button_right:
			//发送邮件
			senderHandMesage = new SenderHandMesage();
			System.out.println("hello :"+senderHandMesage);
			String maillContent=mail_content.getText().toString();
			WsMessage wsMessage=new WsMessage();
			wsMessage.setContext(maillContent);
			//接受者
			wsMessage.setReceiver(messageInfo.getID());
			wsMessage.setReceiverType(0);
			//发送者
			wsMessage.setSender(recipientID);
			wsMessage.setCreateDate(dateTime);
			System.out.println("wsMessage ::"+wsMessage);
			//发送信件
			new MySenderMessage(wsMessage).start();
			break;
		case R.id.add_attachment:
			//上传附件
			Intent r_intent = new Intent(ReplyMail.this,SelectPathActivity.class);
			r_intent.putExtra("ISDISPYAFILE", true);
			ReplyMail.this.startActivityForResult(r_intent, 0, null);
			break;
		case R.id.add_user:
			//获取所有联系人
			System.out.println("name ___>走了");
			new GetUserEmali().start();
			break;
		default:
			break;
		}
	}
	
	
	class GetUserEmali extends Thread {
		@Override
		public void run() {
			System.out.println("name ___>走了 获取");
			ClientWS.getInstance(ReplyMail.this).getpubliccontacts();
			super.run();
		}
	}
	
	
	class SenderHandMesage extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==WsResultType.Success){
				Toast.makeText(ReplyMail.this, "发送成功", 0).show();
			}else if(msg.what==WsResultType.Faild){
				Toast.makeText(ReplyMail.this, "发送失败", 0).show();
			}
			//发送完成 关闭当前Activity
			finish();
		}
	}
	class MySenderMessage extends Thread{
		WsMessage wsMessage;
		public MySenderMessage(WsMessage wsMessage) {
			this.wsMessage=wsMessage;
		}
		@Override
		public void run() {
			Message message =new Message();
			message.what=ClientWS.getInstance(ReplyMail.this).sendMessage(wsMessage);
			senderHandMesage.sendMessage(message);
			super.run();
		}
	} 
}
