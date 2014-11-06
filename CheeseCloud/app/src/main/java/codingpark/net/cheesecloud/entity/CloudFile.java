package codingpark.net.cheesecloud.entity;


/**
 * The parent class of the DownloadFile and UploadFile
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 15:45:01
 */
public class CloudFile {

    /**
     * The processed data size
     */
    private long changedSize = 0;
    /**
     * The file path at local file system or remote logic disk
     */
    private String filePath = "";
    /**
     * The file whole size
     */
    private long fileSize = 0;
    /**
     * The file type
     * 0: File
     * 1: Folder
     */
    private int fileType = 0;
    /**
     * The id(Index) at local table
     */
    private long id = 0;
    /**
     * The file associated local user id
     */
    private long local_user_id = 0;
    /**
     * The whole file MD5 value
     */
    private String md5 = "";
    /**
     * The file parent id on local table
     */
    private long parent_id = 0;
    /**
     * The guid at remote server
     */
    private String remote_id = "";
    /**
     * The guid of the file's parent
     */
    private String remote_parent_id = "";
    /**
     * The file current state
     */
    private int state = 0;

    public CloudFile(){

    }

    public void finalize() throws Throwable {

    }

    public long getChangedSize(){
        return changedSize;
    }

    public String getFilePath(){
        return filePath;
    }

    public long getFileSize(){
        return fileSize;
    }

    public int getFileType(){
        return fileType;
    }

    /**
     * The id(Index) at local table
     */
    public long getId(){
        return id;
    }

    /**
     * The file associated local user id
     */
    public long getLocal_user_id(){
        return local_user_id;
    }

    public String getMd5(){
        return md5;
    }

    /**
     * The file parent id on local table
     */
    public long getParent_id(){
        return parent_id;
    }

    /**
     * The guid at remote server
     */
    public String getRemote_id(){
        return remote_id;
    }

    public String getRemote_parent_id(){
        return remote_parent_id;
    }

    public int getState(){
        return state;
    }

    /**
     *
     * @param newVal
     */
    public void setChangedSize(long newVal){
        changedSize = newVal;
    }

    /**
     *
     * @param newVal
     */
    public void setFilePath(String newVal){
        filePath = newVal;
    }

    /**
     *
     * @param newVal
     */
    public void setFileSize(long newVal){
        fileSize = newVal;
    }

    /**
     *
     * @param newVal
     */
    public void setFileType(int newVal){
        fileType = newVal;
    }

    /**
     * The id(Index) at local table
     *
     * @param newVal
     */
    public void setId(long newVal){
        id = newVal;
    }

    /**
     * The file associated local user id
     *
     * @param newVal
     */
    public void setLocal_user_id(long newVal){
        local_user_id = newVal;
    }

    /**
     *
     * @param newVal
     */
    public void setMd5(String newVal){
        md5 = newVal;
    }

    /**
     * The file parent id on local table
     *
     * @param newVal
     */
    public void setParent_id(long newVal){
        parent_id = newVal;
    }

    /**
     * The guid at remote server
     *
     * @param newVal
     */
    public void setRemote_id(String newVal){
        remote_id = newVal;
    }

    /**
     *
     * @param newVal
     */
    public void setRemote_parent_id(String newVal){
        remote_parent_id = newVal;
    }

    /**
     *
     * @param newVal
     */
    public void setState(int newVal){
        state = newVal;
    }

}
