package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

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
     * The user select disk object(CloudFile) key(Used by Intent.getExtra)
     */
    public static final String SELECT_DISK_KEY      = "select_disk_key";
    private static CloudFile mRootDisk      = null;

    // Folder and File cloud file list, fill up by PullCloudFileTask
    private ArrayList<CloudFile> mFolderList            = null;
    private ArrayList<CloudFile> mFileList              = null;
    // Store files + folders, used by ArrayAdapter, the data is mFolderList + mFileList
    private ArrayList<CloudFile> mFileFolderList        = null;
    // Store user selected files object
    private ArrayList<CloudFile> mSelectedFileList = null;
    // Store user selected files index in the ListView
    private ArrayList<Integer> mSelectedPositions   = null;
    // Remember current folder full path
    private Stack<CloudFile> mPathStack                 = null;
    // Path bar, use to show current directory path
    private LinearLayout path_bar_container             = null;
    // List adapter
    private CloudListAdapter mAdapter                   = null;

    // UI elements
    private LayoutInflater mInflater                    = null;
    private LinearLayout mListContainer                 = null;
    private ProgressBar mLoadingView                    = null;
    private ActionMode mActionMode                      = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        // Set action bar show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_cloud_files);
        getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

        mListContainer = (LinearLayout)findViewById(R.id.listcontainer);
        mLoadingView = (ProgressBar)findViewById(R.id.loading);

        mFolderList = new ArrayList<CloudFile>();
        mFileList = new ArrayList<CloudFile>();
        mFileFolderList = new ArrayList<CloudFile>();
        mSelectedFileList = new ArrayList<CloudFile>();
        mPathStack = new Stack<CloudFile>();
        mSelectedPositions = new ArrayList<Integer>();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the initial list mode
        Intent recIntent = getIntent();
        mRootDisk = (CloudFile)recIntent.getParcelableExtra(SELECT_DISK_KEY);
        // Initial path bar
        path_bar_container = (LinearLayout)findViewById(R.id.pathBarContainer);
        setPathbar();

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 1. Refresh CAB
                if (isMultiSelect()) {
                    // In multiple select mode, didn't handle long item click event
                } else {
                    // Add selected data and refresh UI
                    mAdapter.addMultiPosition(position);
                    mAdapter.notifyDataSetChanged();
                    // Show CAB
                    refreshCAB();

                }
                return true;
            }
        });

        // Initial list adapter
        mAdapter = new CloudListAdapter(this, R.layout.cloud_item_layout);
        setListAdapter(mAdapter);

        refreshPathBar();
        refreshList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cloud_files_activity_ab_menu, menu);
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
                refreshCAB();
            }
        }
        return true;
    }

    private void setPathbar() {
        mPathStack.push(mRootDisk);
    }

    private void setLoadingViewVisible(boolean visible){
        if(null != mLoadingView && null != mListContainer){
            mListContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
            mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void refreshCAB() {
        if (isMultiSelect()) {
            if (mActionMode == null)
                startActionMode(mActionModeCallback);
            String title = getString(R.string.cab_title);
            mActionMode.setTitle(String.format(title, mSelectedFileList.size()));
            Log.d(TAG, "refreshCAB: " + title);
        } else {
            if (mActionMode != null)
                mActionMode.finish();
        }
    }

    private void refreshList() {
        Log.d(TAG, "Call execute.");
        // Hide list view and show loading view
        setLoadingViewVisible(true);
        new PullFileListTask(this, mAdapter, mPathStack.peek(), mFileList, mFolderList).execute();
    }

    private void refreshPathBar() {
        Log.d(TAG, "Start refresh path bar");
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
            mAdapter.addMultiPosition(position);
            mAdapter.notifyDataSetChanged();
            refreshCAB();
        } else {
            if (file.getFileType() == CloudFileType.TYPE_FILE) {
                mAdapter.addMultiPosition(position);
                mAdapter.notifyDataSetChanged();
                refreshCAB();
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
        refreshCAB();
    }

    @Override
    public void onWSTaskDataFinish(CloudFile data) {

    }

    private boolean isMultiSelect() {
        return mSelectedFileList.size() > 0;
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
            setLoadingViewVisible(false);
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
                mSelectedFileList.remove(file);
            } else {
                mSelectedPositions.add(index);
                mSelectedFileList.add(file);
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

            if(mSelectedFileList != null && !mSelectedFileList.isEmpty())
                mSelectedFileList.clear();

        }
        /**
         * This class listening ListView item's select CheckBox checked event.
         * When user checked a item, class add this item's index to {@link #mSelectedPositions},
         * and add path which the item stand for to {@link #mSelectedFileList}
         */
        private class ItemCheckedListener implements CompoundButton.OnCheckedChangeListener{
            //private static final String TAG     = "ItemSelectedListener";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Index: " + buttonView.getTag() + "\nChecked: " + isChecked);
                int r_index = Integer.valueOf(buttonView.getTag().toString());
                if (isChecked) {
                    if (!mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.add(r_index);
                        mSelectedFileList.add(mFileFolderList.get(r_index));
                    }
                } else {
                    if (mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.remove((Integer)r_index);
                        mSelectedFileList.remove(mFileFolderList.get(r_index));
                    }
                }
                refreshCAB();
                Log.d(TAG, "Current selected items: " + mSelectedPositions.toString());
            }
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        private boolean selectValidAction = false;

        /**
         * TODO Do handler according to selected action
         * For test: Just clear selected data and refresh UI
         */
        private void handleAction() {
            mAdapter.clearMultiSelect();
            mAdapter.notifyDataSetChanged();
        }
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.d(TAG, "cab creating");
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cloud_files_activity_cab_menu, menu);
            mActionMode = mode;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d(TAG, "Action item clicked: " + item.getItemId());
            selectValidAction = true;
            handleAction();
            switch (item.getItemId()) {
                case R.id.cab_menu_share:
                    Toast.makeText(CloudFilesActivity.this, "Share action", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.cab_menu_delete:
                    Toast.makeText(CloudFilesActivity.this, "Delete action", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.cab_menu_download:
                    Toast.makeText(CloudFilesActivity.this, "Download action", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.cab_menu_cut:
                    Toast.makeText(CloudFilesActivity.this, "Cut action", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.cab_menu_edit:
                    Toast.makeText(CloudFilesActivity.this, "Edit action", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.cab_menu_copy:
                    Toast.makeText(CloudFilesActivity.this, "Copy action", Toast.LENGTH_SHORT).show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG, "CAB destroy");
            // Cancel multiple select mode, clear selected and refresh UI(Not need sync data from server)
            if (!selectValidAction) {
                mAdapter.clearMultiSelect();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(CloudFilesActivity.this, "Cancel Selected!", Toast.LENGTH_SHORT).show();
            }
            selectValidAction = false;
            mActionMode = null;
        }


    };

}