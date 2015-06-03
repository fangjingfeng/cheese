package codingpark.net.cheesecloud.wsi;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by ethanshan on 14-10-17.
 */
public class FileInfo implements KvmSerializable,Serializable{

    // Count: 8
    protected String LogicName;
    protected String ExpLogicName;
    protected long SizeKB;
    protected long SizeB;
    protected int Offset;
    protected String phyName;
    
    public String getPhyName() {
		return phyName;
	}
    
    public void setPhyName(String phyName){
    	this.phyName=phyName;
    }
    
    public String getExpLogicName() {
		return ExpLogicName;
	}
    
    public void setExpLogicName(String expLogicName){
    	this.ExpLogicName=ExpLogicName;
    }
    
	protected String Path;
    public byte[] fileStream;

    
    @Override
    public Object getProperty(int i) {
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 0;
    }

    @Override
    public void setProperty(int i, Object o) {

    }
    
    public byte[] getFileStream(){
    	return fileStream ;
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

    }
}
