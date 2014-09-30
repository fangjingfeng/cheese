package codingpark.net.cheesecloud.model;

import java.util.List;

public class WsFolder
{

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
    private long _allSpaceSizeKB;

    /// <summary>
    /// 已经使用空间大小
    /// </summary>
    private long _usedSpaceSizeKB;

    /// <summary>
    /// 获取一个值，该值指示对象是否被回收
    /// </summary>
    private boolean _isRecycled;

    /// <summary>
    /// 权限集合
    /// </summary>
    private List<WsPermission> _permissions;
    //gendregion

    //gregion 属性

    /// <summary>
    /// 获取或设置该逻辑文件夹的父逻辑文件夹。如果该逻辑文件夹为根，则该属性为null
    /// </summary>
    public WsGuidOwner Father;

    /// <summary>
    /// 获取或设置第三方应用的Guid
    /// </summary>
    public String ApplicationGuid;

    /// <summary>
    /// 获取或设置该逻辑文件夹所属的逻辑磁盘
    /// </summary>
    public WsGuidOwner LogicDisk;

    /// <summary>
    /// 获取或设置该实例所对应的磁盘空间限额。如果该实例不需要限额，则设置为null
    /// </summary>
    public WsSpaceSizer SpaceSize;

    /// <summary>
    /// 获取或设置 ，该值指示本实例是否显示在逻辑文件夹列表中
    /// </summary>
    public boolean IsShow;

    /// <summary>
    /// 获取或设置该实例的创建日期时间
    /// </summary>
    public String CreatDate;

    /// <summary>
    /// 获取或设置该逻辑文件夹的创建者（用户）的相关信息
    /// </summary>
    public WsGuidOwner Creater;

    /// <summary>
    /// 文件个数
    /// </summary>
    public int FileCount;

    /// <summary>
    /// 文件夹个数
    /// </summary>
    public int FolderCount;

    /// <summary>
    /// 获取一个值，该值指示对象是否被删除
    /// </summary>
    public boolean IsDeleted;

    /// <summary>
    /// 获取对象的GUID
    /// </summary>
    public String Guid;

    /// <summary>
    /// 获取或设置名称(备注)
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
    /// 获取或设置对象被删除的日期时间
    /// </summary>
    public String RecycleDate;

    /// <summary>
    /// 获取或设置该对象的删除者（用户）的相关信息
    /// </summary>
    public WsGuidOwner Recycler;

    /// <summary>
    /// 获取一个值，该值指示对象是否被回收
    /// </summary>
    public boolean IsRecycled;


    /// <summary>
    /// 获取该对象的权限集合
    /// 规约：该属性默认不需要填充。当首次访问时自动通过指定的权限填充类进行填充
    /// </summary>
    public List<WsPermission> Permissions;

}
