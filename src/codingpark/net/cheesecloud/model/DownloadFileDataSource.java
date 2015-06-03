package codingpark.net.cheesecloud.model;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.enumr.DownloadFileState;

/**
 * The "download_files" table column description class
 * 
 * download_files”表列描述类
 * @author Ethan Shan
 * @version 1.0
 */
public class DownloadFileDataSource {

    private Context mContext = null;
    private SQLiteDatabase database = null;
    private SQLiteOpenHelper dbHelper = null;

    public static final class DownloadFileEntry implements BaseColumns {

        /**
         * 用于存储所有的表名下载文件的信息
         */
        public static final String TABLE_NAME = "download_files";

        /**
         * 已经下载的文件大小(以字节单元。
         */
        public static final String COLUMN_DOWNLOADED_SIZE = "downloaded_size";
        /**
         * 下载文件的名字。
         */
        public static final String COLUMN_FILENAME = "filename";
        /**
         * 远程服务器中的文件的逻辑路径。
         */
        public static final String COLUMN_FILEPATH = "filepath";
        /**
         * 整个文件的大小(以字节单位存在远程服务器。
         */
        public static final String COLUMN_FILESIZE = "filesize";
        /**
         * 本地用户表中的用户id。
         */
        public static final String COLUMN_LOCAL_USER_ID = "local_user_id";
        /**
         * 从远程服务器下载文件的md5、计算
         */
        public static final String COLUMN_MD5 = "md5";
        /**
         * 在远程服务器文件guid
         */
        public static final String COLUMN_REMOTE_ID = "remote_id";
        /**
         * 远程服务器上文件的所有者id
         */
        public static final String COLUMN_REMOTE_USER_ID = "remote_user_id";
        /**
         * 文件下载状态。
         */
        public static final String COLUMN_STATE = "state";

        /**
         * download_files表的所有列集
         */
        public static final String[] COLUMN_ARRAY = new String[] {
                _ID,
                COLUMN_DOWNLOADED_SIZE,
                COLUMN_FILEPATH,
                COLUMN_FILESIZE,
                COLUMN_LOCAL_USER_ID,
                COLUMN_MD5,
                COLUMN_REMOTE_ID,
                COLUMN_REMOTE_USER_ID,
                COLUMN_STATE
        };
        
    }

    /**
     * The constructor
     *
     * @param context    The application context
     */
    public DownloadFileDataSource(Context context){
        mContext = context;
        dbHelper = new LocalDatabase(mContext);
    }

    /**
     * Add DownloadFile record to download_files table.
     *
     * @param file    The DownloadFile object, stored to be downloaded file's
     * information
     */
    public boolean addDownloadFile(DownloadFile file){
        ContentValues cv = fileToContentValue(file);
        cv.put(DownloadFileEntry.COLUMN_LOCAL_USER_ID, AppConfigs.current_local_user_id);
        long l_id = database.insert(DownloadFileEntry.TABLE_NAME, null, cv);
        return l_id >= 0;
    }

    /**
     * As the object owner manipulate download_files table finish, call this function
     * to free related system resources
     */
    public void close(){
        if (database != null)
            database.close();
        if (dbHelper != null)
            dbHelper.close();
    }

    /**
     * 删除记录通过给定的参数文件
     *
     * @param file    The DownloadFile object which stored the record information to
     * be deleted
     * @return If deleted rows > 0, return true. else return false;
     */
    public boolean deleteDownloadFile(DownloadFile file){
        int rows = database.delete(DownloadFileEntry.TABLE_NAME, DownloadFileEntry._ID + " =? and " +
                        DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? ",
                new String[] {String.valueOf(file.getId()), String.valueOf(AppConfigs.current_local_user_id)});
        return rows > 0;
    }


    /**
     * 删除记录由给定的成批的下载状态
     * @param state The target state
     * @return If affected rows > 0, return true. Else return false
     */
    public boolean deleteDownloadFileByState(int state) {
        int rows = database.delete(DownloadFileEntry.TABLE_NAME, DownloadFileEntry.COLUMN_STATE + " =? and " +
                DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? ",
                new String[] {String.valueOf(state), String.valueOf(AppConfigs.current_local_user_id)});
        return rows > 0;
    }

    /**
     * 查询所有记录
     */
    public ArrayList<DownloadFile> getAllDownloadFile(){
        ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
        Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
                DownloadFileEntry.COLUMN_ARRAY,
                DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? ", new String[] {String.valueOf(AppConfigs.current_local_user_id)},
                null, null, DownloadFileEntry._ID);
        while(cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }
    
    //database.query("download_files" ,    , 'local_user_id' =? and 'state' , '!=?',
    /**
     * 查询数据库中是否已经下载的文件
     * @param file_name
     * @return
     */
    public boolean sqleDownLoadFile(String file_name ,String md5){
    	 //ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
    	 Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME, DownloadFileEntry.COLUMN_ARRAY, DownloadFileEntry.COLUMN_REMOTE_ID + "= ? and "+DownloadFileEntry.COLUMN_MD5 + " =? ", new String[] {file_name,md5}, null, null, DownloadFileEntry._ID);
    	 while(cursor.moveToNext()) {
    		System.out.println("---"+cursor.getString(5)+":"+cursor.getString(6)+":MD5-->"+md5);
    		if("3".equals(cursor.getString(8))&&md5.equals(cursor.getString(5))){
    			return true;
    		}else{
    			return false;
    		}
         }
		return false;
    	
    }
    /**
     * 下载成功
     * 
     */
    public Boolean updateSuccessfulDownloadFile(DownloadFile downFile){
    	  int rows = database.update(DownloadFileEntry.TABLE_NAME, fileToContentValue(downFile),
                  DownloadFileEntry._ID + " =? ",new String[] {String.valueOf(downFile.getId())});
          return rows > 0;
    }
    /**
     * 删除下载失败的文件
     * 
     */
    public Boolean  deleteDownFile(DownloadFile downFile){
		int rows = database.delete(DownloadFileEntry.TABLE_NAME,
				DownloadFileEntry.COLUMN_REMOTE_ID+ "= ?" ,new String[]{downFile.getRemote_id()}
				
		);
    	return rows>0;
    }
    
    /**
     * 查询所有不属于当前登录用户下载完成的记录
     */
    public ArrayList<DownloadFile> getNotDownloadedFile(){
        ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
        Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
                DownloadFileEntry.COLUMN_ARRAY,DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? and " + DownloadFileEntry.COLUMN_STATE + " !=? ",
                new String[] {String.valueOf(AppConfigs.current_local_user_id), String.valueOf(DownloadFileState.DOWNLOADING)},null, null, DownloadFileEntry._ID);
        while(cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }
    /**
     * Query all record by the target state
     *
     * @param state    The target state
     */
    public ArrayList<DownloadFile> getAllDownloadFileByState(int state){
        ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
        Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
                DownloadFileEntry.COLUMN_ARRAY,
                DownloadFileEntry.COLUMN_STATE + " =? and " + DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =?" ,
                new String[] {String.valueOf(state), String.valueOf(AppConfigs.current_local_user_id)}, null, null, DownloadFileEntry._ID);
        while(cursor.moveToNext()) {
            fileList.add(cursorToFile(cursor));
        }
        return fileList;
    }
    //插入要下载的文件到数据库中
    public void addDownLoadFile(){}
    
    /**
     * 查找要下载的文件
     * DownloadFileEntry.COLUMN_LOCAL_USER_ID + " =? and " +
     */
    public ArrayList<DownloadFile> getDownloadedFile(){
    	 ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
    	 Cursor cursor = database.query(DownloadFileEntry.TABLE_NAME,
    			 DownloadFileEntry.COLUMN_ARRAY,DownloadFileEntry.COLUMN_STATE + " !=? ",
                 new String[] {String.valueOf(DownloadFileState.DOWNLOADED)},null, null,DownloadFileEntry._ID);
         while(cursor.moveToNext()) {
             fileList.add(cursorToFile(cursor));
         }
         System.out.println("fileList-->"+fileList.size());
    	return fileList;
    }
    
    /**
     * Before manipulate download_files table, need call this function to prepare a
     * writable database object.
     */
    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Update the record by the given parameter file.
     * 更新记录通过给定的参数文件。 
     * @param file    The DownloadFile object stored the record information to be
     * deleted
     * @return If affected record number > 0, return true, else return false;
     */
    public boolean updateDownloadFile(DownloadFile file){
        int rows = database.update(DownloadFileEntry.TABLE_NAME, fileToContentValue(file),
                DownloadFileEntry.COLUMN_REMOTE_ID + " =? ",new String[] {String.valueOf(file.getRemote_id())});
        return rows > 0;
    }

    /**
     * Convert DownloadFile object to ContentValues object
     * @param file DownloadFile object
     * @return ContentValues object
     */
    private ContentValues fileToContentValue(DownloadFile file) {
        ContentValues cv = new ContentValues();
        cv.put(DownloadFileEntry.COLUMN_DOWNLOADED_SIZE, file.getChangedSize());
        cv.put(DownloadFileEntry.COLUMN_FILENAME, file.getFilePath());
        cv.put(DownloadFileEntry.COLUMN_FILEPATH, file.getFilePath());
        cv.put(DownloadFileEntry.COLUMN_FILESIZE, file.getFileSize());
        cv.put(DownloadFileEntry.COLUMN_LOCAL_USER_ID, file.getLocal_user_id());
        cv.put(DownloadFileEntry.COLUMN_MD5, file.getMd5());
        cv.put(DownloadFileEntry.COLUMN_REMOTE_ID, file.getRemote_id());
        // Current local table not need remote user id
        cv.put(DownloadFileEntry.COLUMN_REMOTE_USER_ID,file.getThumb_uri_name());
        cv.put(DownloadFileEntry.COLUMN_STATE, file.getState());
        return cv;
    }

    /**
     * Convert CloudFile object to DownloadFile object
     * Current DownloadFile property completely equals CloudFile,
     * just need force type cast
     * @param file The CloudFile object
     * @return The DownloadFile object
     */
    public static DownloadFile convertToDownloadFile(CloudFile file) {
        DownloadFile d_file = new DownloadFile(file);
        return d_file;
    }


    /**
     * Convert Cursor to DownloadFile object
     * 将指针转换成DownloadFile对象
     * @param cursor The cursor fetch from database
     * @return DownloadFile: The DownloadFile object
     */
    private DownloadFile cursorToFile(Cursor cursor) {
        DownloadFile file = new DownloadFile();
        file.setId(cursor.getLong(0));
        file.setChangedSize(cursor.getLong(1));
        // No fileName 2
        file.setFilePath(cursor.getString(2));
        file.setFileSize(cursor.getLong(3));
        file.setLocal_user_id(cursor.getLong(4));
        file.setMd5(cursor.getString(5));
        file.setRemote_id(cursor.getString(6));
        // No remote user id 8
        file.setThumb_uri_name(cursor.getString(7));
        file.setState(cursor.getInt(8));
        return file;
    }
}
