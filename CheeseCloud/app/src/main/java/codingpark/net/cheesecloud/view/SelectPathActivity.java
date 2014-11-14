package codingpark.net.cheesecloud.view;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.handle.PullFileListTask;

/**
 * The class used to list all folder in web server, user select one folder,
 * then return the folder information to {@link SelectUploadActivity}.
 * It upload files to the destination.
 * When user click select_path_ok_bt, the class call mPathStack.peek(), add the result to intent
 * which will received by UploadActivity. Default destination folder is null, so when UploadActivity
 * receive a null folder, it will use the user id as the destination folder id(My Cloud Folder).
 */
public class SelectPathActivity extends ListActivity implements View.OnClickListener, PullFileListTask.OnPullDataReadyListener {
    private static final String TAG     = SelectPathActivity.class.getSimpleName();

    private Button select_path_cancel_bt    = null;
    private Button select_path_ok_bt        = null;

    public static final String RESULT_SELECTED_REMOTE_FOLDER_ID    = "selected_remote_folder_id";
    private ArrayList<CloudFile> mFolderList           = null;
    private Stack<CloudFile> mPathStack                = null;
    // Path bar, use to show current directory path
    private LinearLayout path_bar_container = null;

    private SelectPathAdapter mAdapter                  = null;

    private LinearLayout mListContainer                 = null;
    private ProgressBar mLoadingView                    = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);

        // Initial ActionBar
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.select_path_activity_action_bar_title);
        ImageView view = (ImageView)findViewById(android.R.id.home);
        view.setPadding(0, 0, 0, 0);
        Log.d(TAG, "Change up logo");
        // Initial path list
        mFolderList = new ArrayList<CloudFile>();
        mPathStack = new Stack<CloudFile>();
        // Initial mPathStack with ROOT_ID when not select any folder
        CloudFile file = new UploadFile();
        file.setRemote_id(CheeseConstants.ROOT_ID);
        file.setFilePath("磁盘");
        mPathStack.push(file);


        // Initial list adapter
        mAdapter = new SelectPathAdapter();
        setListAdapter(mAdapter);

        initUI();
        initHandler();
        refreshPathBar();
        refreshList();
        refreshBottomBar();
    }

    private void refreshList() {
        Log.d(TAG, "Call execute.");
        setLoadingViewVisible(true);
        new PullFileListTask(this, mAdapter, mPathStack.peek(), null, mFolderList, this).execute();
    }

    private void initUI() {
        /*
        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
        */
        //ListView lv = (ListView)findViewById(android.R.id.list);
        //TextView emptyText = (TextView)findViewById(R.layout.select_path_activity_list_empty);

        // Set list view empty widget
        //getListView().setEmptyView(findViewById(android.R.id.empty));
        mLoadingView = (ProgressBar)findViewById(R.id.loading);
        mListContainer = (LinearLayout)findViewById(R.id.listcontainer);

        select_path_cancel_bt = (Button) findViewById(R.id.select_upload_path_cancel_bt);
        select_path_ok_bt = (Button) findViewById(R.id.select_upload_path_ok_bt);
        path_bar_container = (LinearLayout) findViewById(R.id.pathBarContainer);
    }

    private void initHandler() {
        select_path_cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel select path action!");
                setResult(RESULT_CANCELED);
                SelectPathActivity.this.finish();
            }
        });

        select_path_ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Select this path!");
                // Return selected remote folder id to UploadActivity
                Intent intent = new Intent();
                if (mPathStack.peek() != null) {
                    String id = mPathStack.peek().getRemote_id();
                    if (id.equals(CheeseConstants.ROOT_ID)) {
                        intent.putExtra(RESULT_SELECTED_REMOTE_FOLDER_ID,
                                AppConfigs.current_remote_user_id);
                    }
                    else {
                        intent.putExtra(RESULT_SELECTED_REMOTE_FOLDER_ID, id);
                    }
                }
                else {
                    intent.putExtra(RESULT_SELECTED_REMOTE_FOLDER_ID,
                            AppConfigs.current_remote_user_id);
                }

                setResult(RESULT_OK, intent);
                SelectPathActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_path, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 1. Refresh bottom bar select path button text
        CloudFile file = mFolderList.get(position);
        mPathStack.push(file);
        refreshPathBar();
        refreshList();
        refreshBottomBar();
    }

    private void refreshBottomBar() {
        if (mPathStack.size() > 1)
            select_path_ok_bt.setText(
                    getString(R.string.select_path_activity_ok_bt_prefix_string)
                            + mPathStack.peek().getFilePath());
        else
            select_path_ok_bt.setText(
                    getString(R.string.select_path_activity_ok_bt_prefix_string));

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

    /**
     * Listen path bar item click event, when user click the item, list
     * view would be switch to the folder which the item stand for.
     * @param v The view(path bar item)
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "Item clicked!");
        int index = Integer.valueOf(v.getTag().toString());
        while (index < (mPathStack.size() - 1))
            mPathStack.pop();
        refreshPathBar();
        refreshList();
        refreshBottomBar();
    }

    private void setLoadingViewVisible(boolean visible){
        if(null != mLoadingView && null != mListContainer){
            mListContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
            mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPullDataReady(int result) {
        setLoadingViewVisible(false);
    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        ImageView icon;
        TextView rightView;
    }

    public class SelectPathAdapter extends ArrayAdapter<CloudFile> {

        public SelectPathAdapter() {
            super(SelectPathActivity.this, R.layout.upload_item_layout, mFolderList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) SelectPathActivity.this.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.select_path_item_layout, parent, false);

                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.file_thumb);
                // Update icon src
                holder.icon.setImageResource(R.drawable.folder);
                holder.rightView = (TextView)convertView.findViewById(R.id.fileNameView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.rightView.setText(mFolderList.get(position).getFilePath());

            return convertView;
        }
    }



}
