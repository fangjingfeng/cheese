package codingpark.net.cheesecloud.model;

public class WsFile
{
    //gregion 字段
    /// <summary>
    /// 获取一个值，该值指示对象是否被删除
    /// </summary>
    private boolean _isDeleted;

    /// <summary>
    /// 获取对象的GUID
    /// </summary>
    private String _guid;


    /// <summary>
    /// 已经使用空间大小
    /// </summary>
    private long _usedSpaceSizeKB;

    /// <summary>
    /// 获取一个值，该值指示对象是否被回收
    /// </summary>
    private boolean _isRecycled;

    private String _expName;
    //gendregion

    //gregion 属性
    /// <summary>
    /// 获取或设置是否为预览文件
    /// </summary>
    public boolean IsPreview;
    /// <summary>
    /// 获取或设置逻辑文件的全名
    /// </summary>
    public String FullName;

    /// <summary>
    /// 获取逻辑文件的扩展文件名（不含“.”）
    /// </summary>
    public String ExpName;

    /// <summary>
    /// 获取或设置逻辑文件所在的逻辑文件夹
    /// </summary>
    public WsGuidOwner Father;

    /// <summary>
    /// 获取或设置该实例的创建日期时间
    /// </summary>
    public String CreatDate;

    /// <summary>
    /// 获取或设置文件扩展名
    /// </summary>
    public String Extend;

    /// <summary>
    /// 获取或设置该逻辑文件的创建者（用户）的相关信息
    /// </summary>
    public WsGuidOwner Creater;

    /// <summary>
    /// 获取或设置该文件对应的物理信息
    /// </summary>
    //public FileInfo PhyInfo { get; set; }
    public String PhyInfo;


    /// <summary>
    /// 获取或设置 ，该值指示本实例是否显示在逻辑文件列表中
    /// </summary>
    public boolean IsShow;

    /// <summary>
    /// 获取一个值，该值指示对象是否被删除
    /// </summary>
    boolean IsDeleted;

    /// <summary>
    /// 获取对象的GUID
    /// </summary>
    public String Guid;

    /// <summary>
    /// 获取或设置名称(备注)
    /// </summary>
    public String Name;

    /// <summary>
    /// 获取或设置实例所使用的存储空间容量（KB）
    /// </summary>
    public long UsedSpaceSizeKB;


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
    /// 获取或设置转码类型
    /// </summary>
    //public TransCodeType TranCodeType;
    public String TranCodeType;

    /// <summary>
    /// 下载次数
    /// </summary>
    public int DownCount;

    /// <summary>
    /// 查看次数
    /// </summary>
    public int LookCount;

}
