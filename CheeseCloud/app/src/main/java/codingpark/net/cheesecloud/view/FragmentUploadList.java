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
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.UploadService;
import codingpark.net.cheesecloud.model.UploadFileDataSource;

/**
 * A fragment representing a list of uploading/uploaded/upload record
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentUploadList extends ListFragment {
    private static final String TAG = FragmentUploadList.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SECTION_NUMBER = "section_number";

    // This fragment index in TransferStateActivity
    private int section_number;

    private Context mContext                        = null;
    private UploadFileDataSource mUploadDataSource  = null;
    private OnFragmentInteractionListener mListener = null;
    private List<UploadFile> mFileList              = null;
    private UploadStateAdapter mAdapter             = null;
    private LayoutInflater mInflater                = null;
    private UploadStateReceiver mReceiver           = null;
    private IntentFilter mFilter                    = null;

    public static FragmentUploadList newInstance(int position) {
        FragmentUploadList fragment = new FragmentUploadList();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentUploadList() {
        mFileList = new ArrayList<UploadFile>();
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

        mContext = getActivity();
        mAdapter = new UploadStateAdapter(mContext, R.layout.upload_state_item_layout, mFileList);
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
        mFileList = mUploadDataSource.getAllUploadFile();
        Log.d(TAG, "refreshList:" + mFileList.size());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
        // TODO refresh list view
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "FragmentUploadList item " + position + " clicked!");
        if (null != mListener) {
        }
    }


    private class UploadStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UploadService.ACTION_UPLOAD_STATE_CHANGE)) {
                int event = intent.getIntExtra(UploadService.EXTRA_UPLOAD_STATE, UploadService.EVENT_UPLOAD_BLOCK_SUCCESS);
                UploadFile file = (UploadFile)intent.getExtras().get(UploadService.EXTRA_UPLOAD_FILE);
                if (event == UploadService.EVENT_UPLOAD_BLOCK_SUCCESS) {
                     for (int i = 0; i < mFileList.size(); i++) {
                         if (mFileList.get(i).getId() == file.getId()) {
                             mFileList.set(i, file);
                             mAdapter.notifyDataSetChanged();
                             break;
                         }
                     }
                } else if(event == UploadService.EVENT_UPLOAD_BLOCK_FAILED){

                }
            }
        }
    }

    public class UploadStateAdapter extends ArrayAdapter<UploadFile> {

        public UploadStateAdapter(Context context, int resource, List<UploadFile> objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            return mFileList.size();
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
            UploadFile file = mFileList.get(position);
            String fileName = file.getFilePath();
            fileName = fileName.substring(fileName.lastIndexOf("/")+1);
            holder.fileNameView.setText(fileName);
            holder.ratioView.setText(file.getChangedSize() + "/" + file.getFileSize());
            switch (file.getState()) {
                case UploadFileState.NOT_UPLOAD:
                case UploadFileState.WAIT_UPLOAD:
                    holder.stateView.setText("等待上传");
                    break;
                case UploadFileState.UPLOADING:
                    holder.stateView.setText("正在上传");
                    break;
                case UploadFileState.UPLOADED:
                    holder.stateView.setText("上传完成");
                    break;
                case UploadFileState.PAUSE_UPLOAD:
                    holder.stateView.setText("暂停上传");
                    break;
                default:
                    holder.stateView.setText("上传完成");
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

}
