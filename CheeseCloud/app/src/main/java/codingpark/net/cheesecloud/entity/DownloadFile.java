package codingpark.net.cheesecloud.entity;


/**
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 15:25:40
 */
public class DownloadFile extends CloudFile {

    public DownloadFile(){

    }

    public DownloadFile(CloudFile file) {
        this.setChangedSize(file.getChangedSize());
        this.setCreateDate(file.getCreateDate());
        this.setFilePath(file.getFilePath());
        this.setFileSize(file.getFileSize());
        this.setFileType(file.getFileType());
        this.setId(file.getId());
        this.setLocal_user_id(file.getLocal_user_id());
        this.setMd5(file.getMd5());
        this.setParent_id(file.getParent_id());
        this.setRecordCreateDate(file.getRecordCreateDate());
        this.setRemote_id(file.getRemote_id());
        this.setRemote_parent_id(file.getRemote_parent_id());
        this.setState(file.getState());
    }

    public void finalize() throws Throwable {
        super.finalize();
    }

}
