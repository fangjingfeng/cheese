package codingpark.net.cheesecloud.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**
	 * 获取当前时间
	 */
	public static String getDateTime(){
		SimpleDateFormat formatter =new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		Date curDate=new Date(System.currentTimeMillis());//获取当前时间       
		return formatter.format(curDate);    
	}
	
}
