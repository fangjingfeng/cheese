package codingpark.net.cheesecloud.wsi;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

public class WsMessageInfo implements Serializable {
			/// 消息内容
			//  private InfoSetResultType _resultType;
			/// 消息内容
			private  String Addresser;
			/// 设置具体的消息内容
			private  String Context;

			private int Count;
			
			private String CreateDate;
			
			private String ID;
			
			private boolean IsRead ;
			
			private String Title;
			
			private String ReadDate ;
			
			private String ReceiveDate;
			private String SendDate;
			
			public WsGuidOwner Recipients;
			public List<WsFile> Files ;
			public List<WsFolder> Folders ;
			public String getAddresser() {
				return Addresser;
			}
			public void setAddresser(String addresser) {
				Addresser = addresser;
			}
			public String getContext() {
				return Context;
			}
			public void setContext(String context) {
				Context = context;
			}
			public int getCount() {
				return Count;
			}
			public void setCount(int count) {
				Count = count;
			}
			public String getCreateDate() {
				return CreateDate;
			}
			public void setCreateDate(String  createDate) {
				CreateDate = createDate;
			}
			public String getID() {
				return ID;
			}
			public void setID(String iD) {
				ID = iD;
			}
			public boolean isIsRead() {
				return IsRead;
			}
			public void setIsRead(boolean isRead) {
				IsRead = isRead;
			}
			public String getTitle() {
				return Title;
			}
			public void setTitle(String title) {
				Title = title;
			}
			public String getReadDate() {
				return ReadDate;
			}
			public void setReadDate(String readDate) {
				ReadDate = readDate;
			}
			public String getReceiveDate() {
				return ReceiveDate;
			}
			public void setReceiveDate(String receiveDate) {
				ReceiveDate = receiveDate;
			}
			public String getSendDate() {
				return SendDate;
			}
			public void setSendDate(String sendDate) {
				SendDate = sendDate;
			}
			public WsGuidOwner getRecipients() {
				return Recipients;
			}
			public void setRecipients(WsGuidOwner recipients) {
				Recipients = recipients;
			}
			public List<WsFile> getFiles() {
				return Files;
			}
			public void setFiles(List<WsFile> files) {
				Files = files;
			}
			public List<WsFolder> getFolders() {
				return Folders;
			}
			public void setFolders(List<WsFolder> folders) {
				Folders = folders;
			}
			
}

