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

    public static final class UploadFileEntry implements BaseColumns{
        /**
         * The table name which to store the user selected files information.
         */
        public static final String TABLE_NAME   = "upload_files";
        /**
         * Type: TEXT
         * Description: The remote folder(destination) path
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_DESTPATH     = "destpath";
        /**
         * Type: TEXT
         * Description: The file absolutely path in local file system
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_FILEPATH     = "filepath";
        /**
         * Type: INTEGER
         * Description: The whole file size in Byte unit
         * Default: 0
         */
        public static final String COLUMN_FILESIZE     = "filesize";
        /**
         * Type: INTEGER
         * Description: The file type(not extension type)
         * 0: file
         * 1: folder
         * Default: 0
         */
        public static final String COLUMN_FILETYPE     = "filetype";
        /**
         * Type: INTEGER
         * Description: The file parent folder id. Associate with {@link #_ID}
         * -1: The selected root
         * >=0:
         * Default: -1
         */
        public static final String COLUMN_PARENT_ID    = "parent_id";
        /**
         * Type: INTEGER
         * Description: The file current upload state
         * 0: not uploaded
         * 1: uploading
         * 2: uploaded
         * Default: 0
         */
        public static final String COLUMN_STATE        = "state";
        /**
         * Type: VARCHAR(50)
         * Description: The whole file MD5 value
         * Default: ""(String.Empty)
         */
        public static final String COLUMN_MD5          = "md5";
        /**
         * Type: INTEGER
         * Description: The user who insert this item. Associate with user table's _ID
         * Default: -1
         */
        public static final String COLUMN_USERID       = "user_id";
        /**
         * Type: INTEGER
         * Description: The file current uploaded size(offset from head)
         * Default: 0
         */
        public static final String COLUMN_UPLOADED_SIZE    = "uploaded_size";
    }
}
