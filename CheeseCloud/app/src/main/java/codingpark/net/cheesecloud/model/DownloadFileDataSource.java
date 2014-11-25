package codingpark.net.cheesecloud.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;

/**
 * The "download_files" table column description class
 * @author Ethan Shan
 * @version 1.0
 */
public class DownloadFileDataSource {

    private Context mContext = null;
    private SQLiteDatabase database = null;
    private SQLiteOpenHelper dbHelper = null;

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
        mContext = context;
        dbHelper = new LocalDatabase(mContext);
    }

    /**
     * Add DownloadFile record to download_files table.
     *
     * @param file    The DownloadFile object, stored to be downloaded file's
     * information
     */
    public boolean addDownloadFile(DownloadFile file){
        ContentValues cv = fileToContentValue(file);
        cv.put(DownloadFileEntry.COLUMN_LOCAL_USER_ID, AppConfigs.current_local_user_id);
        long l_id = database.insert(DownloadFileEntry.TABLE_NAME, null, cv);
        return l_id >= 0;
    }

    /**
     * As the object owner manipulate download_files table finish, call this function
     * to free related system resources
     */
    public void close(){
        if (database != null)
            database.close();
        if (dbHelper != null)
            dbHelper.close();
    }

    /**
     * Delete the record by the given parameter file
     *
     * @param file    The DownloadFile object which stored the record information to
     * be deleted
     * @return If deleted rows > 0, return true. else return false;
     */
    public boolean deleteDownloadFile(DownloadFile file){
        int rows = database.delete(DownloadFileEntry.TABLE_NAME, DownloadFileEntry._ID + " =? ",
                new String[] {String.valueOf(file.getId())});
        return rows > 0;
    }

    /**
     * Query all record
     */
    public ArrayList<DownloadFile> getAllDownloadFile(){
        ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
        Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
                DownloadFileEntry.COLUMN_ARRAY,
                DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? ", new String[] {String.valueOf(AppConfigs.current_local_user_id)},
                null, null, DownloadFileEntry._ID);
        while(cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }

    /**
     * Query all not download completed record which belong to current login user
     */
    public ArrayList<DownloadFile> getNotDownloadedFile(){
        ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
        Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
                DownloadFileEntry.COLUMN_ARRAY,
                DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? and " + DownloadFileEntry.COLUMN_STATE + " !=? ",
                new String[] {String.valueOf(AppConfigs.current_local_user_id), String.valueOf(DownloadFileState.DOWNLOADED)},
                null, null, DownloadFileEntry._ID);
        while(cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }

    /**
     * Query all record by the target state
     *
     * @param state    The target state
     */
    public ArrayList<DownloadFile> getAllDownloadFileByState(int state){
        ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
        Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
                DownloadFileEntry.COLUMN_ARRAY,
                DownloadFileEntry.COLUMN_STATE + " =? and " + DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =?" ,
                new String[] {String.valueOf(state), String.valueOf(AppConfigs.current_local_user_id)}, null, null, DownloadFileEntry._ID);
        while(cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }

    /**
     * Before manipulate download_files table, need call this function to prepare a
     * writable database object.
     */
    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Update the record by the given parameter file.
     *
     * @param file    The DownloadFile object stored the record information to be
     * deleted
     * @return If affected record number > 0, return true, else return false;
     */
    public boolean updateDownloadFile(DownloadFile file){
        int rows = database.update(DownloadFileEntry.TABLE_NAME, fileToContentValue(file),
                DownloadFileEntry._ID + " =? ",new String[] {String.valueOf(file.getId())});
        return rows > 0;
    }

    /**
     * Convert DownloadFile object to ContentValues object
     *
     * @param file DownloadFile object
     * @return ContentValues object
     */
    private ContentValues fileToContentValue(DownloadFile file) {
        ContentValues cv = new ContentValues();
        cv.put(DownloadFileEntry.COLUMN_DOWNLOADED_SIZE, file.getChangedSize());
        cv.put(DownloadFileEntry.COLUMN_FILENAME, file.getFilePath());
        cv.put(DownloadFileEntry.COLUMN_FILEPATH, file.getFilePath());
        cv.put(DownloadFileEntry.COLUMN_FILESIZE, file.getFileSize());
        cv.put(DownloadFileEntry.COLUMN_LOCAL_USER_ID, file.getLocal_user_id());
        cv.put(DownloadFileEntry.COLUMN_MD5, file.getMd5());
        cv.put(DownloadFileEntry.COLUMN_REMOTE_ID, file.getRemote_id());
        // Current local table not need remote user id
        cv.put(DownloadFileEntry.COLUMN_REMOTE_USER_ID, "");
        cv.put(DownloadFileEntry.COLUMN_STATE, file.getState());
        return cv;
    }

    /**
     * Convert CloudFile object to DownloadFile object
     * Current DownloadFile property completely equals CloudFile,
     * just need force type cast
     * @param file The CloudFile object
     * @return The DownloadFile object
     */
    public static DownloadFile convertToDownloadFile(CloudFile file) {
        DownloadFile d_file = new DownloadFile(file);
        return d_file;
    }


    /**
     * Convert Cursor to DownloadFile object
     *
     * @param cursor The cursor fetch from database
     * @return DownloadFile: The DownloadFile object
     */
    private DownloadFile cursorToFile(Cursor cursor) {
        DownloadFile file = new DownloadFile();
        file.setId(cursor.getLong(0));
        file.setChangedSize(cursor.getLong(1));
        // No fileName 2
        file.setFilePath(cursor.getString(3));
        file.setFileSize(cursor.getLong(4));
        file.setLocal_user_id(cursor.getLong(5));
        file.setMd5(cursor.getString(6));
        file.setRemote_id(cursor.getString(7));
        // No remote user id 8
        file.setState(cursor.getInt(9));
        return file;
    }
}
