package codingpark.net.cheesecloud.wsi;

import codingpark.net.cheesecloud.eumn.SexType;

/**
 * Created by ethanshan on 14-10-15.
 * The class store User profile
 */
public class WsUser {
    /**
     * The email that user register account use
     */
    private String email        = "";

    /**
     * The sex of user
     */
    private int sex             = SexType.Female;

    /**
     * User head picture file path
     */
    private String facePicture  = "";

    /**
     * The descriptions of the user
     */
    private String selfInfo     = "";

    /**
     * User login name
     */
    private String username     = "";

    /**
     * User login password
     */
    private String password_md5 = "";

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

    public String getCreateData() {
        return createData;
    }

    public void setCreateData(String createData) {
        this.createData = createData;
    }

    public String getSelfInfo() {
        return selfInfo;
    }

    public void setSelfInfo(String selfInfo) {
        this.selfInfo = selfInfo;
    }

    public String getFacePicture() {
        return facePicture;
    }

    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String createData   = "";

}
