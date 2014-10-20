package codingpark.net.cheesecloud.model;

import android.provider.BaseColumns;

/**
 * Created by ethanshan on 14-10-16.
 * The UploadFile entity, fetch/store uploading files information
 * from/to database.
 *
 */
public class UploadFile {
    private int user_id             = -1;
    private String filepath         = "";
    private String md5              = "";
    private int parent_id           = -1;
    private String destpath         = "";
    private int filesize            = 0;
    private int state               = 0;
    private int uploadsize          = 0;
    private int filetype            = 0;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getDestpath() {
        return destpath;
    }

    public void setDestpath(String destpath) {
        this.destpath = destpath;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getUploadsize() {
        return uploadsize;
    }

    public void setUploadsize(int uploadsize) {
        this.uploadsize = uploadsize;
    }

    public int getFiletype() {
        return filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }

}
