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
 */
public class UserDataSource {
    private Context mContext            = null;
    private SQLiteDatabase database     = null;
    private SQLiteOpenHelper dbHelper   = null;

    public UserDataSource(Context context) {
        mContext = context;
        dbHelper = new LocalDatabase(mContext);
    }

    public void open() {
        if (database == null)
            database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * userinfo table
     * ---------------------------------------
     * |  _ID           |   INTEGER
     * --------------------------------------
     * |  password_md5  |   VARCHAR(50)
     * ---------------------------------------
     * |  username      |   VARCHAR(50)
     * ---------------------------------------
     * |  ws_address    |   VARCHAR(255)
     * ---------------------------------------
     *
     */
    public static class UserEntry implements BaseColumns {
        /**
         * The user table name
         */
        public static final String TABLE_NAME           = "userinfo";
        /**
         * Type: VARCHAR(50)
         * Description: The login password(Encrypt by MD5 Algorithm)
         * Default: ""(String.Empty);
         */
        public static final String COLUMN_PASSWORD_MD5  = "password_md5";
        /**
         * Type: VARCHAR(50)
         * Description: The login user name, current is the email address
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_USERNAME      = "username";
        /**
         * Type: VARCHAR(255)
         * Description: The web service URL
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_WS_ADDRESS    = "ws_address";
    }


    /**
     * Insert a record to userinfo table
     * @param user The user model
     * @return
     *  true: insert success
     *  false: insert failed
     */
    public boolean addUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(UserEntry.COLUMN_PASSWORD_MD5, user.getPassword_md5());
        cv.put(UserEntry.COLUMN_USERNAME, user.getUsername());
        cv.put(UserEntry.COLUMN_WS_ADDRESS, user.getWs_address());
        long id = database.insert(UserEntry.TABLE_NAME, null, cv);
        return id > 0;
    }

    /**
     * Remove a record from userinfo table
     * @param username The username of the userinfo record which will be deleted
     * @return
     *  true: delete success
     *  false: delete failed
     */
    public boolean deleteUser(String username) {
        int rows = database.delete(UserEntry.TABLE_NAME, UserEntry.COLUMN_USERNAME + "=?", new String[]{username} );
        return rows > 0;
    }

    /**
     * Update a record of userinfo table
     * @param user The User model
     * @return
     *  true: Update success
     *  false: Update failed
     */
    public boolean updateUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(UserEntry.COLUMN_PASSWORD_MD5, user.getPassword_md5());
        cv.put(UserEntry.COLUMN_USERNAME, user.getUsername());
        cv.put(UserEntry.COLUMN_WS_ADDRESS, user.getWs_address());
        int rows = database.update(UserEntry.TABLE_NAME, cv, UserEntry.COLUMN_USERNAME + "=?", new String[]{user.getUsername()});
        return rows > 0;
    }

    /**
     * Get the all records of userinfo table
     * @return
     *  List<User>: The User model list(Store the all user information)
     */
    public List<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<User>();
        Cursor cursor = database.query(UserEntry.TABLE_NAME,
                new String[]{UserEntry._ID,
                        UserEntry.COLUMN_PASSWORD_MD5,
                        UserEntry.COLUMN_USERNAME,
                        UserEntry.COLUMN_WS_ADDRESS},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            User u = new User();
            u.setId(cursor.getInt(0));
            u.setPassword_md5(cursor.getString(1));
            u.setUsername(cursor.getString(2));
            u.setWs_address(cursor.getString(3));
            userList.add(u);
        }
        return userList;
    }

    /**
     * Get the record of userinfo table by the username
     * @param username The target username
     * @return
     *  Null: the userinfo table not have record of the username3
     *  Not null: The User object contains the record information
     */
    public User getUserByUsername(String username) {
        User user = null;
        Cursor cursor = database.query(UserEntry.TABLE_NAME,
                new String[]{UserEntry._ID,
                        UserEntry.COLUMN_PASSWORD_MD5,
                        UserEntry.COLUMN_USERNAME,
                        UserEntry.COLUMN_WS_ADDRESS},
                UserEntry.COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        while(cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setPassword_md5(cursor.getString(1));
            user.setUsername(cursor.getString(2));
            user.setWs_address(cursor.getString(3));
        }
        return user;
    }

    private User cursorToUser(Cursor cursor) {
        return new User();
    }
}
