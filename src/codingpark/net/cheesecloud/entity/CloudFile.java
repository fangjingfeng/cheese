package codingpark.net.cheesecloud.entity;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The parent class of the DownloadFile and UploadFile
 * Field count: 12
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一�?-2014 18:25:47
 */
public class CloudFile implements Parcelable ,Serializable{

    /**
     * 处理过的数据大小
     */
    private long changedSize = 0;

	/**
     * 这个文件创建日期。
     * 格式:得到确认
     */
    private String createDate = "";
    /**
     * 本地文件系统的文件路径或远程逻辑磁盘
     */
    private String filePath = "";
    /**
     * 整个文件大小
     */
    private long fileSize = 0;
    /**
     * 文件类型
     * 0: File
     * 1: Folder
     */
    private int fileType = 0;
    /**
     * 当地表的id(索引)
     */
    private long id = 0;
    /**
     * 本地用户id相关联的文件
     */
    private long local_user_id = 0;
    /**
     * 整个文件的MD5值
     */
    private String md5 = "";
    /**
     * 当地表文件父id
     */
    private long parent_id = 0;
    /**
     * 上传/下载文件的日期记录插入到本地数据库。
     */
    private String recordCreateDate = "";
    /**
     * 远程服务器的guid
     */
    private String remote_id = "";
    /**
     * 文件的父的guid
     */
    private String remote_parent_id = "";
    /**
     * 文件的当前状态
     */
    private int state = 0;
    /**
     * 文件的物理id 
     */
    private String thumb_uri_name;
    

    public String getThumb_uri_name() {
		return thumb_uri_name;
	}

	public void setThumb_uri_name(String thumb_uri_name) {
		this.thumb_uri_name = thumb_uri_name;
	}

	public CloudFile(){

    }

    public void finalize() throws Throwable {

    }

    public long getChangedSize(){
        return changedSize;
    }

    /**
     * This file created date.
     * Format: To be confirmed
     */
    public String getCreateDate(){
        return createDate;
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
     * 当地表文件父id
     */
    public long getParent_id(){
        return parent_id;
    }

    public String getRecordCreateDate(){
        return recordCreateDate;
    }

    /**
     * The guid at remote server
     * 
     * 远程服务器的guid
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
     * This file created date.
     * Format: To be confirmed
     *
     * @param newVal
     */
    public void setCreateDate(String newVal){
        createDate = newVal;
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
     *
     * @param newVal
     */
    public void setRecordCreateDate(String newVal){
        recordCreateDate = newVal;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(changedSize);
        dest.writeString(createDate);
        dest.writeString(filePath);
        dest.writeLong(fileSize);
        dest.writeInt(fileType);
        dest.writeLong(id);
        dest.writeLong(local_user_id);
        dest.writeString(md5);
        dest.writeLong(parent_id);
        dest.writeString(recordCreateDate);
        dest.writeString(remote_id);
        dest.writeString(remote_parent_id);
        dest.writeInt(state);
    }

    public CloudFile(Parcel in) {
        changedSize = in.readLong();
        createDate = in.readString();
        filePath = in.readString();
        fileSize = in.readLong();
        fileType = in.readInt();
        id = in.readLong();
        local_user_id = in.readLong();
        md5 = in.readString();
        parent_id = in.readLong();
        recordCreateDate = in.readString();
        remote_id = in.readString();
        remote_parent_id = in.readString();
        state = in.readInt();
    }

    public static final Parcelable.Creator<CloudFile> CREATOR
            = new Parcelable.Creator<CloudFile>() {
        public CloudFile createFromParcel(Parcel in) {
            return new CloudFile(in);
        }

        public CloudFile[] newArray(int size) {
            return new CloudFile[size];
        }
    };

    @Override
	public String toString() {
		return "CloudFile [changedSize=" + changedSize + ", createDate="
				+ createDate + ", filePath=" + filePath + ", fileSize="
				+ fileSize + ", fileType=" + fileType + ", id=" + id
				+ ", local_user_id=" + local_user_id + ", md5=" + md5
				+ ", parent_id=" + parent_id + ", recordCreateDate="
				+ recordCreateDate + ", remote_id=" + remote_id
				+ ", remote_parent_id=" + remote_parent_id + ", state=" + state
				+ ", thumb_uri_name=" + thumb_uri_name + "]";
	}
    
    
}
