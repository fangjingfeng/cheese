package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-20.
 */
public class WsSpaceSizer implements KvmSerializable{
    // Count: 3
    public long AllSpaceSizeKB;
    //用户空间大小
    public long UsedSpaceSizeKB;
    //未使用的控件大小
    public long UnusedSpaceSizeKB;

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return AllSpaceSizeKB;
            case 1:
                return UsedSpaceSizeKB;
            case 2:
                return UnusedSpaceSizeKB;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 3;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                AllSpaceSizeKB = (Long)o;
                break;
            case 1:
                UsedSpaceSizeKB = (Long)o;
                break;
            case 2:
                UnusedSpaceSizeKB = (Long)o;
                break;
            default:
                return;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i) {
            case 0:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("AllSpaceSizeKB");
                break;
            case 1:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("UsedSpaceSizeKB");
                break;
            case 2:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("UnusedSpaceSizeKB");
                break;
            default:
                return;
        }
    }
}
