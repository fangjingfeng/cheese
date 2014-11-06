package codingpark.net.cheesecloud.model;


/**
 * The user entity, fetch/store user profile from/to database.
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 15:22:01
 */
public class User {

    /**
     * The user email(login username)
     */
    public String email = "";
    /**
     * Remote database user id
     */
    public String guid = "";
    /**
     * Local database user id(index)
     */
    public int id = 0;
    /**
     * The user password
     */
    public String password_md5 = "";
    /**
     * The web service address
     */
    public String ws_address = "";

    public User(){

    }

    /**
     *
     * @exception Throwable
     */
    public void finalize()
            throws Throwable{

    }

    /**
     * The user email(login username)
     */
    public String getEmail(){
        return email;
    }

    /**
     * Remote database user id
     */
    public String getGuid(){
        return guid;
    }

    /**
     * Local database user id(index)
     */
    public int getId(){
        return id;
    }

    /**
     * The user password
     */
    public String getPassword_md5(){
        return password_md5;
    }

    /**
     * The web service address
     */
    public String getWs_address(){
        return ws_address;
    }

    /**
     * The user email(login username)
     *
     * @param newVal
     */
    public void setEmail(String newVal){
        email = newVal;
    }

    /**
     * Remote database user id
     *
     * @param newVal
     */
    public void setGuid(String newVal){
        guid = newVal;
    }

    /**
     * Local database user id(index)
     *
     * @param newVal
     */
    public void setId(int newVal){
        id = newVal;
    }

    /**
     * The user password
     *
     * @param newVal    newVal
     */
    public void setPassword_md5(String newVal){
        password_md5 = newVal;
    }

    /**
     * The web service address
     *
     * @param newVal
     */
    public void setWs_address(String newVal){
        ws_address = newVal;
    }

}