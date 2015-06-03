package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import codingpark.net.cheesecloud.CheeseConstants;
import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsFolder;

/**
 * This class used to pull folders and files from remote server and trigger
 * refresh UI(ListView + Bottom Bar).
 * In constructor,
 * if currentFolder equals null or currentFolder.getRemote_id() == ROOT_ID
 * return all disks list on remote server
 * else
 * return currentFolder.getRemote_id() folder's sub files list
 */
public class PullFileListTask extends AsyncTask<Void,Void,Integer> {

    private ArrayAdapter mAdapter        = null;
    private ArrayList<CloudFile> mFileList  = null;
    private ArrayList<CloudFile> mFolderList= null;
    private CloudFile mCurrentFolder        = null;
    private Context mContext                = null;
    private OnPullDataReadyListener mListener   = null;

    /**
     * Constructor
     * @param context Context object
     * @param adapter ArrayAdapter object
     * @param currentFolder User select folder object, the task will fetch sub folder/file in it
     * @param fileList The CloudFile list save the query files result
     * @param folderList The CloudFile list save the query folder result
     */
    public PullFileListTask(Context context, ArrayAdapter adapter,
                            CloudFile currentFolder, ArrayList<CloudFile> fileList,
                            ArrayList<CloudFile> folderList) {
        mContext = context;
        mAdapter = adapter;
        mCurrentFolder = currentFolder;
        mFileList = fileList;
        mFolderList = folderList;
    }

    /**
     * Constructor
     * @param context Context object
     * @param adapter ArrayAdapter object
     * @param currentFolder User select folder object, the task will fetch sub folder/file in it
     * @param fileList The CloudFile list save the query files result
     * @param folderList The CloudFile list save the query folder result
     * @param listener When pull data task complete call this callback
     */
    public PullFileListTask(Context context, ArrayAdapter adapter,
                            CloudFile currentFolder, ArrayList<CloudFile> fileList,
                            ArrayList<CloudFile> folderList, OnPullDataReadyListener listener) {
        mContext = context;
        mAdapter = adapter;
        mCurrentFolder = currentFolder;
        mFileList = fileList;
        mFolderList = folderList;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (mFolderList != null)
            mFolderList.clear();
        if (mFileList != null)
            mFileList.clear();
        int result = WsResultType.Success;
        if (mCurrentFolder == null || mCurrentFolder.getRemote_id().equals(CheeseConstants.ROOT_ID)) {
            // Pull disk list -- 返回磁盘根目录
            result = getDisk_wrapper();
        } else {
            // Pull the sub folder list  获取子文件夹列表
            result = getFolderList_wrapper(mCurrentFolder);
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case WsResultType.Success:
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null){
                	mListener.onPullDataReady(result);
                	/*if(mFileList==null&&mFolderList==null){
                		System.out.println("onPostExecute ---> null");
                		mListener.onPullDataReady(MyConstances.Retrun_file_is_null);
                	}else{
                	}*/
                }
                System.out.println("----------WsResultType.Success:----------");
                return;
            default:
                // TODO Warning pull error
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (mListener != null)
                    mListener.onPullDataReady(result);
                	System.out.println("----------default----------");
                return;
        }
    }
    /**
     * Wrapper getDisk(Web Service Interface)
     * Convert WsFolder to UploadFile
     * @return int, the getDisk execute 
     */
    private int getDisk_wrapper() {
        int result = WsResultType.Success;
        ArrayList<WsFolder> r_wsFolder = new ArrayList<WsFolder>();
        result = ClientWS.getInstance(mContext).getDisk(r_wsFolder);
        for (WsFolder ws_f : r_wsFolder) {
            CloudFile f = new CloudFile();
            f.setFileType(CloudFileType.TYPE_FOLDER);
            f.setRemote_id(ws_f.ID);
            f.setFilePath(ws_f.Name);
            mFolderList.add(f);
        }
        return result;
    }

    /**
     * Wrapper getFolderList(Web Service Interface)
     * Convert WsFolder to UploadFile
     * @return int, the getFolderList execute result
     * {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    
    private int getFolderList_wrapper(CloudFile file) {
        int result = WsResultType.Success;
        ArrayList<WsFolder> r_wsFolderList = new ArrayList<WsFolder>();
        ArrayList<WsFile> r_wsFileList = new ArrayList<WsFile>();
        WsFolder wsFolder = new WsFolder();
        wsFolder.ID = file.getRemote_id();
        result = ClientWS.getInstance(mContext).getFolderList(wsFolder, r_wsFileList, r_wsFolderList);
        if (result == WsResultType.Success) {
            if (mFolderList != null) {
                for (WsFolder tmp_folder : r_wsFolderList) {
                    CloudFile f = new CloudFile();
                    f.setFileType(CloudFileType.TYPE_FOLDER);
                    f.setRemote_id(tmp_folder.ID);
                    f.setRemote_parent_id(tmp_folder.FatherID);
                    f.setFilePath(tmp_folder.Name);
                    f.setCreateDate(tmp_folder.CreatDate);
                    mFolderList.add(f);
                }
            }
            if (mFileList != null) {
                for (WsFile tmp_file : r_wsFileList) {
                    CloudFile f = new CloudFile();
                    f.setFileType(CloudFileType.TYPE_FILE);
                    f.setRemote_id(tmp_file.ID);
                    f.setFilePath(tmp_file.FullName);
                    f.setFileSize(tmp_file.SizeB);
                    f.setCreateDate(tmp_file.CreatDate);
                    f.setThumb_uri_name(tmp_file.phyInfo.getPhyName());
                    f.setMd5(tmp_file.MD5);
                    mFileList.add(f);
                }
            }
        }
        return result;
    }
    /**
     * Created by ethanshan on 14-11-11.
     * When Pull data from server task completed, call this callback function.
     * Current will hide loading view and show list view
     */
    public static interface OnPullDataReadyListener {
        public void onPullDataReady(int result);
    }
}

