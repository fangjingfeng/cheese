package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.handle.LocalDatabase;

/**
 * Created by ethanshan on 14-10-20.
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

    public boolean addUploadFile(UploadFile file) {
        return true;
    }

    public boolean deleteUploadFile() {
        return true;
    }

    public boolean updateUploadFile(UploadFile file) {
        return true;
    }

    public List<UploadFile> getAllUploadFile() {
        return new ArrayList<UploadFile>();
    }
}
