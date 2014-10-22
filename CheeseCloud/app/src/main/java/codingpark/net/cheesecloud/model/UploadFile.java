package codingpark.net.cheesecloud.model;

import android.provider.BaseColumns;

/**
 * Created by ethanshan on 14-10-16.
 * The UploadFile entity, fetch/store uploading files information
 * from/to database.
 *
 */
public class UploadFile {
    private int local_uid           = -1;
    private int remote_uid          = -1;
    private String filepath         = "";
    private String md5              = "";
    private int parent_id           = -1;
    private String destpath         = "";
    private int filesize            = 0;
    private int state               = 0;
    private int uploadsize          = 0;
    private int filetype            = 0;

    public int getLocal_uid() {
        return local_uid;
    }

    public void setLocal_uid(int local_uid) {
        this.local_uid = local_uid;
    }

    public int getRemote_uid() {
        return remote_uid;
    }

    public void setRemote_uid(int remote_uid) {
        this.remote_uid = remote_uid;
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
