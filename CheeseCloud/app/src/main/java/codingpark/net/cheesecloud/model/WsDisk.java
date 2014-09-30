package codingpark.net.cheesecloud.model;

import java.util.List;

/**
 * WebService disk entity class
 */
public class WsDisk {
    /// <summary>
    /// 获取一个值，该值指示对象是否被删除
    /// </summary>
    private boolean _isDeleted;

    /// <summary>
    /// 获取对象的GUID
    /// </summary>
    private String _guid;

    /// <summary>
    /// 总容量大小
    /// </summary>
    private long _allSpaceSizeKB = 0;

    /// <summary>
    /// 已经使用空间大小
    /// </summary>
    private long _usedSpaceSizeKB = 0;

    /// <summary>
    /// 获取或设置该逻辑磁盘所属的存储池
    /// </summary>
    public WsGuidOwner StorePool;

    /// <summary>
    /// 获取或设置该逻辑磁盘的类型
    /// </summary>
    //public LogicDiskType Type;

    /// <summary>
    /// 获取或设置该实例的创建日期时间
    /// </summary>
    public String CreatDate;

    /// <summary>
    /// 获取一个值，该值指示对象是否被删除
    /// </summary>
    public boolean IsDeleted;

    /// <summary>
    /// 获取对象的GUID
    /// </summary>
    public String Guid;

    /// <summary>
    /// 名称(备注)
    /// </summary>
    public String Name;

    /// <summary>
    /// 获取该实例的总存储空间容量（KB）
    /// </summary>
    public long AllSpaceSizeKB;

    /// <summary>
    /// 获取实例的已使用的存储空间容量（KB）
    /// </summary>
    public long UsedSpaceSizeKB;

    /// <summary>
    /// 获取该实例的剩余存储空间容量（KB）
    /// </summary>
    public long UnusedSpaceSizeKB;


    /// <summary>
    /// 获取该对象的权限集合
    /// 规约：该属性默认不需要填充。当首次访问时自动通过指定的权限填充类进行填充
    /// </summary>
    public List<WsPermission> Permissions;

    /// <summary>
    /// 功能：删除对象。
    /// </summary>
    public void Delete()
    {
        _isDeleted = true;
    }
    /// <summary>
    /// 反删除对象。
    /// </summary>
    public void UnDelete()
    {
        _isDeleted = false;
    }

    /// <summary>
    /// 设置对象的Guid
    /// </summary>
    /// <param name="guid">要设置的Guid</param>
    public void SetGuid(String guid)
    {
        _guid = guid;
    }
    /// <summary>
    /// 设置对象的Guid为全0值
    /// </summary>
    public void SetGuid()
    {
        _guid = "";
        //_guid = Guid.Empty;
    }

    /// <summary>
    /// 占用该实例的可用空间，并同时更新该实例的空间占用相关属性值。
    /// 返回值：如果占用成功（可用空间足够），则返回True，否则返回False（可用空间不足）
    /// </summary>
    /// <param name="sizeKB">要占用的空间大小</param>
    /// <returns></returns>
    public boolean UseSpaceSize(long sizeKB)
    {
        if (UnusedSpaceSizeKB >= sizeKB)
        {
            _usedSpaceSizeKB += sizeKB;

            return true;
        }
        else
        {
            return false;
        }
    }

    /// <summary>
    /// 释放该实例的已用空间，并同时更新该实例的空间占用相关属性值。
    /// 返回值：如果释放成功（已用空间足够），则返回True，否则返回False（已用空间不足）
    /// </summary>
    /// <param name="sizeKB">要释放的空间大小</param>
    /// <returns></returns>
    public boolean RelSpaceSize(long sizeKB)
    {
        if (_usedSpaceSizeKB >= sizeKB)
        {
            _usedSpaceSizeKB -= sizeKB;

            return true;
        }
        else
        {
            return false;
        }
    }

    /// <summary>
    /// 设置总空间
    /// </summary>
    /// <param name="sizeKB"></param>
    public void SetAllSpaceSizeKB(long sizeKB)
    {
        _allSpaceSizeKB = sizeKB;
    }

    /// <summary>
    /// 设置已用空间
    /// </summary>
    /// <param name="sizeKB"></param>
    public void SetUsedSspaceSizeKB(long sizeKB)
    {
        _usedSpaceSizeKB = sizeKB;
    }

}
