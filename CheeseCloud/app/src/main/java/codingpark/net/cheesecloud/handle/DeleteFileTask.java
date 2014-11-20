package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.wsi.WsFolder;

// TODO Comment this class
/**
 * This class used to create a folder on remote server refresh UI(ListView +
 * Bottom Bar).
 * @author Ethan Shan
 * @version 1.0
 * @created 14-十一月-2014 14:36:29
 */
public class DeleteFileTask extends AsyncTask<Void,Void,Integer> {

    private ArrayAdapter mAdapter           = null;
    private ArrayList<CloudFile> mFiles                = null;
    private Context mContext                = null;
    private OnDeleteFileCompletedListener mListener   = null;

    /**
     * The Constructor
     * information.
     * @param context    The application context
     * @param adapter    The list view ArrayAdapter
     */
    public DeleteFileTask(Context context, ArrayAdapter adapter,
                          ArrayList<CloudFile> files) {
        mContext = context;
        mAdapter = adapter;
        mFiles = files;
    }

    /**
     * The constructor
     *
     * @param listener    When pull data task complete call this callback
     * @param files The CloudFile object which store the to be created folder
     * information.
     * @param adapter    The list view ArrayAdapter
     * @param context    The application context
     */
    public DeleteFileTask(Context context, ArrayAdapter adapter,
                          ArrayList<CloudFile> files, OnDeleteFileCompletedListener listener) {
        mContext = context;
        mAdapter = adapter;
        mFiles = files;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int result = WsResultType.Success;
        ArrayList<String> fileIds = new ArrayList<String>();
        ArrayList<String> folderIds = new ArrayList<String>();
        for (CloudFile file : mFiles) {
            if (file.getFileType() == CloudFileType.TYPE_FILE) {
                fileIds.add(file.getRemote_id());
            } else {
                folderIds.add(file.getRemote_id());
            }
        }
        ClientWS.getInstance(mContext).deleteFolderAndFile(fileIds, folderIds);
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case WsResultType.Success:
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onDeleteFileCompleted(result);
                return;
            default:
                // TODO Warning pull error
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onDeleteFileCompleted(result);
                return;
        }
    }


    public static interface OnDeleteFileCompletedListener {
        public void onDeleteFileCompleted(int result);
    }
}

