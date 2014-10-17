package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;

/**
 * Created by ethanshan on 14-10-16.
 */
public class WsSyncFile extends  WsFile implements KvmSerializable{
    protected int SyncType;
    protected long BlockIndex;
    protected boolean IsFinally;
    protected SyncFileBlock Blocks;

}
