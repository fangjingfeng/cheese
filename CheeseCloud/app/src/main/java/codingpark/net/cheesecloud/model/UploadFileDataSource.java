package codingpark.net.cheesecloud.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.handle.FileManager;

/**
 * Created by ethanshan on 14-10-20.
 * The class provide operation interface to manage uploadfile table
 */
public class UploadFileDataSource {

    private Context mContext = null;
    private SQLiteDatabase database = null;
    private SQLiteOpenHelper dbHelper = null;

    public static final class UploadFileEntry implements BaseColumns {
        /**
         * The table name which to store the user selected files information.
         */
        public static final String TABLE_NAME = "upload_files";
        /**
         * Type: TEXT
         * Description: The file absolutely path in local file system
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_FILEPATH = "filepath";
        /**
         * Type: INTEGER
         * Description: The whole file size in Byte unit
         * Default: 0
         */
        public static final String COLUMN_FILESIZE = "filesize";
        /**
         * Type: INTEGER
         * Description: The file type(not extension type)
         * 0: file
         * 1: folder
         * Default: 0
         */
        public static final String COLUMN_FILETYPE = "filetype";
        /**
         * Type: VARCHAR(50)
         * Description: The whole file MD5 value
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_MD5 = "md5";
        /**
         * Type: INTEGER
         * Description: The file parent folder id. Associate with {@link #_ID}
         * -1: The selected root
         * >=0:
         * Default: -1
         */
        public static final String COLUMN_LOCAL_PARENT_FOLDER_ID = "local_parent_folder_id";
        /**
         * The user guid at the server database(Current not use)
         */
        public static final String COLUMN_REMOTE_USER_ID = "remote_user_id";
        /**
         * Type: INTEGER
         * Description: The file parent folder id. Associate with {@link #_ID}
         * -1: The selected root
         * >=0:
         * Default: -1
         */
        public static final String COLUMN_REMOTE_PARENT_FOLDER_ID = "remote_parent_folder_id";
        /**
         * Type: INTEGER
         * Description: The file current upload state
         * 0: not uploaded
         * 1: uploading
         * 2: uploaded
         * Default: 0
         */
        public static final String COLUMN_STATE = "state";
        /**
         * Type: INTEGER
         * Description: The file current uploaded size(offset from head)
         * Default: 0
         */
        public static final String COLUMN_UPLOADED_SIZE = "uploaded_size";
        /**
         * Type: INTEGER
         * Description: The user id(local database) who insert this item. Associate with
         * user table's _ID
         * Default: -1
         */
        public static final String COLUMN_LOCAL_USERID = "local_user_id";

        /**
         * UploadFile table columns array
         */
        public static final String[] COLUMN_ARRAY = new String[]{
                _ID,
                COLUMN_FILEPATH,
                COLUMN_FILESIZE,
                COLUMN_FILETYPE,
                COLUMN_MD5,
                COLUMN_LOCAL_PARENT_FOLDER_ID,
                COLUMN_REMOTE_USER_ID,
                COLUMN_REMOTE_PARENT_FOLDER_ID,
                COLUMN_STATE,
                COLUMN_UPLOADED_SIZE,
                COLUMN_LOCAL_USERID};
    }

    /**
     * Constructor
     * @param context The application context
     */
    public UploadFileDataSource(Context context) {
        mContext = context;
        dbHelper = new LocalDatabase(mContext);
    }

    /**
     * First new UploadFileDataSource object, the internal database object is null.
     * Before manipulate the upload_files table, need get writable database object from
     * DatabaseHelper object.
     */
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * As the host manipulate upload_files table completed, call
     * close to free the related system resource
     */
    public void close() {
        if (database != null)
            database.close();
        if (dbHelper != null)
            dbHelper.close();
    }

    /**
     * Insert upload info to uploadfile table
     *
     * @param file
     * @return -1: Insert error occured
     * >0: Insert success
     */
    public long addUploadFile(UploadFile file) {
        file.setLocal_user_id(AppConfigs.current_local_user_id);
        ContentValues cv = fileToContentValue(file);
        long result = database.insert(UploadFileEntry.TABLE_NAME, null, cv);
        return result;
    }

    /**
     * Insert a new record to uploadfile table
     *
     * @param file        The File object
     * @param l_parent_id The local parent id reference to uploadfile._ID column
     * @param r_parent_id The remote parent id reference to server's folder ID
     * @return The insert column's id
     */
    public long addUploadFile(File file, long l_parent_id, String r_parent_id) {
        UploadFile u_file = new UploadFile();
        // TODO Judge the table have the same path/local_user_id/state!=uploaded(If have, not need insert)
        if (file.exists()) {
            try {
                u_file.setFilePath(file.getAbsolutePath());
                if (file.isFile())
                    u_file.setMd5(FileManager.generateMD5(new FileInputStream(file)));
                else
                    u_file.setMd5("");
                u_file.setParent_id(l_parent_id);
                u_file.setRemote_parent_id(r_parent_id);
                u_file.setFileSize(file.length());
                u_file.setState(UploadFileState.NOT_UPLOAD);
                u_file.setChangedSize(0);
                u_file.setFileType(file.isFile() ? CloudFileType.TYPE_FILE : CloudFileType.TYPE_FOLDER);
                u_file.setLocal_user_id(AppConfigs.current_local_user_id);
                return addUploadFile(u_file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    /**
     * Delete the record by local uploadfile._ID column
     *
     * @param id The target record _ID
     * @return true: delete success
     * false: delete failed
     */
    public boolean deleteUploadFile(int id) {
        int result = database.delete(UploadFileEntry.TABLE_NAME, UploadFileEntry._ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    /**
     * Update the file record to database.
     * Identify by UploadFileEntry._ID
     *
     * @param file UploadFile object
     * @return true: Update success
     * false: Update failed
     */
    public boolean updateUploadFile(UploadFile file) {
        ContentValues cv = fileToContentValue(file);
        int result = database.update(UploadFileEntry.TABLE_NAME, cv,
                UploadFileEntry._ID + "=?",
                new String[]{String.valueOf(file.getId())});
        return result > 0;
    }

    /**
     * Update the origin record where the state equals orig_state to
     * new_state
     * @param orig_state Origin state
     * @param new_state New state
     * @return
     *      true: When affect record number > 0;
     *      false: When affect record number == 0;
     */
    public boolean updateUploadFileState(int orig_state, int new_state) {
        ContentValues cv = new ContentValues();
        cv.put(UploadFileEntry.COLUMN_STATE, new_state);
        int result = database.update(UploadFileEntry.TABLE_NAME, cv,
                UploadFileEntry.COLUMN_STATE + "=?",
                new String[]{String.valueOf(orig_state)});
        return result > 0;
    }


    /**
     * Delete the record by local uploadfile.state column
     *
     * @param state The target record state
     * @return true: delete success
     * false: delete failed
     */
    public boolean deleteUploadFileByState(int state) {
        int result = database.delete(UploadFileEntry.TABLE_NAME,
                UploadFileEntry.COLUMN_STATE + "=?",
                new String[]{String.valueOf(state)});
        return result > 0;
    }



    /**
     * Get all upload file list
     *
     * @return ArrayList<UploadFile>: The list include all upload file record
     * in database
     */
    public ArrayList<UploadFile> getAllUploadFile() {
        ArrayList<UploadFile> fileList = new ArrayList<UploadFile>();
        Cursor cursor = database.query(UploadFileEntry.TABLE_NAME,
                UploadFileEntry.COLUMN_ARRAY,
                UploadFileEntry.COLUMN_FILETYPE + " =? and " +
                UploadFileEntry.COLUMN_LOCAL_USERID + " =? ",
                new String[] {String.valueOf(CloudFileType.TYPE_FILE),
                        String.valueOf(AppConfigs.current_local_user_id)},
                null, null, null);
        while (cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }

    /**
     * Get all upload file filtered by the state
     * @param state Target file upload state
     * @return ArrayList<UploadFile>: The list include all the state file record
     * in database
     */
    public ArrayList<UploadFile> getAllUploadFileByState(int state) {
        ArrayList<UploadFile> fileList = new ArrayList<UploadFile>();
        Cursor cursor = database.query(UploadFileEntry.TABLE_NAME,
                UploadFileEntry.COLUMN_ARRAY,
                UploadFileEntry.COLUMN_FILETYPE + " =? and " +
                        UploadFileEntry.COLUMN_LOCAL_USERID + " =? and " +
                        UploadFileEntry.COLUMN_STATE + " =? ",
                new String[] {String.valueOf(CloudFileType.TYPE_FILE),
                        String.valueOf(AppConfigs.current_local_user_id),
                        String.valueOf(state)},
                null, null, null);
        while (cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }


    /**
     * Get the file's sub file list
     *
     * @param file the parent file
     * @return List<UploadFile>: UploadFile object list
     */
    /*
    public List<UploadFile> getSubUploadFiles(UploadFile file) {
        List<UploadFile> fileList = new ArrayList<UploadFile>();
        Cursor cursor = database.query(UploadFileEntry.TABLE_NAME,
                UploadFileEntry.COLUMN_ARRAY,
                UploadFileEntry.COLUMN_LOCAL_PARENT_FOLDER_ID + " =? and " +
                        UploadFileEntry.COLUMN_LOCAL_USERID + " =? ",
                new String[]{String.valueOf(file.getId()), String.valueOf(AppConfigs.current_local_user_id)},
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            UploadFile u = cursorToFile(cursor);
            fileList.add(u);
        }
        return fileList;
    }
    */

    /**
     * Convert Cursor to UploadFile object
     *
     * @param cursor The cursor fetch from database
     * @return UploadFile: The UploadFile object
     */
    private UploadFile cursorToFile(Cursor cursor) {
        UploadFile file = new UploadFile();
        file.setId(cursor.getInt(0));
        file.setFilePath(cursor.getString(1));
        file.setFileSize(cursor.getLong(2));
        file.setFileType(cursor.getInt(3));
        file.setMd5(cursor.getString(4));
        file.setParent_id(cursor.getInt(5));
        file.setRemote_id(cursor.getString(6));
        file.setRemote_parent_id(cursor.getString(7));
        file.setState(cursor.getInt(8));
        file.setChangedSize(cursor.getInt(9));
        file.setLocal_user_id(cursor.getInt(10));
        return file;
    }

    /**
     * Get not upload completed root folder from database
     *
     * @return List<UploadFile>: The UploadFile object list
     */
    /*
    public List<UploadFile> getNotUploadedRootFiles() {
        List<UploadFile> fileList = new ArrayList<UploadFile>();
        Cursor cursor = database.query(UploadFileEntry.TABLE_NAME,
                UploadFileEntry.COLUMN_ARRAY,
                UploadFileEntry.COLUMN_STATE + "!=? and "
                        + UploadFileEntry.COLUMN_LOCAL_USERID + "=? and "
                        + UploadFileEntry.COLUMN_LOCAL_PARENT_FOLDER_ID + " < 0 ",
                new String[]{String.valueOf(UploadFileState.UPLOADED),
                        String.valueOf(AppConfigs.current_local_user_id)}, null, null, null);
        while (cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }
    */

    /**
     * Get not upload completed files from database
     *
     * @return ArrayList<UploadFile>: The UploadFile object list
     */
    public ArrayList<UploadFile> getNotUploadedFiles() {
        ArrayList<UploadFile> fileList = new ArrayList<UploadFile>();
        Cursor cursor = database.query(UploadFileEntry.TABLE_NAME,
                UploadFileEntry.COLUMN_ARRAY,
                UploadFileEntry.COLUMN_STATE + " !=? and "
                        + UploadFileEntry.COLUMN_LOCAL_USERID + " =? and "
                        + UploadFileEntry.COLUMN_FILETYPE + " =? ",
                new String[]{String.valueOf(UploadFileState.UPLOADED),
                        String.valueOf(AppConfigs.current_local_user_id),
                        String.valueOf(CloudFileType.TYPE_FILE)}, null, null, null);
        while (cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }


    /**
     * Convert UploadFile object to ContentValues object
     *
     * @param file UploadFile object
     * @return ContentValues object
     */
    private ContentValues fileToContentValue(UploadFile file) {
        ContentValues cv = new ContentValues();
        cv.put(UploadFileEntry.COLUMN_FILEPATH, file.getFilePath());
        cv.put(UploadFileEntry.COLUMN_FILESIZE, file.getFileSize());
        cv.put(UploadFileEntry.COLUMN_FILETYPE, file.getFileType());
        cv.put(UploadFileEntry.COLUMN_MD5, file.getMd5());
        cv.put(UploadFileEntry.COLUMN_LOCAL_PARENT_FOLDER_ID, file.getParent_id());
        cv.put(UploadFileEntry.COLUMN_REMOTE_USER_ID, file.getRemote_id());
        cv.put(UploadFileEntry.COLUMN_REMOTE_PARENT_FOLDER_ID, file.getRemote_parent_id());
        cv.put(UploadFileEntry.COLUMN_STATE, file.getState());
        cv.put(UploadFileEntry.COLUMN_UPLOADED_SIZE, file.getChangedSize());
        cv.put(UploadFileEntry.COLUMN_LOCAL_USERID, file.getLocal_user_id());
        return cv;
    }

    public static UploadFile createUploadFile(File file, long l_parent_id, String r_parent_id) {
        UploadFile u_file = new UploadFile();
        // TODO Judge the table have the same path/local_user_id/state!=uploaded(If have, not need insert)
        if (file.exists()) {
            try {
                u_file.setFilePath(file.getAbsolutePath());
                if (file.isFile())
                    u_file.setMd5(FileManager.generateMD5(new FileInputStream(file)));
                else
                    u_file.setMd5("");
                u_file.setParent_id(l_parent_id);
                u_file.setRemote_parent_id(r_parent_id);
                u_file.setFileSize(file.length());
                u_file.setState(UploadFileState.NOT_UPLOAD);
                u_file.setChangedSize(0);
                u_file.setFileType(file.isFile() ? CloudFileType.TYPE_FILE : CloudFileType.TYPE_FOLDER);
                u_file.setLocal_user_id(AppConfigs.current_local_user_id);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return u_file;
    }
}
