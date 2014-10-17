package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-17.
 */
public class WsPhyFileInfo implements KvmSerializable{
    // Count: 8
    public String LogicName;
    public String ExpLogicName;
    public long SizeKB;
    public long SizeB;
    public long Offset;
    public String PhyName;
    public String Path;
    public byte[] FileBytes;

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return LogicName;
            case 1:
                return  ExpLogicName;
            case 2:
                return SizeKB;
            case 3:
                return SizeB;
            case 4:
                return Offset;
            case 5:
                return PhyName;
            case 6:
                return Path;
            case 7:
                return FileBytes;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 8;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                LogicName = (String)o;
                break;
            case 1:
                ExpLogicName = (String)o;
                break;
            case 2:
                SizeKB = (Long)o;
                break;
            case 3:
                SizeB = (Long)o;
                break;
            case 4:
                Offset = (Long)o;
                break;
            case 5:
                PhyName = (String)o;
                break;
            case 6:
                Path = (String)o;
                break;
            case 7:
                FileBytes = (byte[])o;
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("LogicName");
                break;
            case 1:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("ExpLogicName");
                break;
            case 2:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("SizeKB");
                break;
            case 3:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("SizeB");
                break;
            case 4:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("Offset");
                break;
            case 5:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("PhyName");
                break;
            case 6:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("Path");
                break;
            case 7:
                propertyInfo.setType(MarshalBase64.BYTE_ARRAY_CLASS);
                propertyInfo.setName("FileBytes");
                break;
            default:
                break;
        }

    }
}
