package codingpark.net.cheesecloud.model;

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
