package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-17.
 */
public class WsFile implements KvmSerializable,Serializable{

    // Count=19
    /**
     * 文件预览文件
     */
    public boolean IsPreview;
    /**
     * 文件逻辑的全名
     */
    public String FullName;
    /**
     * ID文件的父文件夹
     */
    public String FatherID;
    /**
     * The file's create date
     */
    public String CreatDate;
    /**
     * The file extension name
     * Exclude "."
     */
    public String Extend;
    /**
     * The file's creator ID
     */
    public String CreaterID;
    /**
     * ?
     */
    public FileInfo phyInfo;
    /**
     * 显示文件列表的逻辑
     */
    public boolean IsShow;
    /**
     * The file's GUID
     */
    public String ID;
    /**
     * ?
     */
    public String Name;
    /**
     * The space usage(KB)
     */
    public long UsedSpaceSizeKB;
    /**
     * The file deleted date
     */
    public String RecycleDate;
    /**
     * The user object who delete this file
     */
    public WsGuidOwner Recycler;
    /**
     * The file is deleted
     */
    public boolean IsRecycled;
    /**
     * Transcoding type
     */
    public int TranCodeType;
    /**
     * Download time
     */
    public int DownCount;
    /**
     * Show time
     */
    public int LookCount;
    /**
     * The file size in byte unit
     */
    public long SizeB;
    /**
     * 文件的md5值(整个文件)
     */
    public String MD5;



    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return IsPreview;
            case 1:
                return FullName;
            case 2:
                return FatherID;
            case 3:
                return CreatDate;
            case 4:
                return Extend;
            case 5:
                return CreaterID;
            case 6:
                return phyInfo;
            case 7:
                return IsShow;
            case 8:
                return ID;
            case 9:
                return Name;
            case 10:
                return UsedSpaceSizeKB;
            case 11:
                return RecycleDate;
            case 12:
                return Recycler;
            case 13:
                return IsRecycled;
            case 14:
                return TranCodeType;
            case 15:
                return DownCount;
            case 16:
                return LookCount;
            case 17:
                return SizeB;
            case 18:
                return MD5;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 19;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                IsPreview = (Boolean)o;
                break;
            case 1:
                FullName = (String)o;
                break;
            case 2:
                FatherID = (String)o;
                break;
            case 3:
                CreatDate = (String)o;
                break;
            case 4:
                Extend = (String)o;
                break;
            case 5:
                CreaterID = (String)o;
                break;
            case 6:
            	phyInfo = (FileInfo)o;
                break;
            case 7:
                IsShow = (Boolean)o;
                break;
            case 8:
                ID = (String)o;
                break;
            case 9:
                Name = (String)o;
                break;
            case 10:
                UsedSpaceSizeKB = (Long)o;
                break;
            case 11:
                RecycleDate = (String)o;
                break;
            case 12:
                Recycler = (WsGuidOwner)o;
                break;
            case 13:
                IsRecycled = (Boolean)o;
                break;
            case 14:
                TranCodeType = (Integer)o;
                break;
            case 15:
                DownCount = (Integer)o;
                break;
            case 16:
                LookCount = (Integer)o;
                break;
            case 17:
                SizeB = (Long)o;
                break;
            case 18:
                MD5 = (String)o;
                break;
            default:
                break;
        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsPreview");
                break;
            case 1:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("FullName");
                break;
            case 2:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("FatherID");
                break;
            case 3:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("CreateDate");
                break;
            case 4:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("Extend");
                break;
            case 5:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("CreaterID");
                break;
            case 6:
                propertyInfo.setType(FileInfo.class);
                propertyInfo.setName("PhyInfo");
                break;
            case 7:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsShow");
                break;
            case 8:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("ID");
                break;
            case 9:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("Name");
                break;
            case 10:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("UsedSpaceSizeKB");
                break;
            case 11:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("RecycleDate");
                break;
            case 12:
                propertyInfo.setType(WsGuidOwner.class);
                propertyInfo.setName("Recycler");
                break;
            case 13:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsRecycled");
                break;
            case 14:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("TranCodeType");
                break;
            case 15:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("DownCount");
                break;
            case 16:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("LookCount");
                break;
            case 17:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("SizeB");
                break;
            case 18:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("MD5");
                break;
            default:
                break;
        }

    }
    
}
