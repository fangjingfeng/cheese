package codingpark.net.cheesecloud.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.handle.OnTransFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.UploadService;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.utils.BitmapUtil;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.utils.MyUtils;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.PlayFileHelper;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.view.imegeutils.ImageLoader;

/**
 * A fragment representing a list of uploading/uploaded/upload record
 */
public class FragmentUploadList extends ListFragment implements UplodgetNumber{
    private static final String TAG = FragmentUploadList.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SECTION_NUMBER = "section_number";

    // This fragment index in TransferStateActivity
    private int section_number;

    private Context mContext                        = null;
    private UploadFileDataSource mUploadDataSource  = null;
    private OnTransFragmentInteractionListener mListener = null;
    private UploadListAdapter mAdapter             = null;
    private LayoutInflater mInflater                = null;
    private UploadStateReceiver mReceiver           = null;
    private IntentFilter mFilter                    = null;

    private ArrayList<UploadFile> mAllFileList              = null;
    private ArrayList<UploadFile> mWaitUploadFileList       = null;
    private ArrayList<UploadFile> mUploadingFileList        = null;
    private ArrayList<UploadFile> mUploadedFileList         = null;
    private ArrayList<UploadFile> mPauseUploadFileList      = null;
    private ImageLoader imageLoader;
    private int pregrbar                                    = 0;
    private PlayFileHelper mPlayHelper;
    
    private static final int STOP = 1;//设置标记
    private static final int START = 2;//设置标记

    public static FragmentUploadList newInstance(int position) {
        FragmentUploadList fragment = new FragmentUploadList();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }
    
    public void getPablic(){
    	
    }

    /**
     * The constructor
     */
    public FragmentUploadList() {
        mAllFileList = new ArrayList<UploadFile>();
        mWaitUploadFileList = new ArrayList<UploadFile>();
        mUploadingFileList = new ArrayList<UploadFile>();
        mPauseUploadFileList = new ArrayList<UploadFile>();
        mUploadedFileList = new ArrayList<UploadFile>();

        mReceiver = new UploadStateReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(UploadService.ACTION_UPLOAD_STATE_CHANGE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            section_number = getArguments().getInt(SECTION_NUMBER);
        } 
        mPlayHelper = new PlayFileHelper(mContext);
        mAdapter = new UploadListAdapter(mContext, R.layout.upload_state_item_layout, mAllFileList);
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; 
        height = metric.heightPixels;
        imageLoader=new ImageLoader(mContext);
        mUploadDataSource = new UploadFileDataSource(mContext);
        mUploadDataSource.open();
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshList();
    }

    private void refreshList() {
        updateData();
        System.out.println("刷新的listView");
        mAdapter.notifyDataSetChanged();
       /* if (mListener != null)
            mListener.refreshUploadBottomBar(mWaitUploadFileList, mUploadingFileList, mPauseUploadFileList, mUploadedFileList);*/
        
    }

    private void updateData() {
        mWaitUploadFileList = mUploadDataSource.getAllUploadFileByState(UploadFileState.WAIT_UPLOAD);
        mUploadingFileList = mUploadDataSource.getAllUploadFileByState(UploadFileState.UPLOADING);
        mPauseUploadFileList = mUploadDataSource.getAllUploadFileByState(UploadFileState.PAUSE_UPLOAD);
        mUploadedFileList = mUploadDataSource.getAllUploadFileByState(UploadFileState.UPLOADED);
        mAllFileList.clear();
        
        mAllFileList.addAll(mUploadingFileList);
        mAllFileList.addAll(mPauseUploadFileList);
        mAllFileList.addAll(mWaitUploadFileList);
        mAllFileList.addAll(mUploadedFileList);
        
        System.out.println("总的上传个数 = : "+mAllFileList.size());
        System.out.println("等待个数 = : "+mWaitUploadFileList.size());
        System.out.println("正在上传的个数 = : "+mUploadingFileList.size());
        System.out.println("暂停个数 = : "+mPauseUploadFileList.size());
        System.out.println("上传完成个数 = : "+mUploadedFileList.size());
        
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTransFragmentInteractionListener) activity;
            mContext = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: registerReceiver");
        super.onResume();
        mContext.registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: unregisterReceiver");
        super.onStop();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mUploadDataSource != null)
            mUploadDataSource.close();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "serUserVisibleHint:" + isVisibleToUser);
        if (isVisibleToUser) {
            if (mListener != null)
                mListener.refreshUploadBottomBar(mWaitUploadFileList, mUploadingFileList, mPauseUploadFileList, mUploadedFileList);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "FragmentUploadList item " + position + " clicked!");
        
        UploadFile r_file = mAllFileList.get(position);
        if (r_file.getState() == DownloadFileState.DOWNLOADED) {
        	String path = r_file.getFilePath();
            Log.d(TAG, "Try to open downloaded file: " + path);
            mPlayHelper.playFile(path);
        }
    }
    private UploadFile fileChangedSize;

	private int width;

	private int height;
    /**
     * Listen the UploadService broadcast, then refresh list view
     * 听UploadService广播,然后刷新列表视图
     */
    private class UploadStateReceiver extends BroadcastReceiver {

		@Override
        public void onReceive(Context context, Intent intent) {
        	System.out.println("监听到了广播了。");
            String action = intent.getAction();
            if (action.equals(UploadService.ACTION_UPLOAD_STATE_CHANGE)) {
                int event = intent.getIntExtra(UploadService.EXTRA_UPLOAD_STATE, UploadService.EVENT_UPLOAD_BLOCK_SUCCESS);
                fileChangedSize = (UploadFile)intent.getExtras().get(UploadService.EXTRA_UPLOAD_FILE);
                switch (event) {
				case UploadService.EVENT_UPLOAD_BLOCK_SUCCESS:
					 for (int i = 0; i < mAllFileList.size(); i++) {
	             	        if (mAllFileList.get(i).getId() == fileChangedSize.getId()) {
	             	            mAllFileList.set(i, fileChangedSize);
	             	            mAdapter.notifyDataSetChanged();
	             	            break;
	             	        }
	             		}
					 Log.d(TAG, "upload block success");
					break;
				case UploadService.EVENT_UPLOAD_BLOCK_FAILED:
					refreshList();
					Log.d(TAG, "upload block failed");
					break;
				case UploadService.EVENT_RESUME_ALL_UPLOAD_SUCCESS:
					refreshList();
					Log.d(TAG, "resume all upload success");
					break;
				case UploadService.EVENT_RESUME_ALL_UPLOAD_FAILED:
					refreshList();
					Log.d(TAG, "resume all upload failed");
				    break;
				case UploadService.EVENT_PAUSE_ALL_UPLOAD_SUCCESS:
					refreshList();
					Log.d(TAG, "pause all upload success");
					break;
				case UploadService.EVENT_PAUSE_ALL_UPLOAD_FAILED:
					refreshList();
					Log.d(TAG, "pause all upload failed");
					break;
				default:
					break;
				}
            }
        }
    }

    /**
     * FragmentUploadList's ListView adapter
     */
    public class UploadListAdapter extends ArrayAdapter<UploadFile> {

        public UploadListAdapter(Context context, int resource, List<UploadFile> objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            return mAllFileList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView= mInflater.inflate(R.layout.upload_state_item_layout, null);
                holder.rowImage = (ImageView)convertView.findViewById(R.id.file_thumb);
                holder.update_static=(ImageView)convertView.findViewById(R.id.update_static);
                holder.fileNameView = (TextView)convertView.findViewById(R.id.update_file_name);
                holder.ratioView = (TextView)convertView.findViewById(R.id.update_file_size);
                holder.stateView = (TextView)convertView.findViewById(R.id.state_view);
                holder.myProgressBar=(ProgressBar)convertView.findViewById(R.id.progressbar_updown);
                holder.text_title_view =(TextView)convertView.findViewById(R.id.text_title_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            UploadFile file = mAllFileList.get(position);
            String fileName = file.getFilePath();
            fileName = fileName.substring(fileName.lastIndexOf("/")+1);
            if(position==mUploadingFileList.size()+mPauseUploadFileList.size()+mWaitUploadFileList.size()-2){ 
            	holder.text_title_view.setVisibility(View.VISIBLE);
            }else{
            	holder.text_title_view.setVisibility(View.GONE);
            }
            if(((int)(file.getChangedSize()))<((int)file.getFileSize())){
            	holder.myProgressBar.setMax((int)file.getFileSize());
            	holder.myProgressBar.setVisibility(View.VISIBLE);
            	holder.myProgressBar.setProgress((int)(file.getChangedSize()));
            }else{
            	holder.myProgressBar.setVisibility(View.VISIBLE);
            	notifyDataSetChanged();
            }
            holder.fileNameView.setText(fileName);
            holder.ratioView.setText(file.getState()==2?FlowConverter.Convert(file.getChangedSize()) + "/" + FlowConverter.Convert(file.getFileSize()):FlowConverter.Convert(file.getChangedSize()));
            //加载缩略图
            if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType(file.getFilePath()) || ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType(file.getFilePath())){
            	holder.rowImage.setImageResource(ThumbnailCreator.getDefThumbnailsByName(file.getFilePath()));
        	}else{
        		BitmapUtil.downloadBitmap(file.getFilePath(),holder.rowImage,(int)mContext.getResources().getDimension(R.dimen.file_list_icon_height),(int)mContext.getResources().getDimension(R.dimen.file_list_icon_height));
            }
            System.out.println("文件路径："+file.getFilePath());
	        switch (file.getState()) {
	            case UploadFileState.NOT_UPLOAD:
	            case UploadFileState.WAIT_UPLOAD:
	                holder.stateView.setText(R.string.fragment_upload_list_wait_upload_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_wind));
	                refreshList();
	                break;
	            case UploadFileState.UPLOADING:
	            	System.out.println("上传进行中！");
	                holder.stateView.setText(R.string.fragment_upload_list_uploading_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_up));
	                break;
	            case UploadFileState.UPLOADED:
	            	System.out.println("上传完成！");
	                holder.stateView.setText(R.string.fragment_upload_list_uploaded_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_finished));
	                holder.myProgressBar.setVisibility(View.GONE);
	                //holder.stateView.setText("上传时间:"+DateUtils.getDateTime());
	                break;
	            case UploadFileState.PAUSE_UPLOAD:
	            	System.out.println("上传暂停中！");
	                holder.stateView.setText(R.string.fragment_upload_list_pause_upload_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_pause));
	                break;
	            default:
	            	System.out.println("上传出现异常");
	                holder.stateView.setText(R.string.fragment_upload_list_uploaded_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_fail));
	        }
            return convertView;
        }
    }
    private class ViewHolder {
    	public ImageView update_static= null;
        public ImageView rowImage       = null;
        public TextView fileNameView    = null;
        public TextView ratioView       = null;
        public TextView stateView       = null;
        public CheckBox multiselect_view    = null;
        public ProgressBar myProgressBar    = null;
        public TextView text_title_view  = null;
    }

	@Override
	public int getNumber(int pregrbar) {
		this.pregrbar=pregrbar;
		mAdapter.notifyDataSetChanged();
		return 0;
	}

}
