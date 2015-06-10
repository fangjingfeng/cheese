package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.model.HomeListAdapter;
import codingpark.net.cheesecloud.wsi.WsMessage;
import codingpark.net.cheesecloud.wsi.WsMessageInfo;
/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class FragmentHome extends ListFragment implements PullFileListTask.OnPullDataReadyListener,View.OnClickListener{
    private static final String TAG         = "FragmentHome";

    private static Context mContext                 = null;

    
    public static final String TAB_HOME_ITEM_NEWS               = "news";
    public static final String TAB_HOME_ITEM_CLOUD_DISK         = "cloud_disk";
    public static final String TAB_HOME_ITEM_RESOURCE_LIBRARY   = "resource_library";
    public static final String TAB_HOME_ITEM_SMALL_CLASS        = "small_class";
    public static final String TAB_HOME_ITEM_TEMP_SCREEN        = "temp_screen";

    private static final String[] values = new String[] {
            TAB_HOME_ITEM_NEWS,
            TAB_HOME_ITEM_CLOUD_DISK,
            TAB_HOME_ITEM_RESOURCE_LIBRARY,
            //TAB_HOME_ITEM_SMALL_CLASS,
            //TAB_HOME_ITEM_TEMP_SCREEN
    };

    private ArrayList<CloudFile> mDiskList          = null;
    private ArrayAdapter<CloudFile> mAdapter        = null;

    private OnFragmentInteractionListener mListener;

    private LinearLayout mListContainer                 = null;
    private ProgressBar mLoadingView                    = null;

    public Context contexts                          =null;

	private static SharedPreferences sharedPreferenceLetters;
	private static SharedPreferences sharedPreferenceNotic;

	private TextView letters_messager_number;
	private static Editor letters_editor;
	private static Editor notic_editor;

    public static FragmentHome newInstance(Context context,String param2) {
    	FragmentHome fragment = new FragmentHome(context);
        return fragment;
    }

    /**
     * 
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentHome(Context context) {
        mDiskList = new ArrayList<CloudFile>();
        contexts=context;
    }
    
    public FragmentHome(){};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	setUserVisibleHint(true);
    	super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceLetters = mContext.getSharedPreferences(MyConstances.Letters_name_key, 0);
    	letters_editor = sharedPreferenceLetters.edit(); 
    	sharedPreferenceNotic = mContext.getSharedPreferences(MyConstances.Notic_name_key, 0);
    	notic_editor = sharedPreferenceNotic.edit(); 
        mAdapter = new HomeListAdapter(mContext, R.layout.home_item_layout, mDiskList);
        setListAdapter(mAdapter);
    }
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mListContainer = (LinearLayout)rootView.findViewById(R.id.listcontainer);
        mLoadingView = (ProgressBar)rootView.findViewById(R.id.loading);
        
        RelativeLayout notice_button =(RelativeLayout)rootView.findViewById(R.id.button_notice);
        notice_button.setOnClickListener(this);
        letters_messager_number = (TextView) rootView.findViewById(R.id.letters_messager_number);
        
        notice_title = (TextView)rootView.findViewById(R.id.notice_title);
        letters_title = (TextView)rootView.findViewById(R.id.letters_title);
        
        notice_messager_number = (TextView)rootView.findViewById(R.id.notice_messager_number);
        RelativeLayout letters_button =(RelativeLayout)rootView.findViewById(R.id.letters_button);
        letters_button.setOnClickListener(this);
        if (mDiskList.size() == 0)
            refreshList();
        refreshMessedList();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mContext = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //String tag = v.getTag().toString();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        //intent.putExtra(CloudFilesActivity.LIST_MODE_KEY, CloudFilesActivity.MY_CLOUD_LIST_MODE);
        bundle.putSerializable(CloudFilesActivity_1s.SELECT_DISK_KEY, mDiskList.get(position));
        intent.setClass(mContext, CloudFilesActivity_1s.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);

        if (null != mListener) {
            String action_item = v.getTag().toString();
            Log.d(TAG, "action_item:" + action_item);
            mListener.onFragmentInteraction(action_item);
            // TODO Current just resolve tab_home_item_resource_library
        }
    }

    private void setLoadingViewVisible(boolean visible){
        if(null != mLoadingView && null != mListContainer){
            mListContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
            mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void refreshList() {
        setLoadingViewVisible(true);
        new PullFileListTask(mContext, mAdapter, null, null, mDiskList, this).execute();
    }

    @Override
    public void onPullDataReady(int result) {
        setLoadingViewVisible(false);
        //刷新
        mAdapter.notifyDataSetChanged();
    }
    
    private void refreshMessedList() {
    	new AddNoticeListTask().execute();
    }
    
	private ArrayList<WsMessageInfo> lettersInfo;
	private ArrayList<WsMessageInfo> nectionInfo;
	private TextView notice_messager_number;
	private TextView notice_title;
	private TextView letters_title;
    class AddNoticeListTask extends AsyncTask<String, String ,ArrayList<WsMessageInfo> >{
		private int requcetCode_Letters=WsResultType.Faild;
		private int requcetCode_Nection=WsResultType.Faild;
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		//请求网络加载数据
		@Override
		protected ArrayList doInBackground(String... params) {
			lettersInfo = new ArrayList<WsMessageInfo>();
			nectionInfo = new ArrayList<WsMessageInfo>();
			//获取信件集合
			requcetCode_Letters=ClientWS.getInstance(mContext).getMyReceiveWebMessge(lettersInfo);
			//获取消息集合的
			requcetCode_Nection=ClientWS.getInstance(mContext).getMyreceiveannunciate(nectionInfo); 
			return null;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
		}
		@Override
		protected void onPostExecute(ArrayList<WsMessageInfo> arrayMessageInfo) {
			setLoadingViewVisible(false);
			//信件
			if(lettersInfo!=null&& requcetCode_Nection!=WsResultType.Faild){
				if(lettersInfo.size()>sharedPreferenceLetters.getInt(MyConstances.Letters_name_key, 0)){
					int number=lettersInfo.size()-sharedPreferenceLetters.getInt(MyConstances.Letters_name_key, 0);
					letters_messager_number.setText(number+"");
				}else{
					letters_messager_number.setVisibility(View.INVISIBLE);
				}
				letters_editor.putInt(MyConstances.Letters_name_key, lettersInfo.size());
				letters_editor.commit();
				if(lettersInfo!=null&&lettersInfo.size()>0){
					WsMessageInfo wsMessageInfo =lettersInfo.get(0);
					letters_title.setText(wsMessageInfo.getContext());
				}
			}else{
				Toast.makeText(mContext, "通告加载失败！", 0).show();
			}
			//通告
			if(nectionInfo!=null&&requcetCode_Letters!=WsResultType.Faild){
				if(nectionInfo.size()>sharedPreferenceNotic.getInt(MyConstances.Notic_name_key, 0)){
					int number=nectionInfo.size()-sharedPreferenceNotic.getInt(MyConstances.Notic_name_key, 0);
					notice_messager_number.setText(number+"");
				}else{
					notice_messager_number.setVisibility(View.INVISIBLE);
				}
				notic_editor.putInt(MyConstances.Notic_name_key, nectionInfo.size());
				notic_editor.commit();
				if(nectionInfo.size()>0){
					WsMessageInfo wsMessageInfo =nectionInfo.get(0);
					notice_title.setText(wsMessageInfo.getContext());
				}
			}else{
				Toast.makeText(mContext, "信件加载失败！", 0).show();
			}
			super.onPostExecute(arrayMessageInfo);
		}
		
	}
    
	@Override
	public void onClick(View v) {
	 Intent intent =null; 
		switch (v.getId()) {
		case R.id.button_notice:
			Bundle bindle_nectionInfo = new Bundle();
			bindle_nectionInfo.putSerializable("NECTIONINFO", nectionInfo);
			if(intent !=null){
				intent =null;
			}
			intent =new Intent(mContext,NoticeActivity.class);
			intent.putExtras(bindle_nectionInfo);
			break;
		case R.id.letters_button:
			Bundle bindle_Letters= new Bundle();
			bindle_Letters.putSerializable("LETTERSINFO", lettersInfo);
			if(intent !=null){
				intent =null;
			}
			intent =new Intent(mContext,LettersActivity.class);
			intent.putExtras(bindle_Letters);
			break;
		}
		if(intent!=null){
			mContext.startActivity(intent);
		}
	}
	
	
}

