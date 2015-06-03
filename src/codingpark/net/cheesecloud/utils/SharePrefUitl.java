package codingpark.net.cheesecloud.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrefUitl {
	private static String CONFIG = "config";
	private static SharedPreferences sharedPreferences;
	//存储
	public static void saveStringData(Context context,String key,String value){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		
		sharedPreferences.edit().putString(key, value).commit();
	}
	//获取
	public static String getStringData(Context context,String key,String defValue){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defValue);
	}
	
	
	
}