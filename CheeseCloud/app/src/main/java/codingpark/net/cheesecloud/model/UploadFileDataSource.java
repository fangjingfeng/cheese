package codingpark.net.cheesecloud.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.handle.LocalDatabase;

/**
 * Created by ethanshan on 14-10-20.
 * The class provide operation interface to manage uploadfile table
 */
public class UploadFileDataSource {

    private Context mContext            = null;
    private SQLiteDatabase database     = null;
    private SQLiteOpenHelper dbHelper   = null;

    public static final class UploadFileEntry implements BaseColumns {
        /**
         * The table name which to store the user selected files information.
         */
        public static final String TABLE_NAME   = "upload_files";
        /**
         * The user guid at the server database
         */
        public static final String COLUMN_REMOTE_ID    = "remote_id";
        /**
         * Type: TEXT
         * Description: The file absolutely path in local file system
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_FILEPATH     = "filepath";
        /**
         * Type: VARCHAR(50)
         * Description: The whole file MD5 value
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_MD5          = "md5";
        /**
         * Type: INTEGER
         * Description: The file parent folder id. Associate with {@link #_ID}
         * -1: The selected root
         * >=0:
         * Default: -1
         */
        public static final String COLUMN_PARENT_ID    = "parent_id";
        /**
         * Type: INTEGER
         * Description: The file parent folder id. Associate with {@link #_ID}
         * -1: The selected root
         * >=0:
         * Default: -1
         */
        public static final String COLUMN_REMOTE_PARENT_ID    = "remote_parent_id";
        /**
         * Type: INTEGER
         * Description: The whole file size in Byte unit
         * Default: 0
         */
        public static final String COLUMN_FILESIZE     = "filesize";
        /**
         * Type: INTEGER
         * Description: The file current upload state
         * 0: not uploaded
         * 1: uploading
         * 2: uploaded
         * Default: 0
         */
        public static final String COLUMN_STATE        = "state";
        /**
         * Type: INTEGER
         * Description: The file current uploaded size(offset from head)
         * Default: 0
         */
        public static final String COLUMN_UPLOADED_SIZE    = "uploaded_size";
        /**
         * Type: INTEGER
         * Description: The file type(not extension type)
         * 0: file
         * 1: folder
         * Default: 0
         */
        public static final String COLUMN_FILETYPE     = "filetype";
        /**
         * Type: INTEGER
         * Description: The user id(local database) who insert this item. Associate with user table's _ID
         * Default: -1
         */
        public static final String COLUMN_USERID       = "local_user_id";
    }

    public UploadFileDataSource(Context context) {
        mContext = context;
        dbHelper = new LocalDatabase(mContext);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Insert upload info to uploadfile table
     * @param file
     * @return
     *  -1: Insert error occured
     *  >0: Insert success
     */
    public long addUploadFile(UploadFile file) {
        // 1. Judge the file is exist
        //String sql = "SELECT * FROM " ;
        // 2. Insert the file to local database
        ContentValues cv = fileToContentValue(file);
        long result = database.insert(UploadFileEntry.TABLE_NAME, null, cv);
        return result;
    }

    /**
     * Delete the record by local uploadfile._ID column
     * @param id
     *  The target record _ID
     * @return
     *  true: delete success
     *  false: delete failed
     */
    public boolean deleteUploadFile(int id) {
        int result = database.delete(UploadFileEntry.TABLE_NAME, UploadFileEntry._ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean updateUploadFile(UploadFile file) {
        /*
        ContentValues cv = fileToContentValue(file);
        int result = database.update(UploadFileEntry.TABLE_NAME, cv,
                UploadFileEntry.COLUMN_FILEPATH + "=?" +
                " and " + , new String[] {file.getFilepath()});
        return result > 0;
                */
        return true;
    }

    public List<UploadFile> getAllUploadFile() {
        List<UploadFile> fileList = new ArrayList<UploadFile>();
        Cursor cursor = database.query(UploadFileEntry.TABLE_NAME, new String[]{
                UploadFileEntry._ID,
                UploadFileEntry.COLUMN_FILEPATH,
                UploadFileEntry.COLUMN_FILESIZE,
                UploadFileEntry.COLUMN_FILETYPE,
                UploadFileEntry.COLUMN_MD5,
                UploadFileEntry.COLUMN_PARENT_ID,
                UploadFileEntry.COLUMN_REMOTE_ID,
                UploadFileEntry.COLUMN_REMOTE_PARENT_ID,
                UploadFileEntry.COLUMN_STATE,
                UploadFileEntry.COLUMN_UPLOADED_SIZE,
                UploadFileEntry.COLUMN_USERID
        }, null, null, null, null, null);
        while (cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }

    private UploadFile cursorToFile(Cursor cursor) {
        UploadFile file = new UploadFile();
        file.setId(cursor.getInt(0));
        file.setFilepath(cursor.getString(1));
        file.setFilesize(cursor.getLong(2));
        file.setFiletype(cursor.getInt(3));
        file.setMd5(cursor.getString(4));
        file.setParent_id(cursor.getInt(5));
        file.setRemote_id(cursor.getString(6));
        file.setRemote_parent_id(cursor.getString(7));
        file.setState(cursor.getInt(8));
        file.setUploadsize(cursor.getLong(9));
        file.setLocal_user_id(cursor.getInt(10));
        return file;
    }

    /**
     * Convert UploadFile object to ContentValues object
     * @param file
     *  UploadFile object
     * @return
     *  ContentValues object
     */
    private ContentValues fileToContentValue(UploadFile file) {
        ContentValues cv = new ContentValues();
        cv.put(UploadFileEntry.COLUMN_FILEPATH, file.getFilepath());
        cv.put(UploadFileEntry.COLUMN_FILESIZE, file.getFilesize());
        cv.put(UploadFileEntry.COLUMN_FILETYPE, file.getFiletype());
        cv.put(UploadFileEntry.COLUMN_MD5, file.getMd5());
        cv.put(UploadFileEntry.COLUMN_PARENT_ID, file.getParent_id());
        cv.put(UploadFileEntry.COLUMN_REMOTE_ID, file.getRemote_id());
        cv.put(UploadFileEntry.COLUMN_REMOTE_PARENT_ID, file.getRemote_parent_id());
        cv.put(UploadFileEntry.COLUMN_STATE, file.getState());
        cv.put(UploadFileEntry.COLUMN_UPLOADED_SIZE, file.getUploadsize());
        cv.put(UploadFileEntry.COLUMN_USERID, file.getLocal_user_id());
        return cv;
    }
}
