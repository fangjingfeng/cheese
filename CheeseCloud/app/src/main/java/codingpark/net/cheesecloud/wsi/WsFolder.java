package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-20.
 */
public class WsFolder implements KvmSerializable{
    // Count: 17
    public WsGuidOwner Father;
    public String ApplicationID;
    public WsGuidOwner LogicDisk;
    public WsSpaceSizer SpaceSize;
    public boolean IsShow;
    public String CreatDate;
    public WsGuidOwner Creater;
    public int FileCount;
    public int FolderCount;
    public String ID;
    public String Name;
    public long AllSpaceSizeKB;
    public long UsedSpaceSizeKB;
    public String RecycleDate;
    public WsGuidOwner Recycler;
    public boolean IsRecycled;
    public WsPermission permissions[];


    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return Father;
            case 1:
                return ApplicationID;
            case 2:
                return LogicDisk;
            case 3:
                return SpaceSize;
            case 4:
                return IsShow;
            case 5:
                return CreatDate;
            case 6:
                return Creater;
            case 7:
                return FileCount;
            case 8:
                return FolderCount;
            case 9:
                return ID;
            case 10:
                return Name;
            case 11:
                return AllSpaceSizeKB;
            case 12:
                return UsedSpaceSizeKB;
            case 13:
                return RecycleDate;
            case 14:
                return Recycler;
            case 15:
                return IsRecycled;
            case 16:
                return permissions;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 17;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                Father = (WsGuidOwner)o;
                break;
            case 1:
                ApplicationID = (String)o;
                break;
            case 2:
                LogicDisk = (WsGuidOwner)o;
                break;
            case 3:
                SpaceSize = (WsSpaceSizer)o;
                break;
            case 4:
                IsShow = (Boolean)o;
                break;
            case 5:
                CreatDate = (String)o;
                break;
            case 6:
                Creater = (WsGuidOwner)o;
                break;
            case 7:
                FileCount = (Integer)o;
                break;
            case 8:
                FolderCount = (Integer)o;
                break;
            case 9:
                ID = (String)o;
                break;
            case 10:
                Name = (String)o;
                break;
            case 11:
                AllSpaceSizeKB = (Long)o;
                break;
            case 12:
                UsedSpaceSizeKB = (Long)o;
                break;
            case 13:
                RecycleDate = (String)o;
                break;
            case 14:
                Recycler = (WsGuidOwner)o;
                break;
            case 15:
                IsRecycled = (Boolean)o;
                break;
            case 16:
                permissions = (WsPermission[])o;
                break;
            default:
                return;

        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.setType(WsGuidOwner.class);
                propertyInfo.setName("Father");
                break;
            case 1:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("ApplicationID");
                break;
            case 2:
                propertyInfo.setType(WsGuidOwner.class);
                propertyInfo.setName("LogicDisk");
                break;
            case 3:
                propertyInfo.setType(WsSpaceSizer.class);
                propertyInfo.setName("SpaceSize");
                break;
            case 4:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsShow");
                break;
            case 5:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("CreatDate");
                break;
            case 6:
                propertyInfo.setType(WsGuidOwner.class);
                propertyInfo.setName("Creater");
                break;
            case 7:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("FileCount");
                break;
            case 8:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("FolderCount");
                break;
            case 9:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("ID");
                break;
            case 10:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("Name");
                break;
            case 11:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("AllSpaceSizeKB");
                break;
            case 12:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("UsedSpaceSizeKB");
                break;
            case 13:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("RecycleDate");
                break;
            case 14:
                propertyInfo.setType(WsGuidOwner.class);
                propertyInfo.setName("Recycler");
                break;
            case 15:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsRecycled");
                break;
            case 16:
                propertyInfo.setType(PropertyInfo.VECTOR_CLASS);
                propertyInfo.setName("permissions");
                break;
            default:
                return;

        }

    }
}
