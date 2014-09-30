package codingpark.net.cheesecloud.model;
public class WsGuidOwner
{

    /// <summary>
    /// 唯一标识
    /// </summary>
    private String _guid;
    /// <summary>
    /// 是否冻结
    /// </summary>
    private boolean _isfreezed;


    /// <summary>
    /// 设置唯一标识
    /// </summary>
    public String ID;

    /// <summary>
    /// 获取或设置邮箱
    /// </summary>
    public String Email;

    /// <summary>
    /// 获取或设置性别
    /// </summary>
    //public SexType Sex;
    public int Sex;

    /// <summary>
    /// 获取或设置图片路径
    /// </summary>
    public String FacePicture;

    /// <summary>
    /// 获取或设置自我介绍
    /// </summary>
    public String SelfInfo;

    /// <summary>
    /// 创建时间
    /// </summary>
    public String CreateDate;

    /// <summary>
    /// 获取或设置冻结
    /// </summary>
    public boolean IsFreezed;

    /// <summary>
    /// 获取或设置类型
    /// </summary>
    public int UserType;

    /// <summary>
    /// 获取或设置编码
    /// </summary>
    public String AccessCode;
    /// <summary>
    /// 获取或设置名称
    /// </summary>
    public String Name;
}
