package codingpark.net.cheesecloud.constances;

public class MyConstances {

	/**
	 * sharedpreferenced 的名称
	 */
	public static String SP_NAME = "config";
	
	/**
	 * sp 中保存密码的key 
	 */
	public static String PWD = "password";
	/**
	 * sp 中保存设置是否完成的 KEY  
	 */
	public static String isSettingFinish = "isSettingFinish";
	
	/**
	 * sp 中保存设置SIM 序列号的 KEY  
	 */
	public static String SIM = "sim";
	
	/**
	 * sp 中保存 防盗保护是否开启 的 key  结果为 boolean 值 
	 */
	public static String isLostFindEnable = "isLostFindEnable";
	/**
	 * sp 中保存 安全号码的 KEY  
	 */
	public static String safe_number = "safe_number";
	/**
	 * sp 中保存手机防盗模块，的新的名称的，KEY 
	 */
	public static String lost_find_new_name = "lost_find_new_name";
	/**
	 * sp 中保存设置中心，自动更新，开关的 KEY 
	 */
	public static String isAutoUpdate = "isAutoUpdate";
	/**
	 * sp 中保存设置中心，黑名单拦截，开关的 KEY 
	 */
//	public static String isBlackNumberEnable = "isBlackNumberEnable";
	
	/**
	 * sp 中保存来电归属地提示框的风格 
	 * {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	 */
	public static String selectShowAddressStyle = "selectShowAddressStyle";
	/*
	 * 代表成功的字符串
	 */
	public static String successful = "SUCCESSFUL";
	/*
	 * 代表失败的字符串
	 */
	public static String failure = "FAILURE";
	/**
	 * sp 中保存来电归属地提示框坐标
	 */
	public static String lastX = "last_X";
	/**
	 * sp 中保存来电归属地提示框坐标
	 */
	public static String lastY = "last_Y";
	
	/**
	 * sp 中保存进程，管理中，是否显示系统进程的KEY 
	 */
	public static String IsShowSystemProce = "IsShowSystemProce";
	
	/**
	 * 接口 getFileConvertInfoByID 中又来判读请求是否成功 
	 *   不成功就返回此常量
	 */
	public static String isReqesteSuccessful ="ISREQESTESUCCESSFUL";
	
	// 停止
    public static final int OPTION_STOP = 1;
    // 播放
    public static final int OPTION_PLAY = 2;
    // 暂停
    public static final int OPTION_PAUSE = 3;
    // 继续
    public static final int OPTION_CONTINUE = 4;
    
    //更新进度
    public static final int MUSIC_PROGRESS = 5;
    //音乐跳转
    public static final int MUSIC_SEEK = 6;
    
    // 列表循环
    public static final int MODE_CIRCLE = 7;
    // 单曲循环
    public static final int MODE_LOOP = 8;
    // 顺序播放
    public static final int MODE_ORDER = 9;
    // 随机播放
    public static final int MODE_RANDOM = 10;
    
    //音乐结束
    public static final int MUSIC_STOP = 11;
    //当前音乐播放完成
    public static final int MUSIC_COMPLETE = 12;
    
    //手动扫描结束
    public static final int MUSIC_SCAN_FINISHED = 13;
    
    //代表返回的列表中是否有文件
    public static final int Retrun_file_is_null = 404;
    /**
     * 用于保存给我消息条数 
     */
    public static final String Letters_name_key = "LettersKey";
    /**
     * 用于记录来的信件的条数 
     */
    public static final String Notic_name_key = "NoticKey";
	/**
	 * 用于Intent 获取当前文件夹对象的kay
	 */
    public static final String GetFotlerObject ="GETFOTLEROBJECT";
    
    /**
     * 用于 向ReplyMail Activity中传递选中文件的对象
     */
    public static final String GetSenderMeliaObjects ="GETSENDERMELIAOBJECTS";
    
    public static final String PutSendFile ="PUTSENDFILES";
    
    public static final String mIsHeiteSenderTitle ="MISHEITESENDERTITLE";
    
}