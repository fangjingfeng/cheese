package codingpark.net.cheesecloud.model;

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
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

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


    public boolean addUser(User user) {

        return true;
    }

    public User deleteUser(String username) {
        return new User();
    }

    public boolean updateUser(User user) {

        return true;
    }

    public List<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<User>();
        //database.query(UserEntry.TABLE_NAME, );
        Cursor cursor = database.query(UserEntry.TABLE_NAME, null, null, null, null, null, null);
        return userList;
    }

    private User cursorToUser(Cursor cursor) {
        return new User();
    }
}
