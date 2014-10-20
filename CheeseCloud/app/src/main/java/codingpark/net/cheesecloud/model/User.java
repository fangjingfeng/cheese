package codingpark.net.cheesecloud.model;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by ethanshan on 14-10-16.
 * The user entity, fetch/store user profile from/to database.
 */
public class User {

    private String username     = "";

    private String password_md5 = "";

    private String ws_address   = "";


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
