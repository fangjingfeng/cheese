package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.handle.OnWSTaskFinishListener;
import codingpark.net.cheesecloud.handle.PullFileListTask;
import codingpark.net.cheesecloud.utils.ThumbnailCreator;

/**
 * The class display my cloud and resource library folder and files.
 * CloudFilesActivity have 2 mode:
 * MY_CLOUD_LIST_MODE: Initial the my cloud disk as root folder, and display the
 * subdirectory one by one.
 * RESOURCELIB_LIST_MODE: Display all disks at initial exclude my cloud disk.
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 18:12:41
 */
public class CloudFilesActivity extends ListActivity implements View.OnClickListener, OnWSTaskFinishListener<CloudFile>{
    private static final String TAG         = CloudFilesActivity.class.getSimpleName();

    /**
     * The key of CloudFilesActivity display files mode
     */
    public static final String LIST_MODE_KEY        = "list_mode_key";
    /**
     * List user owned cloud folder files
     */
    public static final int MY_CLOUD_LIST_MODE      = 0;
    /**
     * List cloud shared disks
     */
    public static final int RESOURCELIB_LIST_MODE   = 1;
    /**
     * Current list mode
     */
    private static int mListMode            = MY_CLOUD_LIST_MODE;

    // Folder and File cloud file list, fill up by PullCloudFileTask
    private ArrayList<CloudFile> mFolderList            = null;
    private ArrayList<CloudFile> mFileList              = null;
    // Store files + folders, used by ArrayAdapter, the data is mFolderList + mFileList
    private ArrayList<CloudFile> mFileFolderList        = null;
    // Store user selected files object
    private ArrayList<CloudFile> mSelectFileList        = null;
    // Store user selected files index in the ListView
    private ArrayList<Integer> mSelectedPositions   = null;
    // Remember current folder full path
    private Stack<CloudFile> mPathStack                 = null;
    // Path bar, use to show current directory path
    private LinearLayout path_bar_container             = null;
    // List adapter
    private CloudListAdapter mAdapter                   = null;
    LayoutInflater mInflater                            = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        // Set action bar show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_cloud_files);

        mFolderList = new ArrayList<CloudFile>();
        mFileList = new ArrayList<CloudFile>();
        mFileFolderList = new ArrayList<CloudFile>();
        mSelectFileList = new ArrayList<CloudFile>();
        mPathStack = new Stack<CloudFile>();
        mSelectedPositions = new ArrayList<Integer>();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the initial list mode
        Intent recIntent = getIntent();
        mListMode = recIntent.getIntExtra(LIST_MODE_KEY, MY_CLOUD_LIST_MODE);
        // Initial path bar
        path_bar_container = (LinearLayout)findViewById(R.id.pathBarContainer);
        setPathbar();

        // Initial list adapter
        mAdapter = new CloudListAdapter(this, R.layout.cloud_item_layout);
        setListAdapter(mAdapter);

        refreshPathBar();
        refreshList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_files, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Handle action bar event
        // 1. R.id.home: Action Bar up button clicked
        switch (id) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPathStack.size() == 1) {
                finish();
            } else {
                mPathStack.pop();
                mAdapter.clearMultiSelect();
                refreshPathBar();
                refreshList();
            }
        }
        return true;
    }

    private void setPathbar() {
        // Intial mPathStack with current user id(My Cloud Folder) when user select my cloud disk
        if (mListMode == MY_CLOUD_LIST_MODE) {
            CloudFile file = new CloudFile();
            file.setRemote_id(AppConfigs.current_remote_user_id);
            file.setFilePath(getResources().getString(R.string.tab_home_item_cloud_disk_title));
            mPathStack.push(file);
        }
        // Initial mPathStack with ROOT_ID when user select resource library
        else if(mListMode == RESOURCELIB_LIST_MODE) {
            CloudFile file = new CloudFile();
            file.setRemote_id(CheeseConstants.ROOT_ID);
            file.setFilePath(getResources().getString(R.string.tab_home_item_res_lib_title));
            mPathStack.push(file);
        }
    }

    private void refreshList() {
        Log.d(TAG, "Call execute.");
        new PullFileListTask(this, mAdapter, mPathStack.peek(), mFileList, mFolderList).execute();
    }

    private void refreshPathBar() {
        Log.d(TAG, "Start refresh path bar");
        /*
        if (mListMode == MY_CLOUD_LIST_MODE) {

        } else if(mListMode == RESOURCELIB_LIST_MODE) {

        }
        */
        int pathBarCount = path_bar_container.getChildCount();
        int pathStackCount = mPathStack.size();
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if (pathBarCount < pathStackCount) {
            // Add extra path to pathBar
            for (int i = pathBarCount; i < pathStackCount; i++) {
                TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
                textView.setTag(i);
                String path = mPathStack.get(i).getFilePath();
                Log.d(TAG, "path " + i + " is " + path);
                textView.setText(path);
                textView.setOnClickListener(this);
                path_bar_container.addView(textView);
            }
        } else if (pathBarCount > pathStackCount) {
            // Remove extra path from pathBar
            for (int i = pathBarCount; i > pathStackCount ; i--) {
                path_bar_container.removeViewAt(i - 1);
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 1. Refresh bottom bar select path button text
        CloudFile file = mFileFolderList.get(position);
        if (isMultiSelect()) {
            //mSelectedPositions.add(position);
            //mSelectFileList.add(file);
            mAdapter.addMultiPosition(position);
            mAdapter.notifyDataSetChanged();
        } else {
            if (file.getFileType() == CloudFileType.TYPE_FILE) {
                //mSelectFileList.add(file);
                mAdapter.addMultiPosition(position);
                mAdapter.notifyDataSetChanged();
            } else if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
                mPathStack.push(file);
                refreshPathBar();
                refreshList();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Item clicked!");
        int index = Integer.valueOf(v.getTag().toString());
        while (index < (mPathStack.size() - 1)) {
            mPathStack.pop();
            mAdapter.clearMultiSelect();
        }
        refreshPathBar();
        refreshList();
    }

    @Override
    public void onWSTaskDataFinish(CloudFile data) {

    }

    private boolean isMultiSelect() {
        return mSelectFileList.size() > 0;
    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        ImageView fileThumb;
        TextView fileNameView;
        TextView fileSizeView;
        TextView fileDateView;
        CheckBox multiSelectCheckBox;
    }

    private class CloudListAdapter extends ArrayAdapter<CloudFile> {
        private ItemCheckedListener mCheckedListener    = null;

        private CloudListAdapter(Context context, int resource) {
            super(context, resource);
            mCheckedListener = new ItemCheckedListener();
        }

        @Override
        public int getCount() {
            return mFileFolderList.size();
        }

        @Override
        public void notifyDataSetChanged() {
            Log.d(TAG, "notifyDataSetChanged");
            mFileFolderList.clear();
            mFileFolderList.addAll(mFolderList);
            mFileFolderList.addAll(mFileList);
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.cloud_item_layout, parent, false);
                holder = new ViewHolder();
                holder.fileThumb = (ImageView)convertView.findViewById(R.id.file_thumb);
                holder.fileNameView = (TextView)convertView.findViewById(R.id.file_name_view);
                holder.fileSizeView = (TextView)convertView.findViewById(R.id.file_size_view);
                holder.fileDateView = (TextView)convertView.findViewById(R.id.file_date_view);
                holder.multiSelectCheckBox = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                holder.multiSelectCheckBox.setOnCheckedChangeListener(mCheckedListener);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            // Update holder content
            CloudFile file = mFileFolderList.get(position);
            // multiSelectCheckBox
            holder.multiSelectCheckBox.setTag(position);
            if (mSelectedPositions != null && mSelectedPositions.contains(position))
                holder.multiSelectCheckBox.setChecked(true);
            else
                holder.multiSelectCheckBox.setChecked(false);
            // fileThumb/fileSizeView
            if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
                holder.fileThumb.setImageResource(R.drawable.folder);
                holder.fileSizeView.setVisibility(View.INVISIBLE);
            } else {
                holder.fileThumb.setImageResource(ThumbnailCreator.getDefThumbnailsByName(file.getFilePath()));
                holder.fileSizeView.setText(file.getFileSize() + "");
            }
            // fileNameView
            holder.fileNameView.setText(file.getFilePath());
            // fileDateView
            holder.fileDateView.setText(file.getCreateDate());

            return convertView;
        }

        public void addMultiPosition(int index) {
            CloudFile file = mFileFolderList.get(index);
            if (mSelectedPositions.contains(index)) {
                mSelectedPositions.remove(Integer.valueOf(index));
                mSelectFileList.remove(file);
            } else {
                mSelectedPositions.add(index);
                mSelectFileList.add(file);
            }
        }

        /**
         * This will turn off multi-select and hide the multi-select buttons at the
         * bottom of the view.
         */
        public void clearMultiSelect() {
            // TODO Handle multiple select

            if(mSelectedPositions != null && !mSelectedPositions.isEmpty())
                mSelectedPositions.clear();

            if(mSelectFileList!= null && !mSelectFileList.isEmpty())
                mSelectFileList.clear();

        }
        /**
         * This class listening ListView item's select CheckBox checked event.
         * When user checked a item, class add this item's index to {@link #mSelectedPositions},
         * and add path which the item stand for to {@link #mSelectFileList}
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
                        mSelectFileList.add(mFileFolderList.get(r_index));
                        isChanged = true;
                    }
                } else {
                    if (mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.remove((Integer)r_index);
                        mSelectFileList.remove(mFileFolderList.get(r_index));
                        isChanged = true;
                    }
                }
                Log.d(TAG, "Current selected items: " + mSelectedPositions.toString());

                /*
                if (isChanged && mListener != null) {
                    mListener.onSelectUploadChanged(mSelectedPath);
                }
                */
            }
        }
    }
}
