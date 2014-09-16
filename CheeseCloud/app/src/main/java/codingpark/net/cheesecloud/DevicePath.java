/**
 * you can find the path of sdcard,flash and usbhost in here
 * @author chenjd
 * @email chenjd@allwinnertech.com
 * @data 2011-8-10
 */
package codingpark.net.cheesecloud;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;
/**
 * define the root path of flash,sdcard,usbhost
 * @author chenjd
 *
 */
public class DevicePath{
	private ArrayList<String> totalDevicesList;
	private ArrayList<String> flashList;
	private ArrayList<String> sdcardList;
	private ArrayList<String> usbList;
	private StorageManager stmg;
	private static final String TAG = DevicePath.class.getSimpleName();
    private Method mMethodGetPaths;
    private Method mMethodGetPathsState;
    public DevicePath(Context context)
	{
		totalDevicesList = new ArrayList<String>();
		flashList = new ArrayList<String>();
		sdcardList = new ArrayList<String>();
		usbList = new ArrayList<String>();
		String flash = Environment.getExternalStorageDirectory().getAbsolutePath();
		stmg = (StorageManager) context.getSystemService(context.STORAGE_SERVICE);
        String[] list = new String[0];//stmg.getVolumePaths();
        try {
            mMethodGetPaths = (Method)stmg.getClass().getMethod("getVolumePaths");
            list = (String[])mMethodGetPaths.invoke(stmg);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < list.length; i++)
		{
			totalDevicesList.add(list[i]);
			if(list[i].equals(flash)){
				flashList.add(list[i]);
			}else if(list[i].contains("extsd")){
				sdcardList.add(list[i]);
			}else if(list[i].contains("usb")){
				usbList.add(list[i]);
			}
		}
	}
	
	public ArrayList<String> getSdStoragePath(){
		return (ArrayList<String>) sdcardList.clone();
	}

	public ArrayList<String> getMountedPath(ArrayList<String> storages){
		ArrayList<String> mounted = new ArrayList<String>();
		for(String dev:storages){
            try {
                mMethodGetPathsState = (Method)stmg.getClass().getMethod("getVolumeState", String.class);

                String state = (String)mMethodGetPathsState.invoke(stmg, dev);
                if(Environment.MEDIA_MOUNTED.equals(state)){
                    mounted.add(dev);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
		}
		return mounted;
	}
	
	public ArrayList<String> getInterStoragePath()
	{
		return (ArrayList<String>) flashList.clone();
	}
	
	public ArrayList<String> getUsbStoragePath()
	{
		return (ArrayList<String>) usbList.clone();
	}
	
	/**
	 * @return
	 */
	public ArrayList<String> getTotalDevicesList()
	{
		return (ArrayList<String>) totalDevicesList.clone();
	}
	
	public int getPartitions(String dPath){
		try{
			if(hasMultiplePartition(dPath)){
				int j = 0;
				File[] fList = new File(dPath).listFiles();
				for(int i = 0; i < fList.length; i++){
					try{
						StatFs statFs = new StatFs(fList[i].getAbsolutePath());
						int count = statFs.getBlockCount();
						if(count == 0){
							continue;
						}
						j++;
					}catch(Exception e){
						Log.d(TAG,fList[i].getName() + "  exception");
						continue;
					}
				}
				return j;
			}else{
				return 0;
			}
		}catch(Exception e){
			return 0;
		}
	}
	
	public boolean hasMultiplePartition(String dPath)
	{
		try
		{
			File file = new File(dPath);
			String minor = null;
			String major = null;
			for(int i = 0; i < totalDevicesList.size(); i++)
			{
				if(dPath.equals(totalDevicesList.get(i)))
				{
					String[] list = file.list();
					for(int j = 0; j < list.length; j++)
					{
						int lst = list[j].lastIndexOf("_");
						if(lst != -1 && lst != (list[j].length() -1))
						{
							major = list[j].substring(0, lst);
							minor = list[j].substring(lst + 1, list[j].length());
							try
							{
							
								Integer.valueOf(major);
								Integer.valueOf(minor);
							}
							catch(NumberFormatException e)
							{
								return false;
							}
						}
						else 
						{
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
		catch(Exception e)
		{
			Log.e(TAG, "hasMultiplePartition() exception e");
			return false;
		}
	}
}


