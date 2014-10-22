package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-16.
 */
public class WsSyncFile implements KvmSerializable {

    // Count=23
    public boolean IsPreview;
    public String FullName;
    public String FatherID;
    public String CreatDate;
    public String Extend;
    public String CreaterID;
    public WsPhyFileInfo PhyInfo;
    public boolean IsShow;
    public String ID;
    public String Name;
    public long UsedSpaceSizeKB;
    public String RecycleDate;
    public WsGuidOwner Recycler;
    public boolean IsRecycled;
    public int TranCodeType;
    public int DownCount;
    public int LookCount;
    public long SizeB;
    public String MD5;
    public int SyncType;
    public long BlockIndex;
    public boolean IsFinally;
    public SyncFileBlock Blocks;



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
                return PhyInfo;
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
            case 19:
                return SyncType;
            case 20:
                return BlockIndex;
            case 21:
                return IsFinally;
            case 22:
                return Blocks;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 23;
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
                PhyInfo = (WsPhyFileInfo)o;
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
            case 19:
                SyncType = (Integer)o;
                break;
            case 20:
                BlockIndex = (Long)o;
                break;
            case 21:
                IsFinally = (Boolean)o;
                break;
            case 22:
                Blocks = (SyncFileBlock)o;
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
                propertyInfo.setType(WsPhyFileInfo.class);
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
            case 19:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("SyncType");
                break;
            case 20:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("BlockIndex");
                break;
            case 21:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsFinally");
                break;
            case 22:
                propertyInfo.setType(SyncFileBlock.class);
                propertyInfo.setName("Blocks");
            default:
                break;
        }
    }

}
