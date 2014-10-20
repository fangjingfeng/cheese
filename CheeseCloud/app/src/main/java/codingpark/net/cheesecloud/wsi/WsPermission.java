package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-20.
 */
public class WsPermission implements KvmSerializable{
    public int Type;

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return Type;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                Type = (Integer)o;
                break;
            default:
                return;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("Type");
                break;
            default:
                return;
        }
    }
}
