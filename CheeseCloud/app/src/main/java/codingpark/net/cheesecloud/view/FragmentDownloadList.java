package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;
import codingpark.net.cheesecloud.handle.DownloadService;
import codingpark.net.cheesecloud.handle.OnTransFragmentInteractionListener;
import codingpark.net.cheesecloud.model.DownloadFileDataSource;
import codingpark.net.cheesecloud.view.dummy.DummyContent;

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
    private DownloadStateAdapter mAdapter             = null;
    private LayoutInflater mInflater                = null;
    private DownloadStateReceiver mReceiver           = null;
    private IntentFilter mFilter                    = null;

    private ArrayList<DownloadFile> mAllFileList                = null;
    private ArrayList<DownloadFile> mWaitDownloadFileList       = null;
    private ArrayList<DownloadFile> mDownloadingFileList     = null;
    private ArrayList<DownloadFile> mDownloadedFileList         = null;
    private ArrayList<DownloadFile> mPauseDownloadFileList      = null;

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

        mContext = getActivity();
        mAdapter = new DownloadStateAdapter(mContext, R.layout.upload_state_item_layout, mAllFileList);
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
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    private void refreshList() {
        Log.d(TAG, "refreshList:" + mAllFileList.size());
        updateData();
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
        mAllFileList.addAll(mPauseDownloadFileList);
        mAllFileList.addAll(mWaitDownloadFileList);
        mAllFileList.addAll(mDownloadedFileList);
    }

    public class DownloadStateAdapter extends ArrayAdapter<DownloadFile> {

        public DownloadStateAdapter(Context context, int resource, List<DownloadFile> objects) {
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
                holder.fileNameView = (TextView)convertView.findViewById(R.id.fileNameView);
                holder.ratioView = (TextView)convertView.findViewById(R.id.ratioView);
                holder.stateView = (TextView)convertView.findViewById(R.id.stateView);
                holder.multiselect_view = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            DownloadFile file = mAllFileList.get(position);
            String fileName = file.getFilePath();
            fileName = fileName.substring(fileName.lastIndexOf("/")+1);
            holder.fileNameView.setText(fileName);
            holder.ratioView.setText(file.getChangedSize() + "/" + file.getFileSize());
            switch (file.getState()) {
                case DownloadFileState.NOT_DOWNLOAD:
                case DownloadFileState.WAIT_DOWNLOAD:
                    holder.stateView.setText(R.string.fragment_download_list_wait_download_state);
                    break;
                case DownloadFileState.DOWNLOADING:
                    holder.stateView.setText(R.string.fragment_download_list_downloading_state);
                    break;
                case DownloadFileState.DOWNLOADED:
                    holder.stateView.setText(R.string.fragment_download_list_downloaded_state);
                    break;
                case DownloadFileState.PAUSE_DOWNLOAD:
                    holder.stateView.setText(R.string.fragment_download_list_pause_download_state);
                    break;
                default:
                    holder.stateView.setText(R.string.fragment_download_list_downloaded_state);
            }
            return convertView;
        }

        private class ViewHolder {
            public ImageView rowImage       = null;
            public TextView fileNameView    = null;
            public TextView ratioView       = null;
            public TextView stateView       = null;
            public CheckBox multiselect_view    = null;
        }
    }

    private class DownloadStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadService.ACTION_DOWNLOAD_STATE_CHANGE)) {
                int event = intent.getIntExtra(DownloadService.EXTRA_DOWNLOAD_STATE, DownloadService.EVENT_DOWNLOAD_BLOCK_SUCCESS);
                DownloadFile file = (DownloadFile)intent.getExtras().get(DownloadService.EXTRA_DOWNLOAD_FILE);
                if (event == DownloadService.EVENT_DOWNLOAD_BLOCK_SUCCESS) {
                    Log.d(TAG, "download block success");
                    for (int i = 0; i < mAllFileList.size(); i++) {
                        if (mAllFileList.get(i).getId() == file.getId()) {
                            mAllFileList.set(i, file);
                            //mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else if(event == DownloadService.EVENT_DOWNLOAD_BLOCK_FAILED){
                    Log.d(TAG, "download block failed");
                } else if (event == DownloadService.EVENT_RESUME_ALL_DOWNLOAD_SUCCESS) {
                    Log.d(TAG, "resume all download success");
                } else if (event == DownloadService.EVENT_RESUME_ALL_DOWNLOAD_FAILED) {
                    Log.d(TAG, "resume all download failed");
                } else if (event == DownloadService.EVENT_PAUSE_ALL_DOWNLOAD_SUCCESS) {
                    Log.d(TAG, "pause all download success");
                } else if (event == DownloadService.EVENT_PAUSE_ALL_DOWNLOAD_FAILED) {
                    Log.d(TAG, "pause all download failed");
                }

            }
            // TODO Current update bottom bar state when receive broadcast from UploadService every time, need optimize
            refreshList();
        }
    }
}
