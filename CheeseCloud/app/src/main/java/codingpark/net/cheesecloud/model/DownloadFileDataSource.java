package codingpark.net.cheesecloud.model;

import android.provider.BaseColumns;

/**
 * The "download_files" table column description class
 * @author Ethan Shan
 * @version 1.0
 */
public class DownloadFileDataSource {

    public static final class DownloadFileEntry implements BaseColumns {

        /**
         * The table name used to store all to be downloaded file information
         */
        public static final String TABLE_NAME = "download_files";

        /**
         * The file already have downloaded size in byte unit.
         */
        public static final String COLUMN_DOWNLOADED_SIZE = "downloaded_size";
        /**
         * The to be downloaded file name.
         */
        public static final String COLUMN_FILENAME = "filename";
        /**
         * The file logic path in remote server.
         */
        public static final String COLUMN_FILEPATH = "filepath";
        /**
         * The file's whole size in byte unit which exist in remote server.
         */
        public static final String COLUMN_FILESIZE = "filesize";
        /**
         * The user id in local user table.
         */
        public static final String COLUMN_LOCAL_USER_ID = "local_user_id";
        /**
         * The download file md5, computed from  remote server
         */
        public static final String COLUMN_MD5 = "md5";
        /**
         * The file guid in remote server
         */
        public static final String COLUMN_REMOTE_ID = "remote_id";
        /**
         * The file's owner id on remote server
         */
        public static final String COLUMN_REMOTE_USER_ID = "remote_user_id";
        /**
         * The file download state.
         */
        public static final String COLUMN_STATE = "state";

        /**
         * The download_files table all columns set
         */
        public static final String[] COLUMN_ARRAY = new String[] {
                _ID,
                COLUMN_DOWNLOADED_SIZE,
                COLUMN_FILENAME,
                COLUMN_FILEPATH,
                COLUMN_FILESIZE,
                COLUMN_LOCAL_USER_ID,
                COLUMN_MD5,
                COLUMN_REMOTE_ID,
                COLUMN_REMOTE_USER_ID,
                COLUMN_STATE
        };
    }
}
