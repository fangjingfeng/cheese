package codingpark.net.cheesecloud.model;

/**
 * Created by ethanshan on 14-10-16.
 * The user entity, fetch/store user profile from/to database.
 */
public class User {

    /**
     * Local database user id(index)
     */
    private int id              = -1;

    /**
     * Remote database user id
     */
    private String guid         = "";

    /**
     * The user email(login username)
     */
    private String email = "";

    /**
     * The user password
     */
    private String password_md5 = "";

    /**
     * The web service address
     */
    private String ws_address   = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
