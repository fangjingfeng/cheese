package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.utils.BitmapUtil;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.view.dummy.utils.DevicePathUtils;
import codingpark.net.cheesecloud.R;

import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.handle.FileManager;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.OnSelectUploadChangedListener;
import codingpark.net.cheesecloud.view.dummy.utils.ContentntIsFile;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.view.dummy.utils.TypeFilter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentSelectUploadFiles extends ListFragment implements OnKeyDownListener {
    public static final String TAG      = FragmentSelectUploadFiles.class.getSimpleName();

    private OnSelectUploadChangedListener mListener     = null;
    private Context mContext                            = null;
    private FileManager mFileMgr                        = null;
    private FileListAdapter mAdapter                    = null;
    // Enable/disable show pictures/videos thumbnail
    private boolean thumbnail_flag                  = true;
    private ThumbnailCreator thumbnail              = null;

    //the list used to feed info into the array adapter
    private ArrayList<String> mFileList             = null;
    // Store user selected all file/folder path
    private ArrayList<String> mSelectedPath         = null;
    // Store user selected files index in the ListView
    private ArrayList<Integer> mSelectedPositions   = null;
    private LinearLayout mPathBar                   = null;

    private PathBarItemClickListener mPathBatItemListener       = null;
    //private SelectedChangedListener mSelectedChangedListener    = null;
    private SharedPreferences mSettings                         = null;

    public static FragmentSelectUploadFiles newInstance(String param1, String param2) {
        FragmentSelectUploadFiles fragment = new FragmentSelectUploadFiles();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentSelectUploadFiles() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mSettings           = mContext.getSharedPreferences(AppConfigs.PREFS_NAME, 0);
        boolean hide        = mSettings.getBoolean(AppConfigs.PREFS_HIDDEN, false);
        boolean thumb       = mSettings.getBoolean(AppConfigs.PREFS_THUMBNAIL, true);
        int sort            = mSettings.getInt(AppConfigs.PREFS_SORT, 1);

        mFileList = new ArrayList<String>();
        mSelectedPath = new ArrayList<String>();
        mSelectedPositions = new ArrayList<Integer>();

        // 1. Initial FileManager utility
        // 2. Set FileManager utility work parameter
        mFileMgr = new FileManager(mContext);
        mFileMgr.setShowHiddenFiles(hide);
        mFileMgr.setSortType(sort);

        mPathBatItemListener = new PathBarItemClickListener();
        // Initial ListView
        mAdapter = new FileListAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectUploadChangedListener) activity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = View.inflate(mContext, R.layout.fragment_select_upload_files, null);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Destroy View");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        // Initial Path bar
        LinearLayout path_bar_container = (LinearLayout)getView().findViewById(R.id.pathBarContainer);
        setUpdatePathBar(path_bar_container);
        refreshPathBar();
        // Set ListView adapter
        setListAdapter(mAdapter);
        updateContent(mFileMgr.switchToDirByIndex(mFileMgr.getPathStack().size() - 1));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "onHiddenChanged: " + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if ((mSelectedPositions != null) || (mSelectedPath != null)) {
                mSelectedPath.clear();
                mSelectedPositions.clear();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        final String item = getFilePath(position);
        File file = new File(item);

        if (isMultiSelected()) {
            mAdapter.addMultiPosition(position);
            return;
        }

        if (file.isDirectory()) {
            if(file.canRead()) {
                updateContent(mFileMgr.switchToNextDir(item));

            } else {
                Toast.makeText(mContext, "Can't read folder due to permissions",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (file.isFile()) {
            Log.d(TAG, "Select file: " + item);
            mAdapter.addMultiPosition(position);
        }
    }

    public boolean isMultiSelected() {
        return !mSelectedPath.isEmpty();
    }
    public String getFilePath(int position){
        final String item = getData(position);
        Log.d(TAG, "item  " + item);
        String curDir = mFileMgr.getCurrentDir();
        if(curDir.equals(mFileMgr.diskName)) {
            return item;
        }
        else {
            return (mFileMgr.getCurrentDir() + "/" + item);
        }
    }

    /**
     * will return the data in the ArrayList that holds the dir contents.
     *
     * @param position	the indext of the arraylist holding the dir content
     * @return the data in the arraylist at position (position)
     */
    private String getData(int position) {

        if(position > mFileList.size() - 1 || position < 0)
            return null;

        return mFileList.get(position);
    }

    /**
     * This method is called from the upload activity and is passed
     * the LinearLayout that should be updated as the directory changes
     * so the user knows which folder they are in.
     *
     * @param pathBar	The label to update as the directory changes
     */
    private void setUpdatePathBar(LinearLayout pathBar) {
        mPathBar = pathBar;
        // Initial path bar default item, Disk, this item is root.
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
        textView.setTag(0);
        String path = mContext.getResources().getString(R.string.upload_activity_bottom_bar_default_item_string);
        textView.setText(path);
        textView.setOnClickListener(mPathBatItemListener);
        mPathBar.addView(textView);
    }

    private void refreshPathBar() {
        Log.d(TAG, "Start refresh path bar");
        Stack<String> pathStack = mFileMgr.getPathStack();
        int pathBarCount = mPathBar.getChildCount();
        int pathStackCount = pathStack.size();
        Log.d(TAG, "pathStackCount: " + pathStackCount);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        if (pathBarCount < pathStackCount) {
            // Add extra path to pathBar
            for (int i = pathBarCount; i < pathStackCount; i++) {
                TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
                textView.setTag(i);
                String path = pathStack.get(i);
                path = path.substring(path.lastIndexOf("/") + 1, path.length());
                Log.d(TAG, "path " + i + " is " + path);
                textView.setText(path);
                textView.setOnClickListener(mPathBatItemListener);
                mPathBar.addView(textView);
            }
        } else if (pathBarCount > pathStackCount) {
            // Remove extra path from pathBar
            for (int i = pathBarCount; i > pathStackCount ; i--) {
                mPathBar.removeViewAt(i - 1);
            }

        }
    }

    /**
     * called to update the file contents as the user navigates there
     * phones file system.
     *
     * @param content	an ArrayList of the file/folders in the current directory.
     */
    public void updateContent(ArrayList<String> content) {
        if(!mFileList.isEmpty())
            mFileList.clear();

        mFileList.addAll(content);
        mAdapter.notifyDataSetChanged();

        if(mPathBar != null)
            refreshPathBar();
    }

    @Override
    public boolean onBackKeyDown() {
        if (mFileMgr.isRoot()) {
            return false;
        } else {
            if (isMultiSelected())
                mAdapter.clearMultiSelect();
            updateContent(mFileMgr.switchToPreviousDir());
            return true;
        }
    }

    /**
     * This class listening path bar item click event.Path bar's item
     * stand for a folder of current path. When user click one item,
     * the current path should switch to the folder and clear the path
     * bar's extra redundant item.
     */
    private class PathBarItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int index = Integer.valueOf(v.getTag().toString());
            updateContent(mFileMgr.switchToDirByIndex(index));
        }
    }
    
    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        TextView topView;
        TextView bottomView;
        ImageView icon;
        CheckBox mSelect;	//multi-select check mark icon
    }

    /**
     * A nested class to handle displaying a custom view in the ListView that
     * is used in the Main activity. If any icons are to be added, they must
     * be implemented in the getView method. This class is instantiated once in Main
     * and has no reason to be instantiated again.
     */
    public class FileListAdapter extends ArrayAdapter<String> {
        private String dir_name         = null;
        //private LinearLayout hidden_layout;
        //private ThumbnailCreator thumbnail      = null;
        private DevicePathUtils mDevices        = null;
        private ItemCheckedListener mCheckedListener = null;

        public FileListAdapter() {
            super(mContext, R.layout.upload_item_layout, mFileList);

            thumbnail = new ThumbnailCreator(mContext, 64, 64);
            dir_name = mFileMgr.getCurrentDir();
            mDevices = new DevicePathUtils(mContext);

            mCheckedListener = new ItemCheckedListener();
            mSelectedPositions = new ArrayList<Integer>();
        }

        public void addMultiPosition(int index) {
            String r_path = getFilePath(index);
            if (mSelectedPositions.contains(index)) {
                mSelectedPositions.remove(Integer.valueOf(index));
                mSelectedPath.remove(r_path);
            } else {
                mSelectedPositions.add(index);
                mSelectedPath.add(r_path);
            }
            notifyDataSetChanged();
            if (mListener != null)
                mListener.onSelectUploadChanged(mSelectedPath);
        }

        /**
         * This will turn off multi-select and hide the multi-select buttons at the
         * bottom of the view.
         */
        public void clearMultiSelect() {

            if(mSelectedPositions != null && !mSelectedPositions.isEmpty())
                mSelectedPositions.clear();

            if(mSelectedPath != null && !mSelectedPath.isEmpty())
                mSelectedPath.clear();

            notifyDataSetChanged();
            if (mListener != null)
                mListener.onSelectUploadChanged(mSelectedPath);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return getView_tree(position,convertView,parent);
        }

        /**
         * Get view on TreeView mode
         * @param position
         *      Current view position in the list view
         * @param convertView The item view object
         * @param parent
         * @return
         */
        private View getView_tree(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int num_items = 0;
            String temp = mFileMgr.getCurrentDir();

            File file = new File(getFilePath(position));
            String[] list = file.list();
            if(list != null)
                num_items = list.length;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.upload_item_layout, parent, false);

                holder = new ViewHolder();
                holder.topView = (TextView)convertView.findViewById(R.id.file_name_view);
                holder.bottomView = (TextView)convertView.findViewById(R.id.sub_files_count_view);
                holder.icon = (ImageView)convertView.findViewById(R.id.file_thumb);
                holder.mSelect = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                // 2. Set CheckBox's OnCheckedChangeListener
                holder.mSelect.setOnCheckedChangeListener(mCheckedListener);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
                // 2. Set CheckBox's OnCheckedChangeListener
                holder.mSelect.setOnCheckedChangeListener(mCheckedListener);
            }

            // 1. Update CheckBox's tag, this tag used in ItemSelectedListener
            holder.mSelect.setTag(position);

    		/* This will check if the thumbnail cache needs to be cleared by checking
    		 * if the user has changed directories. This way the cache wont show
    		 * a wrong thumbnail image for the new image file
    		 */
            if(!dir_name.equals(temp) && thumbnail_flag) {
                thumbnail.clearBitmapCache();
                dir_name = temp;
            }


            if (mSelectedPositions != null && mSelectedPositions.contains(position))
                holder.mSelect.setChecked(true);
            else
                holder.mSelect.setChecked(false);

            if (file.isFile()) {
               if(ContentntIsFile.TAB_File_IS_file==ContentntIsFile.isFileType(file.getAbsolutePath()) || ContentntIsFile.TAB_File_IS_MUSIC==ContentntIsFile.isFileType(file.getAbsolutePath())){
            		holder.icon.setImageResource(ThumbnailCreator.getDefThumbnailsByName(file.getAbsolutePath()));
            	}else{
            		BitmapUtil.downloadBitmap(file.getAbsolutePath(),holder.icon,(int)mContext.getResources().getDimension(R.dimen.file_list_icon_height),(int)mContext.getResources().getDimension(R.dimen.file_list_icon_height));
            	}
            } else {
            	 holder.icon.setImageResource(R.drawable.folder);
            }
            
            if(file.isFile()) {
                if(file.isHidden())
                    holder.bottomView.setText("(hidden) | " + FlowConverter.Convert(file.length()));
                else
                    holder.bottomView.setText( FlowConverter.Convert(file.length()));

            } else {
                String count_unit = mContext.getString(R.string.upload_activity_list_item_count_unit_string);
                if(file.isHidden())
                    holder.bottomView.setText("(hidden) | " + num_items + " " + count_unit);
                else
                    holder.bottomView.setText(num_items + " " + count_unit);
            }

            holder.topView.setText(file.getName());

            return convertView;
        }

        /**
         * This class listening ListView item's select CheckBox checked event.
         * When user checked a item, class add this item's index to {@link #mSelectedPositions},
         * and add path which the item stand for to {@link #mSelectedPath}
         */
        private class ItemCheckedListener implements CompoundButton.OnCheckedChangeListener{
            //private static final String TAG     = "ItemSelectedListener";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Index: " + buttonView.getTag() + "\nChecked: " + isChecked);
                int r_index = Integer.valueOf(buttonView.getTag().toString());
                boolean isChanged = false;
                if (isChecked) {
                    if (!mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.add(r_index);
                        mSelectedPath.add(getFilePath(r_index));
                        isChanged = true;
                    }
                } else {
                    if (mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.remove((Integer)r_index);
                        mSelectedPath.remove(getFilePath(r_index));
                        isChanged = true;
                    }
                }
                Log.d(TAG, "Current selected items: " + mSelectedPositions.toString());

                if (isChanged && mListener != null) {
                    mListener.onSelectUploadChanged(mSelectedPath);
                }
            }
        }

    }

}
