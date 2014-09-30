package codingpark.net.cheesecloud.model;

public class WsMessage
{
    /// <summary>
    /// 获取或设置对象的GUID
    /// </summary>
    public String Guid;

    /// <summary>
    /// 发送者
    /// </summary>
    public String Sender;

    /// <summary>
    /// 获取或设置消息接收者的类型
    /// </summary>
    //public ReceiverType ReceiverType { get; set; }
    public int ReceiverType;

    /// <summary>
    /// 接收者
    /// </summary>
    public String Receiver;

    /// <summary>
    /// 创建时间
    /// </summary>
    public String CreateDate;

    /// <summary>
    /// 发送时间
    /// </summary>
    public String SendDate;

    /// <summary>
    /// 接收时间
    /// </summary>
    public String ReceiveDate;

    /// <summary>
    /// 读取时间
    /// </summary>
    public String ReadDate;

    /// <summary>
    /// 获取或设置消息的生命流程
    /// </summary>
    //public TrackingType Tracking { get; set; }
    public int Tracking;


    /// <summary>
    /// 获取或设置消息的具体内容
    /// </summary>
    //public WsMessageInfo Info { get; set; }
    public WsMessageInfo Info;
}
