package codingpark.net.cheesecloud.wsi;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/*
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <SendMessage xmlns="http://tempuri.org/">
      <message>
        <ID>string</ID>
        <Sender>string</Sender>
        <ReceiverType>int</ReceiverType>
        <Receiver>string</Receiver>
        <CreateDate>string</CreateDate>
        <SendDate>string</SendDate>
        <ReceiveDate>string</ReceiveDate>
        <ReadDate>string</ReadDate>
        <Context>string</Context>
        <Tracking>int</Tracking>
      </message>
    </SendMessage>
  </soap:Body>
</soap:Envelope>
*/

public class WsMessage implements KvmSerializable {

	//获取或设置对象的GUID
	private String ID;
	//发送者
	private String Sender;
	//接受者
	private int ReceiverType;
	//
	private String Receiver;

	//创建时间
	private String CreateDate;
	//发送时间
	private String SendDate;
	//接受时间
	private String ReceiveDate;
	//读取时间
	private String ReadDate;

	private String  Context; 
	
	private int Tracking;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getSender() {
		return Sender;
	}

	public void setSender(String sender) {
		Sender = sender;
	}

	public int getReceiverType() {
		return ReceiverType;
	}

	public void setReceiverType(int receiverType) {
		ReceiverType = receiverType;
	}

	public String getReceiver() {
		return Receiver;
	}

	public void setReceiver(String receiver) {
		Receiver = receiver;
	}

	public String getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(String createDate) {
		CreateDate = createDate;
	}

	public String getSendDate() {
		return SendDate;
	}

	public void setSendDate(String sendDate) {
		SendDate = sendDate;
	}

	public String getReceiveDate() {
		return ReceiveDate;
	}

	public void setReceiveDate(String receiveDate) {
		ReceiveDate = receiveDate;
	}

	public String getReadDate() {
		return ReadDate;
	}

	public void setReadDate(String readDate) {
		ReadDate = readDate;
	}

	public String getContext() {
		return Context;
	}

	public void setContext(String context) {
		Context = context;
	}

	public int getTracking() {
		return Tracking;
	}

	public void setTracking(int tracking) {
		Tracking = tracking;
	}

	
	
	
	@Override
	public Object getProperty(int arg0) {
			switch (arg0) {
	        case 0:
	            return ID;
	        case 1:
	            return Sender;
	        case 2:
	            return ReceiverType;
	        case 3:
	            return Receiver;
	        case 4:
	            return CreateDate;
	        case 5:
	            return SendDate;
	        case 6:
	            return ReceiveDate;
	        case 7:
	            return ReadDate;
	        case 8:
	            return Context;
	        case 9:
	            return Tracking;
	        default:
	            return null;
	    }
	}

	@Override
	public int getPropertyCount() {
		return 10;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo propertyInfo) {
		 switch (arg0) {
         case 0:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("ID");
             break;
         case 1:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("Sender");
             break;
         case 2:
             propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
             propertyInfo.setName("ReceiverType");
             break;
         case 3:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("Receiver");
             break;
         case 4:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("CreateDate");
             break;
         case 5:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("SendDate");
             break;
         case 6:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("ReceiveDate");
             break;
         case 7:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("ReadDate");
             break;
         case 8:
             propertyInfo.setType(PropertyInfo.STRING_CLASS);
             propertyInfo.setName("Context");
             break;
         case 9:
             propertyInfo.setType(PropertyInfo.INTEGER_CLASS);
             propertyInfo.setName("Tracking");
             break;
         default:
             break;
     }
	}

	@Override
	public void setProperty(int arg0, Object o) {
		switch (arg0) {
        case 0:
            ID = (String)o;
            break;
        case 1:
        	Sender = (String)o;
            break;
        case 2:
        	ReceiverType = (Integer)o;
            break;
        case 3:
        	Receiver = (String)o;
            break;
        case 4:
        	CreateDate = (String)o;
            break;
        case 5:
        	SendDate = (String)o;
            break;
        case 6:
        	ReceiveDate = (String)o;
            break;
        case 7:
        	ReadDate = (String)o;
            break;
        case 8:
        	Context = (String)o;
            break;
        case 9:
        	Tracking = (Integer)o;
            break;
        default:
            break;
    }
	}

	@Override
	public String toString() {
		return "WsMessage [ID=" + ID + ", Sender=" + Sender + ", ReceiverType="
				+ ReceiverType + ", Receiver=" + Receiver + ", CreateDate="
				+ CreateDate + ", SendDate=" + SendDate + ", ReceiveDate="
				+ ReceiveDate + ", ReadDate=" + ReadDate + ", Context="
				+ Context + ", Tracking=" + Tracking + "]";
	}	

	
}
