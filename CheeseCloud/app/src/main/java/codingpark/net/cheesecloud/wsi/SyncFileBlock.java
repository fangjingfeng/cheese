package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-17.
 */
public class SyncFileBlock implements KvmSerializable{
    // Count: 7
    protected long SourceIndex;
    protected long SourceSize;
    protected String SourceMd5;
    protected String SourceAlder32;
    protected long UpdateIndex;
    protected long OffSet;
    protected byte[] UpdateData;

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return SourceIndex;
            case 1:
                return SourceSize;
            case 2:
                return SourceMd5;
            case 3:
                return SourceAlder32;
            case 4:
                return UpdateIndex;
            case 5:
                return OffSet;
            case 6:
                return UpdateData;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 7;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                SourceIndex = (Long)o;
                break;
            case 1:
                SourceSize = (Long)o;
                break;
            case 2:
                SourceMd5 = (String)o;
                break;
            case 3:
                SourceAlder32 = (String)o;
                break;
            case 4:
                UpdateIndex = (Long)o;
                break;
            case 5:
                OffSet = (Long)o;
                break;
            case 6:
                UpdateData = (byte[])o;
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("SourceIndex");
                break;
            case 1:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("SourceSize");
                break;
            case 2:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("SourceMd5");
                break;
            case 3:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("SourceAlder32");
                break;
            case 4:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("UpdateIndex");
                break;
            case 5:
                propertyInfo.setType(PropertyInfo.LONG_CLASS);
                propertyInfo.setName("OffSet");
                break;
            case 6:
                propertyInfo.setType(PropertyInfo.VECTOR_CLASS);
                propertyInfo.setName("UpdateData");
                break;
            default:
                break;
        }

    }
}
