package codingpark.net.cheesecloud.model;

import android.provider.BaseColumns;

/**
 * Created by ethanshan on 14-10-16.
 * The user entity, fetch/store user profile from/to database.
 */
public class User {

    private String username     = "";

    private String password_md5 = "";

    private String ws_address   = "";

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_md5() {
        return password_md5;
    }

    public void setPassword_md5(String password_md5) {
        this.password_md5 = password_md5;
    }

    public String getWs_address() {
        return ws_address;
    }

    public void setWs_address(String ws_address) {
        this.ws_address = ws_address;
    }
}
