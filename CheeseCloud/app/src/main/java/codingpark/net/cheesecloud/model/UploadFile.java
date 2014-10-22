package codingpark.net.cheesecloud.model;

import android.provider.BaseColumns;

/**
 * Created by ethanshan on 14-10-16.
 * The UploadFile entity, fetch/store uploading files information
 * from/to database.
 *
 */
public class UploadFile {
    /**
     * The id(Index) at local table uploadfile
     */
    private int id                  = -1;
    /**
     * The guid at remote server
     */
    private String remote_id        = "";
    /**
     * The absolute file path(Local file system)
     */
    private String filepath         = "";
    /**
     * The file's md5 value(Whole)
     */
    private String md5              = "";
    /**
     * The parent folder id at local
     */
    private int parent_id           = -1;
    /**
     * The parent folder id(GUID) at remote server
     */
    private String remote_parent_id = "";
    /**
     * The whole file size in byte unit
     */
    private long filesize            = 0;
    /**
     * The file upload state
     */
    private int state               = 0;
    /**
     * The uploaded size
     */
    private long uploadsize          = 0;
    /**
     * The file type(file or folder)
     */
    private int filetype            = 0;
    /**
     * The user id who insert the this item to table
     */
    private int local_user_id       = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(String remote_id) {
        this.remote_id = remote_id;
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

    public String getRemote_parent_id() {
        return remote_parent_id;
    }

    public void setRemote_parent_id(String remote_parent_id) {
        this.remote_parent_id = remote_parent_id;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getUploadsize() {
        return uploadsize;
    }

    public void setUploadsize(long uploadsize) {
        this.uploadsize = uploadsize;
    }

    public int getFiletype() {
        return filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }

    public int getLocal_user_id() {
        return local_user_id;
    }

    public void setLocal_user_id(int local_user_id) {
        this.local_user_id = local_user_id;
    }
}
