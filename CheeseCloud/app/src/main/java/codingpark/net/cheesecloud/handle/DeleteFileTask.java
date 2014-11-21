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
 * This class used to delete files and folders on remote server and refresh UI(ListView +
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
     * @param files      The CloudFile object list which to be deleted
     */
    public DeleteFileTask(Context context, ArrayAdapter adapter,
                          ArrayList<CloudFile> files) {
        mContext = context;
        mAdapter = adapter;
        mFiles = files;
    }

    /**
     * The constructor
     * @param context    The application context
     * @param adapter    The list view ArrayAdapter
     * @param files      The CloudFile object list which to be deleted
     * @param listener    When delete files and folders task complete call this callback
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


    /**
     * DeleteFileTask is async, when host create and execute the task, host maybe
     * want to know this task execute result. This interface for this reason.
     * The host need implement this interface, and pass itself as parameter to
     * the DeleteFileTask's constructor. After the task execute completed, will
     * call onDeleteFileCompleted with the execute result(int).
     */
    public static interface OnDeleteFileCompletedListener {
        public void onDeleteFileCompleted(int result);
    }
}

