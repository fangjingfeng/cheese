package codingpark.net.cheesecloud.view.adapte;

import codingpark.net.cheesecloud.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemAdapte extends ArrayAdapter{
	
	private  Context mContext;
	public ListItemAdapte(Context context, int resource) {
		super(context, resource);
		mContext=context;
	}
	
	class ViewHoder{
		ImageView ImageView;
		TextView tv_title;
		TextView tv_time;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoder viewHoder;
		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.list_iten_layout, parent);
			viewHoder = new ViewHoder();
			
			viewHoder.ImageView=(ImageView) convertView.findViewById(R.id.itemnumber);
			viewHoder.tv_title=(TextView) convertView.findViewById(R.id.notice_title);
			viewHoder.tv_time=(TextView) convertView.findViewById(R.id.notice_tome);
		
		}
		
		return convertView;
	}
	
}
