package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnPullDataReadyListener;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.model.HomeListAdapter;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class FragmentHome extends ListFragment implements OnPullDataReadyListener {
    private static final String TAG         = "FragmentHome";
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM2 = "param2";

    private static Context mContext                 = null;
    private String mParam2;

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


    public static FragmentHome newInstance(String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentHome() {
        mDiskList = new ArrayList<CloudFile>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAdapter = new HomeListAdapter(mContext, R.layout.home_item_layout, mDiskList);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mListContainer = (LinearLayout)rootView.findViewById(R.id.listcontainer);
        mLoadingView = (ProgressBar)rootView.findViewById(R.id.loading);
        // Current, just pull data(The all disks information) from server
        if (mDiskList.size() == 0)
            refreshList();
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
        //intent.putExtra(CloudFilesActivity.LIST_MODE_KEY, CloudFilesActivity.MY_CLOUD_LIST_MODE);
        intent.putExtra(CloudFilesActivity.SELECT_DISK_KEY, mDiskList.get(position));
        intent.setClass(mContext, CloudFilesActivity.class);
        mContext.startActivity(intent);
        /*
        if (tag.equals(TAB_HOME_ITEM_CLOUD_DISK)) {
            Intent intent = new Intent();
            intent.putExtra(CloudFilesActivity.LIST_MODE_KEY, CloudFilesActivity.MY_CLOUD_LIST_MODE);
            intent.setClass(mContext, CloudFilesActivity.class);
            mContext.startActivity(intent);
        } else if (tag.equals(TAB_HOME_ITEM_RESOURCE_LIBRARY)) {
            Intent intent = new Intent();
            intent.putExtra(CloudFilesActivity.LIST_MODE_KEY, CloudFilesActivity.RESOURCELIB_LIST_MODE);
            intent.setClass(mContext, CloudFilesActivity.class);
            mContext.startActivity(intent);
        }
        */

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
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
    public void onPullDataReady() {
        setLoadingViewVisible(false);
    }
}

