package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ethanshan on 14-10-15.
 * This class used to create localdatabase(Database) and userinfo/
 * upload_files/download_files(table). When application upgrade, the
 * class support database upgrade.
 */
public class LocalDatabase extends  SQLiteOpenHelper {

    /**
     * The database name, system will use the name create
     * database files. Such as localdatabase.db
     */
    private static final String DATABASE_NAME       = "localdatabase";

    /**
     * The database version. When application upgrade, may be need
     * modify the database table column. System will check if the version
     * is larger than current, if larger, will call {@link #onUpgrade} to
     * copy old data to new table(Developer implement the logic).
     */
    private static final int DATABASE_VERSION_1     = 1;


    /**
     * userinfo table
     */
    private static final String CREATE_USERINFO_TABLE_SQL       =
            "CREATE TABLE " + UserDataSource.UserEntry.TABLE_NAME + " (" +
                    UserDataSource.UserEntry._ID + " INTEGER PRIMARY KEY, " +
                    UserDataSource.UserEntry.COLUMN_GUID + " VARCHAR(50), " +
                    UserDataSource.UserEntry.COLUMN_PASSWORD_MD5 + " VARCHAR(50), " +
                    UserDataSource.UserEntry.COLUMN_USERNAME + " VARCHAR(50), " +
                    UserDataSource.UserEntry.COLUMN_WS_ADDRESS + " VARCHAR(255) " +
                    " )";

    /**
     * upload_files table
     */
    private static final String CREATE_UPLOAD_FILES_TABLE_SQL   =
            "CREATE TABLE " + UploadFileDataSource.UploadFileEntry.TABLE_NAME + " (" +
                    UploadFileDataSource.UploadFileEntry._ID + " INTEGER PRIMARY KEY, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_REMOTE_ID + " VARCHAR(50), " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_FILEPATH + " TEXT, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_MD5 + " VARCHAR(50), " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_LOCAL_PARENT_FOLDER_ID + " INTEGER, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_REMOTE_PARENT_FOLDER_ID + " VARCHAR(50), " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_FILESIZE + " INTEGER, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_STATE + " INTEGER, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_UPLOADED_SIZE + " INTEGER, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_FILETYPE + " INTEGER, " +
                    UploadFileDataSource.UploadFileEntry.COLUMN_LOCAL_USERID + " INTEGER" +
                    " )";

    /**
     * download_files_table
     */

    public LocalDatabase(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION_1);
    }

    /*
    public LocalDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    */


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Create userinfo table
        db.execSQL(CREATE_USERINFO_TABLE_SQL);
        // 2. Create upload_files table
        db.execSQL(CREATE_UPLOAD_FILES_TABLE_SQL);
        // 3. Create download_files table
        // TODO Create download_files table
        //db.execSQL(CREATE_DOWNLOAD_FILES_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO According to current database version, implement database upgrade logic
    }

}
