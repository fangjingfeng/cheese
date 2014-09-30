package codingpark.net.cheesecloud.model;

public class WsSyncFile {
    /// <summary>
/// 获取该同步信息框架的同步根文件夹
/// </summary>
    //public ItemSyncType SyncType;

    /// <summary>
/// 获取或设置一个数值，当同步文件尺寸过大而被分割成多个部分时，该数值表示该部分的索引
/// </summary>
    public int BlockIndex;

    /// <summary>
/// 获取或设置一个值，当同步文件尺寸过大而被分割成多个部分时，该值表示该部分是否是最末尾的部分
/// </summary>
    public boolean IsFinally;

    /// <summary>
/// 获取组成文件的数据块
/// </summary>
    //public SyncFileBlock[] Blocks;
}
