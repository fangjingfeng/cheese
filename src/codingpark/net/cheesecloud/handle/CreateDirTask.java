package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.WsResultType;

/**
 * 这个类用于在远程服务器上创建一个文件夹刷新UI(列表视图+底栏)。
 * This class used to create a folder on remote server refresh UI(ListView +
 * Bottom Bar).
 * @author Ethan Shan
 * @version 1.0
 * @created 14-十一月-2014 14:36:29
 */
public class CreateDirTask extends AsyncTask<Void,Void,Integer> {

    private ArrayAdapter mAdapter        = null;
    private CloudFile mFolder = null;
    private Context mContext                = null;
    private OnCreateFolderCompletedListener mListener   = null;

    /**
     * The Constructor
     *
     * @param folder    The CloudFile object which store the to be created folder
     * information.
     * @param adapter    The list view ArrayAdapter
     * @param context    The application context
     */
    public CreateDirTask(Context context, ArrayAdapter adapter,
                         CloudFile folder) {
        mContext = context;
        mAdapter = adapter;
        mFolder = folder;
    }

    /**
     * The constructor
     *
     * @param listener    When pull data task complete call this callback
     * @param folder    The CloudFile object which store the to be created folder
     * information.
     * @param adapter    The list view ArrayAdapter
     * @param context    The application context
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
        result = ClientWS.getInstance(mContext).createFolderCloud_wrapper(mFolder);
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case WsResultType.Success:
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onCreateFolderCompleted(result);
                return;
            default:
                // TODO Warning pull error
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onCreateFolderCompleted(result);
                return;
        }
    }


    /**
     * The object who start CreateDirTask may be interested in the task completed
     * action. If care, it should implement this interface and implement
     * onCreateFolderCompleted function. Set implementation as parameter of the
     * constructor.When task finish, CreateDirTask will call onCreateFolderCompleted
     * function and pass the task return result as a parameter.
     */
    public static interface OnCreateFolderCompletedListener {
        public void onCreateFolderCompleted(int result);
    }
}

