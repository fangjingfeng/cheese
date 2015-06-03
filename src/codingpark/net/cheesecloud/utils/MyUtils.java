package codingpark.net.cheesecloud.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


public class MyUtils {
	/**
	 * 将输入流转换为字符串
	 * @param input
	 * @return
	 */
	public static String converStream2String(InputStream input){
		String str = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); //
			
			byte[] buffer = new byte[512];
			int len = -1;
			
			while((len = input.read(buffer))!=-1){
				baos.write(buffer, 0, len);
			}
			
			input.close();
			
			str = new String(baos.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 对字符串进行MD5加密
	 * @param string
	 * @return
	 */
	public static String md5(String fileName) {
		String fileSuffix=fileName.substring(fileName.lastIndexOf("."));
		String file=fileName.substring(0,fileName.lastIndexOf("."));
		//System.out.println("filePaths :"+filePaths+" file :"+file);
	    byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(file.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString()+fileSuffix;
	}
	//对字符串进行加密
	public static String getMD5(String instr) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(instr.getBytes());
			byte tmp[] = md.digest(); 
										
			char str[] = new char[16 * 2]; 
										
			int k = 0; 
			for (int i = 0; i < 16; i++) { 
										
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; 
														
				str[k++] = hexDigits[byte0 & 0xf]; 
			}
			s = new String(str).toUpperCase(); 

		} catch (Exception e) {

		}
		return s;
	}
	
	/**
	 * 弹出toast
	 * @param ctx  Activity 对象
	 * @param msg 要显示的信息
	 */
	public static void showToast(final Activity ctx,final String msg){
		
		if("main".equals(Thread.currentThread().getName())){ //
			Toast.makeText(ctx, msg, 0).show();
			
		}else{
			ctx.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ctx, msg, 0).show();
				}
			});
		}
		
	};
	
	
	/**
	 * 标准的MD5 加密算法
	 * @param password	明文密码
	 * @return	加密后的密文
	 */
	public static String md5Encrypt(String password) {
		StringBuilder sb = new StringBuilder();

		try {
			MessageDigest md = MessageDigest.getInstance("md5");

			/*
			 * 在公司中 大多都是用 密码明文 + 公司私有的密码  进行MD5 加密，得到最终的密文 
			 */
			password = password+"lasjidf7asd89f6w4klj9s8ydf2354sdf"; 
			
			byte[] encryptPwd = md.digest(password.getBytes());

			for (byte b : encryptPwd) {
				int num = b & 0XFF;
				String hexString = Integer.toHexString(num);
				
				if (hexString.length() == 1) {
					sb.append("0" + hexString);
				} else {
					sb.append(hexString);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 取得指定文件的MD5值
	 * @param filePath 文件的全路径
	 * @return
	 */
	public static String getFileMd5(String filePath){

		String md5Str = null;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			
			FileInputStream fileIn = new FileInputStream(new File(filePath));
			
			byte[] buffer = new byte[1024];
			
			int len = 0;
			while((len = fileIn.read(buffer))!=-1){
				md.update(buffer, 0, len);
			}
			byte[] digest = md.digest();
			
			StringBuffer sb = new StringBuffer();
			
			for (byte b : digest) {
				int num = b & 0XFF;
				String hexString = Integer.toHexString(num);
				
				if (hexString.length() == 1) {
					sb.append("0" + hexString);
				} else {
					sb.append(hexString);
				}
			}
			md5Str = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5Str;
	}
	
	/**
	 * 判断指定的服务是否在运行
	 * @param class1
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx,
			Class class1) {
		
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<RunningServiceInfo> serviceList = am.getRunningServices(200);
		
		for(RunningServiceInfo info : serviceList){
			
			String runningClassName = info.service.getClassName();
			if(runningClassName.equals(class1.getName())){ // 判断正在运行的服务的类名，与传进来的服务的类名，是否相同
				return true;
			}
		}
		return false;
	};
	
	
	// 二进制转换图片
		public Bitmap getBitmapFromByte(byte[] temp) {
		      if (temp != null) {
		     	 /*YuvImage yuvimage=new YuvImage(temp, ImageFormat.NV21, 20,20, null);//20、20分别是图的宽度与高度
		          ByteArrayOutputStream baos = new ByteArrayOutputStream();
		          yuvimage.compressToJpeg(new Rect(0, 0,20, 20), 80, baos);//80--JPG图片的质量[0-100],100最高
		          byte[] jdata = baos.toByteArray();*/
		          Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
		          System.out.println("temp .leng="+temp.length+":"+bitmap);
		          return bitmap;
		      } else {
		          return null;
		      }
		  }
	
	
	
}
