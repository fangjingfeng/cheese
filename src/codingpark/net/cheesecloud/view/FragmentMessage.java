package codingpark.net.cheesecloud.view;

import codingpark.net.cheesecloud.R;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMessage extends ListFragment {
	private Context mContext ;
	private View rootView;

	public static FragmentMessage newInstance(Context context) {
		FragmentMessage fragment = new FragmentMessage(context);
	    	return fragment;
	}
	public FragmentMessage(Context context) {
		mContext = context;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragement_notice, container, false);
		
		return rootView;
	}
}
