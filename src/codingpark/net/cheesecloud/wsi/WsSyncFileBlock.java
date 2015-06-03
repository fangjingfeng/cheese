package codingpark.net.cheesecloud.wsi;

/**
 * Created by ethanshan on 14-10-16.
 */
public class WsSyncFileBlock {
    /**
     * The block index of the whole file
     */
    private int sourceIndex             = 0;
    /**
     * The block size in byte unit
     */
    private int sourceSize              = 0;

    /**
     * The block md5 value
     */
    private String sourceMd5            = "";

    /**
     * The block alder32 value
     */
    private String sourceAlder32        = "";

    /**
     * The block index of file for sync
     */
    private int updateIndex             = 0;

    /**
     * The block data in byte array
     */
    private byte[] updateData           = null;

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public int getSourceSize() {
        return sourceSize;
    }

    public void setSourceSize(int sourceSize) {
        this.sourceSize = sourceSize;
    }

    public String getSourceMd5() {
        return sourceMd5;
    }

    public void setSourceMd5(String sourceMd5) {
        this.sourceMd5 = sourceMd5;
    }

    public String getSourceAlder32() {
        return sourceAlder32;
    }

    public void setSourceAlder32(String sourceAlder32) {
        this.sourceAlder32 = sourceAlder32;
    }

    public int getUpdateIndex() {
        return updateIndex;
    }

    public void setUpdateIndex(int updateIndex) {
        this.updateIndex = updateIndex;
    }

    public byte[] getUpdateData() {
        return updateData;
    }

    public void setUpdateData(byte[] updateData) {
        this.updateData = updateData;
    }
}
