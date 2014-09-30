package codingpark.net.cheesecloud.model;

/// <summary>
/// WebService具备空间尺寸的实体类的接口
/// author:陈偲
/// time:20140904
/// </summary>
public class WsSpaceSizer
{
    /// <summary>
    /// 获取或设置该实例的总存储空间容量（KB）
    /// </summary>
    long AllSpaceSizeKB;

    /// <summary>
    /// 获取或设置实例的已使用的存储空间容量（KB）
    /// </summary>
    long UsedSpaceSizeKB;

    /// <summary>
    /// 获取该实例的剩余存储空间容量（KB）
    /// </summary>
    long UnusedSpaceSizeKB;

    ///// <summary>
    ///// 占用该实例的可用空间，并同时更新该实例的空间占用相关属性值。
    ///// 返回值：如果占用成功（可用空间足够），则返回True，否则返回False（可用空间不足）
    ///// </summary>
    ///// <param name="sizeKB">要占用的空间大小</param>
    ///// <returns></returns>
    //bool UseSpaceSize(long sizeKB);

    ///// <summary>
    ///// 释放该实例的已用空间，并同时更新该实例的空间占用相关属性值。
    ///// 返回值：如果释放成功（已用空间足够），则返回True，否则返回False（已用空间不足）
    ///// </summary>
    ///// <param name="sizeKB">要释放的空间大小</param>
    ///// <returns></returns>
    //bool RelSpaceSize(long sizeKB);
}
