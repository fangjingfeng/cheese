package codingpark.net.cheesecloud.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnTransFragmentInteractionListener;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.utils.MyUtils;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.Misc;
import codingpark.net.cheesecloud.view.dummy.utils.PlayFileHelper;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.view.dummy.utils.TypeFilter;
import codingpark.net.cheesecloud.view.imegeutils.ImageLoader;
import codingpark.net.cheesecloud.view.imegeutils.ImageThreadLoad;

/**
 * A fragment representing a list of download/downloading/downloaded record.
 */
public class FragmentDownloadList extends ListFragment {
    public static final String TAG          = FragmentDownloadList.class.getSimpleName();

    private static final String SECTION_NUMBER = "section_number";

    // The fragment index in TransferStateActivity
    private int section_number;

    private Context mContext                        = null;
    private DownloadFileDataSource mDownloadDataSource      = null;
    private OnTransFragmentInteractionListener mListener = null;
    private DownloadListAdapter mAdapter             = null;
    private LayoutInflater mInflater                = null;
    private DownloadStateReceiver mReceiver           = null;
    private IntentFilter mFilter                    = null;

    private ArrayList<DownloadFile> mAllFileList                = null;
    private ArrayList<DownloadFile> mWaitDownloadFileList       = null;
    private ArrayList<DownloadFile> mDownloadingFileList     = null;
    private ArrayList<DownloadFile> mDownloadedFileList         = null;
    private ArrayList<DownloadFile> mPauseDownloadFileList      = null;
    private ImageLoader imageLoader;

    private boolean thumbnail_flag                  = true;
    private ThumbnailCreator thumbnail              = null;
    private PlayFileHelper mPlayHelper              = null;
    

    public static FragmentDownloadList newInstance(int number) {
        FragmentDownloadList fragment = new FragmentDownloadList();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * The constructor
     */
    public FragmentDownloadList() {
        mAllFileList = new ArrayList<DownloadFile>();
        mWaitDownloadFileList = new ArrayList<DownloadFile>();
        mDownloadingFileList = new ArrayList<DownloadFile>();
        mPauseDownloadFileList = new ArrayList<DownloadFile>();
        mDownloadedFileList = new ArrayList<DownloadFile>();
        mReceiver = new DownloadStateReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(DownloadService.ACTION_DOWNLOAD_STATE_CHANGE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            section_number = getArguments().getInt(SECTION_NUMBER);
        }
        thumbnail = new ThumbnailCreator(mContext, 64, 64);
        mPlayHelper = new PlayFileHelper(mContext);
        imageLoader=new ImageLoader(mContext);
        mAdapter = new DownloadListAdapter(mContext, R.layout.upload_state_item_layout, mAllFileList);
        mDownloadDataSource = new DownloadFileDataSource(mContext);
        mDownloadDataSource.open();
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(mAdapter);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshList();
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
        if (mDownloadDataSource != null)
            mDownloadDataSource.close();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "serUserVisibleHint:" + isVisibleToUser);
        if (isVisibleToUser) {
            if (mListener != null)
                mListener.refreshDownloadBottomBar(mWaitDownloadFileList, mDownloadingFileList, mPauseDownloadFileList, mDownloadedFileList);
        }
        //super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }

        // TODO Current just handle the downloaded file's click event
        DownloadFile r_file = mAllFileList.get(position);
        if (r_file.getState() == DownloadFileState.DOWNLOADED) {
        	String path=Environment.getExternalStorageDirectory().toString()+File.separator +"testCamera/"+MyUtils.md5(r_file.getFilePath());
             //= Misc.mergePath(Misc.getDownloadRootDir(), r_file.getFilePath());
            Log.d(TAG, "Try to open downloaded file: " + path);
            mPlayHelper.playFile(path);
        }
    }

    private void refreshList() {
        Log.d(TAG, "refreshList:" + mAllFileList.size());
        updateData();
        System.out.println("刷新界面");
        mAdapter.notifyDataSetChanged();
        if (mListener != null)
            mListener.refreshDownloadBottomBar(mWaitDownloadFileList, mDownloadingFileList, mPauseDownloadFileList, mDownloadedFileList);
    }

    private void updateData() {
        mWaitDownloadFileList = mDownloadDataSource.getAllDownloadFileByState(DownloadFileState.WAIT_DOWNLOAD);
        mDownloadingFileList = mDownloadDataSource.getAllDownloadFileByState(DownloadFileState.DOWNLOADING);
        mPauseDownloadFileList = mDownloadDataSource.getAllDownloadFileByState(DownloadFileState.PAUSE_DOWNLOAD);
        mDownloadedFileList = mDownloadDataSource.getAllDownloadFileByState(DownloadFileState.DOWNLOADED);
        mDownloadDataSource.getAllDownloadFile();
        mAllFileList.clear();
        mAllFileList.addAll(mDownloadingFileList);
        mAllFileList.addAll(mWaitDownloadFileList);
        mAllFileList.addAll(mPauseDownloadFileList);
        mAllFileList.addAll(mDownloadedFileList);
    }

    /**
     * The FragmentDownloadList's ListView adapter
     */
    public class DownloadListAdapter extends ArrayAdapter<DownloadFile> {
        public DownloadListAdapter(Context context, int resource, List<DownloadFile> objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            return mAllFileList.size();
        }

        @SuppressLint("NewApi")
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView= mInflater.inflate(R.layout.upload_state_item_layout, null);
                holder.rowImage = (ImageView)convertView.findViewById(R.id.file_thumb);
                holder.fileNameView = (TextView)convertView.findViewById(R.id.update_file_name);
                holder.update_static=(ImageView)convertView.findViewById(R.id.update_static);
                holder.text_title_view=(TextView)convertView.findViewById(R.id.text_title_view);
                holder.ratioView = (TextView)convertView.findViewById(R.id.update_file_size);
                holder.myProgressBar=(ProgressBar)convertView.findViewById(R.id.progressbar_updown);
                holder.stateView = (TextView)convertView.findViewById(R.id.state_view);
                holder.multiselect_view = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            DownloadFile file = mAllFileList.get(position);
            String fileName = file.getFilePath();
            fileName = fileName.substring(fileName.lastIndexOf("/")+1);
            holder.fileNameView.setText(fileName);
            // TODO Resolve server return file size unit is KB
            long fileSize = file.getFileSize() * 1000;
            holder.ratioView.setText(file.getState()==2?FlowConverter.Convert(file.getChangedSize()) + "/" + FlowConverter.Convert(file.getFileSize()*1024):FlowConverter.Convert(file.getChangedSize()));
            if(position==mDownloadingFileList.size()+mWaitDownloadFileList.size()+mPauseDownloadFileList.size()){ 
            	holder.text_title_view.setVisibility(View.VISIBLE);
            }else{
            	holder.text_title_view.setVisibility(View.GONE);
            }
            
        	if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType(file.getFilePath()) || ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType(file.getFilePath())){
            	holder.rowImage.setImageResource(ThumbnailCreator.getDefThumbnailsByName(file.getFilePath()));
        	}else{
        		String imgName=file.getThumb_uri_name()+".jpg";
        		String url= "Http://58.116.52.8:8977/Images/Prev/"+imgName;
        		imageLoader.DisplayImage(url, holder.rowImage);
        	}
            switch (file.getState()) {
                case DownloadFileState.NOT_DOWNLOAD:
                case DownloadFileState.WAIT_DOWNLOAD:
                    holder.stateView.setText(R.string.fragment_download_list_wait_download_state);
                    holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_wind));
	                refreshList();
                    break;
                case DownloadFileState.DOWNLOADING:
                    holder.stateView.setText(R.string.fragment_download_list_downloading_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_down));
	                holder.myProgressBar.setMax((int)file.getFileSize()*1024);
	            	holder.myProgressBar.setVisibility(View.VISIBLE);
	            	holder.myProgressBar.setProgress((int)(file.getChangedSize()));
                    break;
                case DownloadFileState.DOWNLOADED:
                    holder.stateView.setText(R.string.fragment_download_list_downloaded_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_finished));
	                holder.myProgressBar.setVisibility(View.GONE);
                    break;
                case DownloadFileState.PAUSE_DOWNLOAD:
                    holder.stateView.setText(R.string.fragment_download_list_pause_download_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_pause));
	                holder.myProgressBar.setVisibility(View.VISIBLE);
	            	notifyDataSetChanged();
	                break;
                default:
                    holder.stateView.setText(R.string.fragment_upload_list_uploaded_state);
	                holder.update_static.setBackground(getResources().getDrawable(R.drawable.job_status_fail));
	                notifyDataSetChanged();
            }
            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView rowImage       = null;
        public TextView fileNameView    = null;
        public TextView ratioView       = null;
        public TextView stateView       = null;
        public CheckBox multiselect_view    = null;
        public TextView text_title_view =null;
        public ProgressBar myProgressBar    = null;
        public ImageView update_static    =null;
    }

    /**
     * Listen  broadcast, then refresh list view
     */
    private class DownloadStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadService.ACTION_DOWNLOAD_STATE_CHANGE)) {
                int event = intent.getIntExtra(DownloadService.EXTRA_DOWNLOAD_STATE, DownloadService.EVENT_DOWNLOAD_BLOCK_SUCCESS);
                if (event == DownloadService.EVENT_DOWNLOAD_BLOCK_SUCCESS) {
                    Log.d(TAG, "download block success");
                    DownloadFile file = (DownloadFile)intent.getExtras().get(DownloadService.EXTRA_DOWNLOAD_FILE);
                    refreshList();
                    for (int i = 0; i < mAllFileList.size(); i++) {
                    	if (mAllFileList.get(i).getId() == file.getId()) {
                            mAllFileList.set(i, file);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else if(event == DownloadService.EVENT_DOWNLOAD_BLOCK_FAILED){
                    Log.d(TAG, "download block failed");
                    refreshList();
                } else if (event == DownloadService.EVENT_RESUME_ALL_DOWNLOAD_SUCCESS) {
                    Log.d(TAG, "resume all download success");
                    refreshList();
                } else if (event == DownloadService.EVENT_RESUME_ALL_DOWNLOAD_FAILED) {
                    Log.d(TAG, "resume all download failed");
                    refreshList();
                } else if (event == DownloadService.EVENT_PAUSE_ALL_DOWNLOAD_SUCCESS) {
                    Log.d(TAG, "pause all download success");
                    refreshList();
                } else if (event == DownloadService.EVENT_PAUSE_ALL_DOWNLOAD_FAILED) {
                    Log.d(TAG, "pause all download failed");
                    refreshList();
                }

            }
           
        }
    }
}
