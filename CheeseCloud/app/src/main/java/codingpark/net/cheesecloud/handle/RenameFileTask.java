package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.WsResultType;

/**
 * This class used to rename file or folder on remote server refresh UI(ListView +
 * Bottom Bar).
 * @author Ethan Shan
 * @version 1.0
 * @created 14-十一月-2014 14:36:29
 */
public class RenameFileTask extends AsyncTask<Void,Void,Integer> {

    private ArrayAdapter mAdapter           = null;
    private CloudFile mFile                 = null;
    private Context mContext                = null;
    private OnRenameFileCompletedListener mListener     = null;

    /**
     * The Constructor
     *
     * @param file The CloudFile object which store the to be renamed file with new filename
     * information.
     * @param adapter    The list view ArrayAdapter
     * @param context    The application context
     */
    public RenameFileTask(Context context, ArrayAdapter adapter,
                          CloudFile file) {
        mContext = context;
        mAdapter = adapter;
        mFile = file;
    }

    /**
     * The constructor
     * @param context    The application context
     * @param adapter    The list view ArrayAdapter
     * @param file The CloudFile object which store the to be renamed file with new filename
     * information.
     * @param listener    When rename file task complete call this callback
     */
    public RenameFileTask(Context context, ArrayAdapter adapter,
                          CloudFile file, OnRenameFileCompletedListener listener) {
        mContext = context;
        mAdapter = adapter;
        mFile = file;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int result = WsResultType.Success;
        result = ClientWS.getInstance(mContext).renameObj_wrapper(mFile);
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case WsResultType.Success:
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onRenameFileCompleted(result);
                return;
            default:
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onRenameFileCompleted(result);
                return;
        }
    }


    /**
     * The object who start RenameFileTask may be interested in the task completed
     * action. If care, it should implement this interface and implement
     * onRenameFileCompleted function. Set implementation as parameter of the
     * constructor.When task finish, RenameFileTask will call oRenameFileCompleted
     * function and pass the task return result as a parameter.
     */
    public static interface OnRenameFileCompletedListener {
        public void onRenameFileCompleted(int result);
    }
}

