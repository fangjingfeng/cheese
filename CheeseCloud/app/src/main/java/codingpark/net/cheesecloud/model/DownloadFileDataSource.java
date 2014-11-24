package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.provider.BaseColumns;

import java.util.ArrayList;

import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;

/**
 * The "download_files" table column description class
 * @author Ethan Shan
 * @version 1.0
 */
public class DownloadFileDataSource {

    public static final class DownloadFileEntry implements BaseColumns {

        /**
         * The table name used to store all to be downloaded file information
         */
        public static final String TABLE_NAME = "download_files";

        /**
         * The file already have downloaded size in byte unit.
         */
        public static final String COLUMN_DOWNLOADED_SIZE = "downloaded_size";
        /**
         * The to be downloaded file name.
         */
        public static final String COLUMN_FILENAME = "filename";
        /**
         * The file logic path in remote server.
         */
        public static final String COLUMN_FILEPATH = "filepath";
        /**
         * The file's whole size in byte unit which exist in remote server.
         */
        public static final String COLUMN_FILESIZE = "filesize";
        /**
         * The user id in local user table.
         */
        public static final String COLUMN_LOCAL_USER_ID = "local_user_id";
        /**
         * The download file md5, computed from  remote server
         */
        public static final String COLUMN_MD5 = "md5";
        /**
         * The file guid in remote server
         */
        public static final String COLUMN_REMOTE_ID = "remote_id";
        /**
         * The file's owner id on remote server
         */
        public static final String COLUMN_REMOTE_USER_ID = "remote_user_id";
        /**
         * The file download state.
         */
        public static final String COLUMN_STATE = "state";

        /**
         * The download_files table all columns set
         */
        public static final String[] COLUMN_ARRAY = new String[] {
                _ID,
                COLUMN_DOWNLOADED_SIZE,
                COLUMN_FILENAME,
                COLUMN_FILEPATH,
                COLUMN_FILESIZE,
                COLUMN_LOCAL_USER_ID,
                COLUMN_MD5,
                COLUMN_REMOTE_ID,
                COLUMN_REMOTE_USER_ID,
                COLUMN_STATE
        };
    }

    /**
     * The constructor
     *
     * @param context    The application context
     */
    public DownloadFileDataSource(Context context){

    }

    /**
     * Add DownloadFile record to download_files table.
     *
     * @param file    The DownloadFile object, stored to be downloaded file's
     * information
     */
    public boolean addDownloadFile(DownloadFile file){
        return false;
    }

    /**
     * As the object owner manipulate download_files table finish, call this function
     * to free related system resources
     */
    public void close(){

    }

    /**
     * Delete the record by the given parameter file
     *
     * @param file    The DownloadFile object which stored the record information to
     * be deleted
     */
    public boolean deleteDownloadFile(DownloadFile file){
        return false;
    }

    /**
     * Query all record
     */
    public ArrayList<DownloadFile> getAllDownloadFile(){
        return null;
    }

    /**
     * Query all record by the target state
     *
     * @param state    The target state
     */
    public ArrayList<DownloadFile> getAllDownloadFileByState(int state){
        return null;
    }

    /**
     * Before manipulate download_files table, need call this function to prepare a
     * writable database object.
     */
    public void open(){

    }

    /**
     * Update the record by the given parameter file.
     *
     * @param file    The DownloadFile object stored the record information to be
     * deleted
     */
    public boolean updateDownloadFile(DownloadFile file){

        return false;
    }

    public static DownloadFile convertToDownloadFile(CloudFile file) {
        return (DownloadFile)file;
    }
}
