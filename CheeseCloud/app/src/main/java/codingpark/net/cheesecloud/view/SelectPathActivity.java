package codingpark.net.cheesecloud.view;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.eumn.UploadFileType;
import codingpark.net.cheesecloud.eumn.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.model.UploadFile;
import codingpark.net.cheesecloud.wsi.WsFolder;

public class SelectPathActivity extends ListActivity {
    private static final String TAG     = "SelectPathActivity";

    private Button select_path_cancel_bt    = null;
    private Button select_path_ok_bt        = null;

    public static final String RESULT_SELECTED_REMOTE_PARENT_ID = "";
    // TODO The value should fetch from server dynamic
    private String remote_parent_id                     = "395ED821-E528-42F0-8EA7-C59F258E7435";
    private ArrayList<String> mFolderNameList           = null;
    private ArrayList<UploadFile> mFolderList           = null;
    private Stack<UploadFile> mPathStack                = null;

    private SelectPathAdapter mAdapter                  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);

        // Initial ActionBar
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // 2. Set the title
        getActionBar().setTitle(R.string.select_path_activity_action_bar_title);

        // Initial path list
        mFolderNameList = new ArrayList<String>();
        mFolderList = new ArrayList<UploadFile>();
        mPathStack = new Stack<UploadFile>();

        // Initial list adapter
        mAdapter = new SelectPathAdapter();
        setListAdapter(mAdapter);

        initUI();
        initHandler();
        refreshList();
    }

    private void refreshList() {
        Log.d(TAG, "Call execute.");
        new PullFolderListTask().execute();
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
        getListView().setEmptyView(findViewById(android.R.id.empty));

        select_path_cancel_bt = (Button) findViewById(R.id.select_upload_path_cancel_bt);
        select_path_ok_bt = (Button) findViewById(R.id.select_upload_path_ok_bt);
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
                // Return selected remote parent id to UploadActivity
                Intent intent = new Intent();
                intent.putExtra(RESULT_SELECTED_REMOTE_PARENT_ID, remote_parent_id);
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
        UploadFile file = mFolderList.get(position);
        mPathStack.push(file);
        refreshList();
    }

    private void refreshPathBar() {

    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        ImageView icon;
        TextView rightView;
    }

    public class SelectPathAdapter extends ArrayAdapter<UploadFile> {

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
                holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
                // Update icon src
                holder.icon.setImageResource(R.drawable.folder);
                holder.rightView = (TextView)convertView.findViewById(R.id.file_name_view);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.rightView.setText(mFolderList.get(position).getFilepath());

            return convertView;
        }
    }

    /**
     * This class used to pull folder from remote server and trigger
     * refresh UI(ListView + Bottom Bar).
     */
    private class PullFolderListTask extends AsyncTask<Void,Void,Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            mFolderList.clear();
            int result = WsResultType.Success;
            if (mPathStack.size() == 0) {
                // TODO Need pull disk list
                result = getDisk_wrapper();
            } else {
                // TODO Need pull the sub folder list
                result = getFolderList_wrapper(mPathStack.peek());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case WsResultType.Success:
                    // TODO Refresh ListView
                    mAdapter.notifyDataSetChanged();
                    return;
                default:
                    // TODO Warning pull error
                    return;
            }
        }

        /**
         * Wrapper getDisk(Web Service Interface)
         * Convert WsFolder to UploadFile
         * @return int, the getDisk execute result
         */
        private int getDisk_wrapper() {
            int result = WsResultType.Success;
            ArrayList<WsFolder> r_wsFolder = new ArrayList<WsFolder>();
            result = ClientWS.getInstance(SelectPathActivity.this).getDisk(r_wsFolder);
            for (WsFolder ws_f : r_wsFolder) {
                UploadFile f = new UploadFile();
                f.setFiletype(UploadFileType.TYPE_FOLDER);
                f.setRemote_id(ws_f.ID);
                f.setFilepath(ws_f.Name);
                mFolderList.add(f);
            }
            return result;
        }

        /**
         * Wrapper getFolderList(Web Service Interface)
         * Convert WsFolder to UploadFile
         * @return int, the getFolderList execute result
         * {@link codingpark.net.cheesecloud.eumn.WsResultType}
         */
        private int getFolderList_wrapper(UploadFile file) {
            int result = WsResultType.Success;
            ArrayList<WsFolder> r_wsFolderList = new ArrayList<WsFolder>();
            WsFolder wsFolder = new WsFolder();
            wsFolder.ID = file.getRemote_id();
            result = ClientWS.getInstance(SelectPathActivity.this).getFolderList(wsFolder, null, r_wsFolderList);
            if (result == WsResultType.Success) {
                for (WsFolder tmp_folder : r_wsFolderList) {
                    UploadFile f = new UploadFile();
                    f.setFiletype(UploadFileType.TYPE_FOLDER);
                    f.setRemote_id(tmp_folder.ID);
                    f.setFilepath(tmp_folder.Name);
                    mFolderList.add(f);
                }
            }
            return result;
        }
    }


}
