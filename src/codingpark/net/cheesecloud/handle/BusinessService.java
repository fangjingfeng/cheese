package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class BusinessService {
	private static BusinessService business;
	private BusinessHandler handler;

	public static BusinessService getInstance(){
		if(business==null){
			business = new BusinessService();
		}
		return business;
	}
			
			
	public void getPublicContacts(Context mContext,boolean isOpenHandle){
		if(isOpenHandle){
			handler	=new BusinessHandler();
		}
		new BusinessContacts(mContext).start();
	}
	//Handler 
	class BusinessHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
		}
	}
	class BusinessContacts extends Thread{
		private Context mContext;
		public BusinessContacts(Context mContext){
			this.mContext= mContext;
		}
		@Override
		public void run() {
			int result = 0 ;
			ClientWS.getInstance(mContext).getpubliccontacts();
			super.run();
		}
	}
}
