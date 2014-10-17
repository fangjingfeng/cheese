package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import codingpark.net.cheesecloud.model.UploadFile;
import codingpark.net.cheesecloud.model.User;

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
            "CREATE TABLE " + User.UserEntry.TABLE_NAME + " (" +
                    User.UserEntry._ID + " INTEGER PRIMARY KEY, " +
                    User.UserEntry.COLUMN_PASSWORD_MD5 + " VARCHAR(50), " +
                    User.UserEntry.COLUMN_USERNAME + " VARCHAR(50), " +
                    User.UserEntry.COLUMN_WS_ADDRESS + " VARCHAR(255) " +
                    " )";

    /**
     * upload_files table
     */
    private static final String CREATE_UPLOAD_FILES_TABLE_SQL   =
            "CREATE TABLE " + UploadFile.UploadFileEntry.TABLE_NAME + " (" +
                    UploadFile.UploadFileEntry._ID + " INTEGER PRIMARY KEY, " +
                    UploadFile.UploadFileEntry.COLUMN_DESTPATH + " TEXT, " +
                    UploadFile.UploadFileEntry.COLUMN_FILEPATH + " TEXT, " +
                    UploadFile.UploadFileEntry.COLUMN_FILESIZE + " INTEGER, " +
                    UploadFile.UploadFileEntry.COLUMN_FILETYPE + " INTEGER, " +
                    UploadFile.UploadFileEntry.COLUMN_MD5 + " VARCHAR(50), " +
                    UploadFile.UploadFileEntry.COLUMN_PARENT_ID + " INTEGER, " +
                    UploadFile.UploadFileEntry.COLUMN_STATE + " INTEGER, " +
                    UploadFile.UploadFileEntry.COLUMN_UPLOADED_SIZE + " INTEGER, " +
                    UploadFile.UploadFileEntry.COLUMN_USERID + " INTEGER" +
                    " )";

    /**
     * download_files_table
     */
    public static final String DOWNLOAD_FILES_TABLE_NAME        = "download_files";
    private static final String CREATE_DOWNLOAD_FILES_TABLE_SQL = "CREATE TABLE " + DOWNLOAD_FILES_TABLE_NAME
            + "";

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
