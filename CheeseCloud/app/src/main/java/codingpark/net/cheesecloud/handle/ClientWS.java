package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;
import org.kobjects.base64.Base64;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.enumr.LoginResultType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.utils.Misc;
import codingpark.net.cheesecloud.wsi.FileInfo;
import codingpark.net.cheesecloud.wsi.SyncFileBlock;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsFolder;
import codingpark.net.cheesecloud.wsi.WsGuidOwner;
import codingpark.net.cheesecloud.wsi.WsPermission;
import codingpark.net.cheesecloud.wsi.WsPhyFileInfo;
import codingpark.net.cheesecloud.wsi.WsSpaceSizer;
import codingpark.net.cheesecloud.wsi.WsSyncFile;

/**
 * Created by ethanshan on 14-10-15.
 * The class singleton pattern, used to call web service interface.
 */
public final class ClientWS {
    private static final String TAG     = "ClientWS";

    private static ClientWS client      = null;

    // Web Services method name
    /**
     * In reality, the endpoint address should dynamic fetch from SharedPreference,
     * this const string used for SharedPrefernece.getString's default value parameter
     */
    public static final String DEFAULT_ENDPOINT = "http://192.168.0.101:22332/ClientWS.asmx";
    /**
     * Web service API name used by upload file process
     */
    public static final String METHOD_CHECKEDFILEINFO = "CheckedFileInfo";
    /**
     * Web service API name used by create a folder on server process
     */
    public static final String METHOD_CREATEFOLDER = "CreateFolder";
    /**
     * Web service API name used by delete folders and files batched
     */
    public static final String METHOD_DELETE_FOLDER_AND_FILE = "DeleteFolderAndFile";
    /**
     * Web service API name used by get folders and files of the parent folder on
     * server
     */
    public static final String METHOD_DOWNLOAD_FILE = "DownloadFile";
    /**
     * Web service API name used by get all root disk list process from server
     */
    public static final String METHOD_GETDISK = "GetDisk";
    /**
     * Web service API name used by get the folder information from server process
     */
    public static final String METHOD_GETFOLDERINFO = "GetFolderInfo";
    /**
     * Web service API name used by get folders and files of the parent folder on
     * server
     */
    public static final String METHOD_GETFOLDERLIST = "GetFolderList";
    /**
     * Web service API name used to copy or clip files or folders to other folder
     */
    public static final String METHOD_PASTE_OBJ = "PasteObjs";
    /**
     * Web service API name used to rename file or folder
     */
    public static final String METHOD_RENAME_OBJ = "RenameObj";
    /**
     * Web service API name used by upload file process
     */
    public static final String METHOD_UPLOADFILE = "UploadFile";
    /**
     * Web service API name used by user login process
     */
    public static final String METHOD_USERLOGIN = "UserLogin";


    // Web services server configurations
    // Namespace
    private String NAMESPACE        = "http://tempuri.org/";
    // EndPoint
    private String mEndPoint        = "http://192.168.0.108:22332/ClientWS.asmx";

    private static String session_id            = "";
    private static String s_id                  = "";

    private static Context mContext             = null;

    private ClientWS() {
        SharedPreferences prefs = mContext.getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);
        mEndPoint = prefs.getString(AppConfigs.SERVER_ADDRESS, DEFAULT_ENDPOINT);
    }

    /**
     * The ClientWS class creator(Singleton)
     *
     * @param context
     * @return
     */
    public static ClientWS getInstance(Context context) {
        mContext = context;
        if (client == null)
            client = new ClientWS();
        return client;
    }

    public void setEndPoint(String url) {
        mEndPoint = url;
    }

    /**
     * Before start upload file, need call this function to make a record of the file
     * on remote server. This function transfer the target WsFile object to server
     * include whole file MD5 value. When server receive it, will check server is have
     * the file. If already have it , will return 1. If not have return 2. For network
     * access failed or other reason will return -1.
     *
     * @param wsFile    The target file information include whole file MD5 value.
     * @return
     */
    public int checkedFileInfo(WsFile wsFile) {
        int result;

        wsFile.CreaterID = AppConfigs.current_remote_user_id;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_CHECKEDFILEINFO;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_CHECKEDFILEINFO);
        // add web service method parameter
        PropertyInfo p_fileInfo = new PropertyInfo();
        p_fileInfo.setName("file");
        p_fileInfo.setValue(wsFile);
        p_fileInfo.setType(WsFile.class);
        rpc.addPropertyIfValue(p_fileInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Mapping
        envelope.addMapping(NAMESPACE, WsFile.class.getSimpleName(), WsFile.class);
        envelope.addMapping(NAMESPACE, FileInfo.class.getSimpleName(), FileInfo.class);
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);

            // Process return data
            // Fetch operation result
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getProperty("CheckedFileInfoResult").toString());
            if ((result == CheckedFileInfoResultType.RESULT_CHECK_SUCCESS)
                    || (result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD)) {
                // Fetch file info
                SoapObject obj = (SoapObject) resp.getProperty("file");
                wsFile.ID = obj.getProperty("ID").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }


    /**
     * Rename target file
     * @param id The target file id on remote server
     * @param type "file" or "folder"
     * @param newName The new file name
     * @return {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int renameObj(String id, String type, String newName) {
        int result = WsResultType.Success;

        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_RENAME_OBJ;

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_RENAME_OBJ);
        // add web service method parameter
        rpc.addProperty("id", id);
        rpc.addProperty("type", type);
        rpc.addProperty("newName", newName);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            // Get webservice return object
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getProperty("RenameObjResult").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return WsResultType.Faild;
        }
        return result;
    }

    /**
     * Copy or clip remote files and folders to target folder
     * @param ids The files or folders remote GUID string, separate with ","(1111,22222,33333)
     * @param types The file type string, value of "file" or "folder", separate with ","(file,folder,folder)
     * @param identity The operation type("copy" or "clip")
     * @param parentID The moved file target folder GUID
     * @return {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int pasteObj(String ids, String types, String identity, String parentID) {
        int result = WsResultType.Success;

        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_PASTE_OBJ;

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_PASTE_OBJ);
        // add web service method parameter
        // string ids, string types, string identity, Guid parentID
        rpc.addProperty("ids", ids);
        rpc.addProperty("types", types);
        rpc.addProperty("identity", identity);
        rpc.addProperty("parentID", parentID);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            // Get webservice return object
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getProperty("PasteObjsResult").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return WsResultType.Faild;
        }
        return result;
    }


    /**
     ** Web service interface, call this function to create a folder on the current
     * server. The wsFolder parameter point the creating folder's parent(Server need
     * Folder guid).
     *
     * @param wsFolder    Store the to creat folder's parent folder information(Server
     * need folder guid).
     * @return
     */
    public int createFolder(WsFolder wsFolder) {
        int result;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_CREATEFOLDER;

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_CREATEFOLDER);
        // add web service method parameter
        PropertyInfo p_folderInfo= new PropertyInfo();
        p_folderInfo.setName("folder");
        p_folderInfo.setValue(wsFolder);
        p_folderInfo.setType(WsFolder.class);
        rpc.addPropertyIfValue(p_folderInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, WsFolder.class.getSimpleName(), WsFolder.class);
        envelope.addMapping(NAMESPACE, WsSpaceSizer.class.getSimpleName(), WsSpaceSizer.class);
        envelope.addMapping(NAMESPACE, WsPermission.class.getSimpleName(), WsPermission.class);
        envelope.addMapping(NAMESPACE, FileInfo.class.getSimpleName(), FileInfo.class);
        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            // Get webservice return object
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getProperty("CreateFolderResult").toString());
            if (result == WsResultType.Success) {
                SoapObject r_folder = (SoapObject)resp.getProperty("folder");
                wsFolder.ID = r_folder.getProperty("ID").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WsResultType.Faild;
        }
        return result;
    }

    /**
     * Delete folders and files batched
     * @param delFileIds The file's to be deleted remote id list
     * @param delFolderIds The folder's to be deleted remote id list
     * @return WsResultType
     *
     */
    public int deleteFolderAndFile(ArrayList<String> delFileIds, ArrayList<String> delFolderIds) {
        int result = WsResultType.Success;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_DELETE_FOLDER_AND_FILE;

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_DELETE_FOLDER_AND_FILE);
        String id_str = "";
        String type_str = "";
        for (int i = 0; i < delFileIds.size(); i++) {
            id_str += delFileIds.get(i);
            type_str += "file";
            if (i != (delFileIds.size() - 1)) {
                id_str += ",";
                type_str += ",";
            }
        }
        for (int i = 0; i < delFolderIds.size(); i++) {
            id_str += delFolderIds.get(i);
            type_str += "folder";
            if (i != (delFileIds.size() - 1)) {
                id_str += ",";
                type_str += ",";
            }
        }
        // add web service method parameter
        rpc.addProperty("ids", id_str);
        rpc.addProperty("types", type_str);


        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            // Get webservice return object
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getProperty("DeleteFolderAndFileResult").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return WsResultType.Faild;
        }
        return result;
    }

    /**
     * Download target file from server to local. This function will
     * parse the web service response message, and fetch download block
     * data and IsFinally. In the end, set the parameter to file object.
     * WsSyncFile object need follow parameters :
     * file.ID:The target file GUID
     * file.Blocks.OffSet: The request block of file offset
     * file.Blocks.SourceSize:The request block size(Client set)
     *
     * @param file    Store the target file information, The guid of the file
     * @return {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int downloadFile(WsSyncFile file){
        int result = WsResultType.Success;

        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_DOWNLOAD_FILE;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_DOWNLOAD_FILE);
        // add web service method parameter
        PropertyInfo p_fileInfo = new PropertyInfo();
        p_fileInfo.setName("file");
        p_fileInfo.setValue(file);
        p_fileInfo.setType(WsSyncFile.class);
        rpc.addPropertyIfValue(p_fileInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        envelope.addMapping(NAMESPACE, WsSyncFile.class.getSimpleName(), WsSyncFile.class);
        envelope.addMapping(NAMESPACE, WsPhyFileInfo.class.getSimpleName(), WsPhyFileInfo.class);
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, SyncFileBlock.class.getSimpleName(), SyncFileBlock.class);
        // Set MARSHALLING type
        Marshal base64Marshal = new MarshalBase64();//MarshalFloat();
        base64Marshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);

            final SoapObject resp = (SoapObject) envelope.bodyIn;
            // Process return data
            // Fetch operation result
            result = Integer.valueOf(resp.getProperty("DownloadFileResult").toString());
            if (result == WsResultType.Success) {
                SoapObject r_file = (SoapObject)resp.getProperty("file");
                if (r_file != null) {
                    SoapObject syncBlock = (SoapObject)r_file.getProperty("Blocks");
                    String base64_stream = syncBlock.getProperty("UpdateData").toString();
                    if (base64_stream != "") {
                        byte[] bloc = Base64.decode(base64_stream);
                        file.Blocks.UpdateData = bloc;
                    }
                    file.IsFinally = Boolean.parseBoolean(r_file.getPropertyAsString("IsFinally"));
                }
            }
            Log.d(TAG, "DownloadFile result : " + result);
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }
        return result;
    }

    /**
     * Pull disk list from web server. Disk as root folder on server.
     *
     * @param folderList    WsFolder list used to store server return result
     * @return
     *  {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int getDisk(List<WsFolder> folderList) {
        int result = WsResultType.Success;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_GETDISK;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_GETDISK);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        // Add parameters

        // Set Mapping
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, WsFolder.class.getSimpleName(), WsFolder.class);
        envelope.addMapping(NAMESPACE, WsSpaceSizer.class.getSimpleName(), WsSpaceSizer.class);
        envelope.addMapping(NAMESPACE, WsPermission.class.getSimpleName(), WsPermission.class);

        // Set MARSHALLING:
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            // Convert return object to local entity
            result = Integer.valueOf(resp.getProperty("GetDiskResult").toString());
            if (result == WsResultType.Success) {
                SoapObject disks = (SoapObject)resp.getProperty("disks");
                for (int i = 0; i < disks.getPropertyCount(); i++) {
                    SoapObject folder = (SoapObject)disks.getProperty(i);
                    WsFolder r_folder = new WsFolder();
                    r_folder.ID = folder.getProperty("ID").toString();
                    r_folder.Name = folder.getProperty("Name").toString();
                    folderList.add(r_folder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Pull the folder information by the folder.ID from web server. Save the
     * information to folder.
     *
     * @param folder    The folder object stored the target folder guid and store the
     * folder information when server return query result.
     * @return {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int getFolderInfo(WsFolder folder) {
        int result = WsResultType.Success;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_GETFOLDERINFO;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_GETFOLDERINFO);
        // add web service method parameter
        PropertyInfo p_folderInfo = new PropertyInfo();
        p_folderInfo.setType(WsFolder.class);
        p_folderInfo.setName("folder");
        p_folderInfo.setValue(folder);
        rpc.addProperty(p_folderInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        // Set Mapping
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, WsFolder.class.getSimpleName(), WsFolder.class);
        envelope.addMapping(NAMESPACE, WsSpaceSizer.class.getSimpleName(), WsSpaceSizer.class);
        envelope.addMapping(NAMESPACE, WsPermission.class.getSimpleName(), WsPermission.class);
        envelope.addMapping(NAMESPACE, FileInfo.class.getSimpleName(), FileInfo.class);

        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            // Get webservice return object
            final SoapObject resp= (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getPropertyAsString("GetFolderInfoResult"));
            if (result == WsResultType.Success) {
                // Parse folder result
                if (folder != null) {
                    SoapObject x_folder = (SoapObject)resp.getProperty("folder");
                    // Save the folder name
                    folder.Name = x_folder.getPropertyAsString("Name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }
        return result;
    }

    /**
     * Pull the parent's sub folder and file list from web server
     *
     * @param folderList    the parent folder's sub folders list
     * @param fileList    the parent folder's sub files list
     * @param folder    the parent folder info
     * @return {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int getFolderList(WsFolder folder, ArrayList<WsFile> fileList,
                             ArrayList<WsFolder> folderList) {
        int result = WsResultType.Success;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_GETFOLDERLIST;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_GETFOLDERLIST);
        // add web service method parameter
        PropertyInfo p_folderInfo = new PropertyInfo();
        p_folderInfo.setType(WsFolder.class);
        p_folderInfo.setName("folder");
        p_folderInfo.setValue(folder);
        rpc.addProperty(p_folderInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        // Set Mapping
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, WsFolder.class.getSimpleName(), WsFolder.class);
        envelope.addMapping(NAMESPACE, WsSpaceSizer.class.getSimpleName(), WsSpaceSizer.class);
        envelope.addMapping(NAMESPACE, WsPermission.class.getSimpleName(), WsPermission.class);
        envelope.addMapping(NAMESPACE, FileInfo.class.getSimpleName(), FileInfo.class);

        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            // Get webservice return object
            final SoapObject resp= (SoapObject) envelope.bodyIn;
            result = Integer.valueOf(resp.getProperty("GetFolderListResult").toString());
            if (result == WsResultType.Success) {
                // Parse file list result
                if (fileList != null) {
                    SoapObject x_fileArr = (SoapObject)resp.getProperty("files");
                    for(int i = 0; i < x_fileArr.getPropertyCount(); i++) {
                        WsFile r_file = new WsFile();
                        SoapObject x_file = (SoapObject)x_fileArr.getProperty(i);
                        r_file.Extend = x_file.getPropertyAsString("Extend");
                        r_file.FullName = x_file.getPropertyAsString("FullName");
                        r_file.Name = x_file.getPropertyAsString("Name");
                        r_file.SizeB = Long.valueOf(x_file.getPropertyAsString("UsedSpaceSizeKB"));
                        r_file.ID = x_file.getPropertyAsString("ID");
                        r_file.MD5 = "";//x_file.getPropertyAsString("MD5");
                        fileList.add(r_file);
                    }
                }
                // Parse folder list result
                if (folderList != null) {
                    SoapObject x_folderArr = (SoapObject)resp.getProperty("folders");
                    for(int i = 0; i < x_folderArr.getPropertyCount(); i++) {
                        WsFolder r_folder = new WsFolder();
                        SoapObject x_folder = (SoapObject)x_folderArr.getProperty(i);
                        r_folder.ID = x_folder.getPropertyAsString("ID");
                        r_folder.Name = x_folder.getPropertyAsString("Name");
                        folderList.add(r_folder);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }
        return result;
    }



    /**
     * Before call this function, need make sure the target file already call
     * checkedFileInfo function to create the record information on server. This
     * function will upload the byte array stored in wsSyncFile to server.
     *
     * @param wsSyncFile    Store the target file information, The guid of the file
     * record on server and the byte array of the part file.
     * @return
     */
    public int uploadFile(WsSyncFile wsSyncFile) {
        int result;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_UPLOADFILE;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_UPLOADFILE);
        // add web service method parameter
        PropertyInfo p_fileInfo = new PropertyInfo();
        p_fileInfo.setName("file");
        p_fileInfo.setValue(wsSyncFile);
        p_fileInfo.setType(WsSyncFile.class);
        rpc.addPropertyIfValue(p_fileInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        envelope.addMapping(NAMESPACE, WsSyncFile.class.getSimpleName(), WsSyncFile.class);
        envelope.addMapping(NAMESPACE, WsPhyFileInfo.class.getSimpleName(), WsPhyFileInfo.class);
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, SyncFileBlock.class.getSimpleName(), SyncFileBlock.class);
        // Set MARSHALLING type
        Marshal base64Marshal = new MarshalBase64();//MarshalFloat();
        base64Marshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        //transport.debug = true;

        // Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // Call WS
        try {
            transport.call(soapAction, envelope, paraHttpHeaders);
            //Log.d(TAG, "Request: \n" + transport.requestDump);
            //Log.d(TAG, "Response: \n" + transport.responseDump);

            final SoapObject resp = (SoapObject) envelope.bodyIn;
            // Process return data
            // Fetch operation result
            result = Integer.valueOf(resp.getProperty("UploadFileResult").toString());
            Log.d(TAG, "UploadFile 100KB result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }

        return result;
    }

    /**
     * Call this function to login in on server.
     *
     * @param username    The user name.(email address)
     * @param password    The user password. Now transfer plain text, after sso
     * completed, need convert to MD5 value.
     * @param userInfo    Store the user information return from the remote server
     * @return
     */
    public int userLogin(String username, String password, WsGuidOwner userInfo) {
        int result;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_USERLOGIN;//"http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_USERLOGIN);
        // add web service method parameter
        rpc.addProperty("user", username);
        rpc.addProperty("passwordMd5", password);
        PropertyInfo p_userInfo = new PropertyInfo();
        p_userInfo.setName("userInfo");
        p_userInfo.setValue(userInfo);
        p_userInfo.setType(WsGuidOwner.class);
        rpc.addPropertyIfValue(p_userInfo);

        // Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);
        // Set Mapping
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        // Set MARSHALLING type
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // Set http header cookies values before call WS(null)
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();

        // Call WS, store the return http header
        // Store http header values after call WS
        List resultHttpHeaderList = null;
        try {
            resultHttpHeaderList = transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // Process return data
            final SoapObject resp = (SoapObject) envelope.bodyIn;
            // Fetch operation result
            result = Integer.valueOf(resp.getProperty("UserLoginResult").toString());

            if (result == LoginResultType.Success) {
                // Fetch session id
                for (Object o : resultHttpHeaderList) {
                    HeaderProperty p = (HeaderProperty)o;
                    if (p.getKey()!=null && p.getKey().equals("Set-Cookie")) {
                        Log.d(TAG, "key: " + p.getKey() + "\t" + "values:" + p.getValue());
                        session_id = p.getValue();
                        break;
                    }
                }
                // Fetch user info
                SoapObject x_user = (SoapObject)resp.getProperty("userInfo");
                userInfo.ID = x_user.getPropertyAsString("ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }
        return result;
    }


    /* #########################################Web Service API Wrapper########################## */

    /**
     * When user select upload file is a folder, call createFolderUpload_wrapper and
     * set UploadFile as a parameter, this function convert UploadFile to WsFolder,
     * then call createFolder(WsFolder) do real create action. If create success, will
     * fill up UploadFile.remote_id
     * @param file
     * @return
     */
    public int createFolderUpload_wrapper(UploadFile file) {
        int result;
        WsFolder wsFolder = new WsFolder();
        wsFolder.FatherID = file.getRemote_parent_id();
        File r_file = new File(file.getFilePath());
        wsFolder.Name = r_file.getName();
        result = createFolder(wsFolder);
        if (result == WsResultType.Success) {
            file.setRemote_id(wsFolder.ID);
        }
        return result;
    }

    public int createFolderCloud_wrapper(CloudFile folder) {
        int result = WsResultType.Success;
        WsFolder wsFolder = new WsFolder();
        wsFolder.Name = folder.getFilePath();
        wsFolder.FatherID = folder.getRemote_parent_id();
        result = createFolder(wsFolder);
        if (result == WsResultType.Success) {
            folder.setRemote_id(wsFolder.ID);
        }
        return result;
    }

    public int checkedFileInfo_wrapper(UploadFile file) {
        int result = -1;
        WsFile wsFile = new WsFile();
        String path = file.getFilePath();
        File r_file = new File(path);
        wsFile.CreaterID = AppConfigs.current_remote_user_id;
        wsFile.FatherID = file.getRemote_parent_id();
        wsFile.Extend = path.substring(path.lastIndexOf(".") + 1);
        wsFile.SizeB = r_file.length();
        wsFile.FullName = r_file.getName();
        wsFile.CreatDate = Misc.getDateString();
        try {
            wsFile.MD5 = FileManager.generateMD5(new FileInputStream(r_file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        result = checkedFileInfo(wsFile);
        // Update UploadFile.remote_id
        if (result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD) {
            file.setRemote_id(wsFile.ID);
            file.setState(UploadFileState.UPLOADED);
            file.setChangedSize(file.getFileSize());
        } else if (result == CheckedFileInfoResultType.RESULT_CHECK_SUCCESS) {
            file.setRemote_id(wsFile.ID);
            file.setState(UploadFileState.WAIT_UPLOAD);
        }
        return result;
    }

    /**
     * Wrapper getFolderList(Web Service Interface)
     * Convert WsFolder to CloudFile
     * @param file The CloudFile object, include target parent remote folder information
     * @param fileList The sub files(CloudFile) object list
     * @param folderList The sub folders(CloudFile) object list
     * @return int, the getFolderList execute result
     * {@link codingpark.net.cheesecloud.enumr.WsResultType}
     */
    public int getFolderList_wrapper(CloudFile file, ArrayList<CloudFile> fileList, ArrayList<CloudFile> folderList) {
        int result = WsResultType.Success;
        ArrayList<WsFolder> r_wsFolderList = new ArrayList<WsFolder>();
        ArrayList<WsFile> r_wsFileList = new ArrayList<WsFile>();
        WsFolder wsFolder = new WsFolder();
        wsFolder.ID = file.getRemote_id();
        result = ClientWS.getInstance(mContext).getFolderList(wsFolder, r_wsFileList, r_wsFolderList);
        if (result == WsResultType.Success) {
            if (folderList != null) {
                for (WsFolder tmp_folder : r_wsFolderList) {
                    CloudFile f = new CloudFile();
                    f.setFileType(CloudFileType.TYPE_FOLDER);
                    f.setRemote_id(tmp_folder.ID);
                    f.setFilePath(tmp_folder.Name);
                    folderList.add(f);
                }
            }

            if (fileList != null) {
                for (WsFile tmp_file : r_wsFileList) {
                    CloudFile f = new CloudFile();
                    f.setFileType(CloudFileType.TYPE_FILE);
                    f.setRemote_id(tmp_file.ID);
                    f.setFilePath(tmp_file.FullName);
                    f.setFileSize(tmp_file.SizeB);
                    f.setMd5(tmp_file.MD5);
                    fileList.add(f);
                }
            }
        }
        return result;
    }

    public int downloadFile_wrapper(DownloadFile file, byte[] data, int count) {
        int result = WsResultType.Success;
        WsSyncFile syncFile = new WsSyncFile();
        result = downloadFile(syncFile);
        return result;
    }

    /* ###########################################Unit Test###################################### */
    private void test_userLogin() {
        WsGuidOwner owner = new WsGuidOwner();
        //owner.CreateDate = "2014-10-17 16:44:23";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            userLogin("mrmsadmin@cheese.com", "cheese", owner);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void test_checkedFileInfo(String path) {
        WsFile ws_file = new WsFile();
        File file = new File(path);
        if (file.exists()) {
            ws_file.CreaterID = "395ED821-E528-42F0-8EA7-C59F258E7435";
            ws_file.FatherID = "395ED821-E528-42F0-8EA7-C59F258E7435";
            ws_file.Extend = path.substring(path.lastIndexOf(".") + 1);
            ws_file.SizeB = file.length();
            ws_file.FullName = file.getName();
            ws_file.CreatDate = "2014/10/17 16:44:23";
            try {
                ws_file.MD5 = FileManager.generateMD5(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "File: " + path + " \t" + " not exist!");
        }
        checkedFileInfo(ws_file);
    }

    private void test_uploadFile(String path) {
        File file = new File(path);
        Log.d(TAG, "filesize: " + file.length());
        //long cutLength = 1446;
        WsSyncFile ws_file = new WsSyncFile();
        ws_file.ID = s_id;
        ws_file.IsFinally = true;

        ws_file.Blocks = new SyncFileBlock();
        ws_file.Blocks.OffSet = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] cache = new byte[(int)file.length()];
            fis.read(cache, 0, (int)file.length());
            ws_file.Blocks.UpdateData = cache;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadFile(ws_file);
    }

    private void test_getDisk() {
        getDisk(new ArrayList<WsFolder>());
    }

    private void test_getFolderList() {
        WsFolder folder = new WsFolder();
        folder.FatherID = "395ED821-E528-42F0-8EA7-C59F258E7435";
        folder.ID = "395ED821-E528-42F0-8EA7-C59F258E7435";
        getFolderList(folder, null, null);
    }

    private void test_createFolder(String path) {
        WsFolder folder = new WsFolder();
        File file = new File(path);
        if (file.exists()) {
            folder.FatherID = "395ED821-E528-42F0-8EA7-C59F258E7435";
            //folder.ID = "395ED821-E528-42F0-8EA7-C59F258E7435";
            folder.Name = file.getName();
        } else {
            Log.d(TAG, "File: " + path + " \t" + " not exist!");
        }
        createFolder(folder);
    }

    public int renameObj_wrapper(CloudFile file) {
        int result = WsResultType.Success;
        result =renameObj(file.getRemote_id(),
                file.getFileType()== CloudFileType.TYPE_FILE ? "file" : "folder", file.getFilePath());
        return result;
    }

}
