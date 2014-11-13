package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.wsi.WsFolder;

/**
 * This class used to create a folder on remote server
 * refresh UI(ListView + Bottom Bar).
 * In constructor,
 */
public class CreateDirTask extends AsyncTask<Void,Void,Integer> {

    private ArrayAdapter mAdapter        = null;
    private CloudFile mFolder = null;
    private Context mContext                = null;
    private OnCreateFolderCompletedListener mListener   = null;

    /**
     * Constructor
     * @param context Context object
     * @param adapter ArrayAdapter object
     */
    public CreateDirTask(Context context, ArrayAdapter adapter,
                         CloudFile folder) {
        mContext = context;
        mAdapter = adapter;
        mFolder = folder;
    }

    /**
     * Constructor
     * @param context Context object
     * @param adapter ArrayAdapter object
     * @param folder To be created folder information
     * @param listener When pull data task complete call this callback
     */
    public CreateDirTask(Context context, ArrayAdapter adapter,
                         CloudFile folder, OnCreateFolderCompletedListener listener) {
        mContext = context;
        mAdapter = adapter;
        mFolder = folder;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int result = WsResultType.Success;
        result = createFolder_wrapper();
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case WsResultType.Success:
                // TODO Refresh ListView
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onCreateFOlderCompleted(result);
                return;
            default:
                // TODO Warning pull error
                return;
        }
    }

    private int createFolder_wrapper() {
        int result = WsResultType.Success;
        WsFolder wsFolder = new WsFolder();
        wsFolder.Name = mFolder.getFilePath();
        wsFolder.FatherID = mFolder.getRemote_parent_id();
        result = ClientWS.getInstance(mContext).createFolder(wsFolder);
        return result;
    }

    public static interface OnCreateFolderCompletedListener {
        public void onCreateFOlderCompleted(int result);
    }
}

