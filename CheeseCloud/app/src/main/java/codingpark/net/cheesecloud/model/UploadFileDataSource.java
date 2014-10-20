package codingpark.net.cheesecloud.model;

import android.provider.BaseColumns;

/**
 * Created by ethanshan on 14-10-20.
 */
public class UploadFileDataSource {
    public static final class UploadFileEntry implements BaseColumns {
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
