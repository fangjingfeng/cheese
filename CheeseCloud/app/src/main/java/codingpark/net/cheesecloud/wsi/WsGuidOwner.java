package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-17.
 */
public class WsGuidOwner implements KvmSerializable{

    // Count: 10
    public String ID;
    public String Email;
    public int Sex;
    public String FacePicture;
    public String SelfInfo;
    public String CreateDate;
    public boolean IsFreezed;
    public int UserType;
    public String AccessCode;
    public String Name;

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return ID;
            case 1:
                return Email;
            case 2:
                return Sex;
            case 3:
                return FacePicture;
            case 4:
                return SelfInfo;
            case 5:
                return CreateDate;
            case 6:
                return IsFreezed;
            case 7:
                return UserType;
            case 8:
                return AccessCode;
            case 9:
                return Name;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 10;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                ID = (String)o;
                break;
            case 1:
                Email = (String)o;
                break;
            case 2:
                Sex = (Integer)o;
                break;
            case 3:
                FacePicture = (String)o;
                break;
            case 4:
                SelfInfo = (String)o;
                break;
            case 5:
                CreateDate = (String)o;
                break;
            case 6:
                IsFreezed = (Boolean)o;
                break;
            case 7:
                UserType = (Integer)o;
                break;
            case 8:
                AccessCode = (String)o;
                break;
            case 9:
                Name = (String)o;
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
                propertyInfo.setName("ID");
                break;
            case 1:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("Email");
                break;
            case 2:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("Sex");
                break;
            case 3:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("FacePicture");
                break;
            case 4:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("SelfInfo");
                break;
            case 5:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("CreateDate");
                break;
            case 6:
                propertyInfo.setType(PropertyInfo.BOOLEAN_CLASS);
                propertyInfo.setName("IsFreezed");
                break;
            case 7:
                propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
                propertyInfo.setName("UserType");
                break;
            case 8:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("AccessCode");
                break;
            case 9:
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setName("Name");
                break;
            default:
                break;
        }

    }
}
