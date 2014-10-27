package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
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
import codingpark.net.cheesecloud.eumn.CheckedFileInfoType;
import codingpark.net.cheesecloud.eumn.LoginResultType;
import codingpark.net.cheesecloud.eumn.WsResultType;
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
 * The class singleton pattern, used to support Web Service
 * function interface
 */
public final class ClientWS {
    private static final String TAG     = "ClientWS";

    private static ClientWS client      = null;

    // Web Services method name
    public static final String METHOD_USERLOGIN             = "UserLogin";
    public static final String METHOD_CHECKEDFILEINFO       = "CheckedFileInfo";
    public static final String METHOD_UPLOADFILE            = "UploadFile";
    public static final String METHOD_GETDISK               = "GetDisk";
    public static final String METHOD_GETFOLDERLIST         = "GetFolderList";
    public static final String METHOD_CREATEFOLDER          = "CreateFolder";

    // Default server info
    public static final String DEFAULT_ENDPOINT             = "http://192.168.0.101:22332/ClientWS.asmx";

    // Web services server configurations
    // Namespace
    private String NAMESPACE        = "http://tempuri.org/";
    // EndPoint
    private String mEndPoint        = "http://192.168.0.108:22332/ClientWS.asmx";

    private static String session_id            = "";
    private static WsFile s_file                = null;
    private static String s_id                  = "";

    private static Context mContext             = null;

    private ClientWS() {
        SharedPreferences prefs = mContext.getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);
        mEndPoint = prefs.getString(AppConfigs.SERVER_ADDRESS, DEFAULT_ENDPOINT);
        //mEndPoint = "http://192.168.0.101:22332/ClientWS.asmx?wsdl";
    }

    public static ClientWS getInstance(Context context) {
        mContext = context;
        if (client == null)
            client = new ClientWS();
        return client;
    }

    public void setEndPoint(String url) {
        mEndPoint = url;
    }

    public void test_userLogin() {
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

    public int userLogin(String username, String password, WsGuidOwner userinfo) {
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
        p_userInfo.setValue(userinfo);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }
        return result;
    }


    public void test_checkedFileInfo(String path) {
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


    public int checkedFileInfo(WsFile wsFile) {
        int result;

        wsFile.CreaterID = "395ED821-E528-42F0-8EA7-C59F258E7435";
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
            if ((result == CheckedFileInfoType.RESULT_CHECK_SUCCESS)
                    || (result == CheckedFileInfoType.RESULT_QUICK_UPLOAD)) {
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

    public void test_uploadFile(String path) {
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
            result = Integer.valueOf(resp.getProperty("UploadFileResult").toString());
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }

        return result;
    }

    public void test_getDisk() {
        getDisk(new ArrayList<WsFolder>());

    }

    /**
     * Pull disk list from web server
     * @param list The list save WsFolder object
     * @return
     *  {@link codingpark.net.cheesecloud.eumn.WsResultType}
     */
    public int getDisk(List<WsFolder> list) {
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
                    list.add(r_folder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void test_getFolderList() {
        WsFolder folder = new WsFolder();
        folder.FatherID = "395ED821-E528-42F0-8EA7-C59F258E7435";
        folder.ID = "395ED821-E528-42F0-8EA7-C59F258E7435";
        getFolderList(folder, null, null);
    }

    /**
     * Pull the folder and file list from web server
     * @param folder the parent folder info
     * @param fileList the parent folder's sub files list
     * @param folderList the parent folder's sub folders list
     * @return {@link codingpark.net.cheesecloud.eumn.WsResultType}
     */
    public int getFolderList(WsFolder folder, ArrayList<WsFile> fileList,
                              ArrayList<WsFolder> folderList) {
        int result = WsResultType.Success;
        // 1. Create SOAP Action
        String soapAction = NAMESPACE + METHOD_GETFOLDERLIST;//"http://tempuri.org/Test";

        // 2. Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_GETFOLDERLIST);
        // add web service method parameter
        PropertyInfo p_folderInfo = new PropertyInfo();
        p_folderInfo.setType(WsFolder.class);
        p_folderInfo.setName("folder");
        p_folderInfo.setValue(folder);
        rpc.addProperty(p_folderInfo);

        // 3. Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // Initial envelope's SoapObject
        envelope.bodyOut = rpc;
        // Initial web service implements technology(.Net)
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        // Mapping
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        envelope.addMapping(NAMESPACE, WsFolder.class.getSimpleName(), WsFolder.class);
        envelope.addMapping(NAMESPACE, WsSpaceSizer.class.getSimpleName(), WsSpaceSizer.class);
        envelope.addMapping(NAMESPACE, WsPermission.class.getSimpleName(), WsPermission.class);
        envelope.addMapping(NAMESPACE, FileInfo.class.getSimpleName(), FileInfo.class);

        //---------------------------------------------------------------------------------------
        // MARSHALLING:
        //---------------------------------------------------------------------------------------
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // 4. Initial http transport
        HttpTransportSE transport = new HttpTransportSE(mEndPoint);
        transport.debug = true;

        // 5. Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // 6. Call WS, store the return http header
        // Store http header values after call WS
        List resultHttpHeaderList = null;
        try {
            resultHttpHeaderList = transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
            // 7. Process return data
            // Get webservice return object
            final SoapObject object = (SoapObject) envelope.bodyIn;
            // Convert return object to local entity
            Log.d(TAG, object.toString());
            Log.d(TAG, "************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            result = WsResultType.Faild;
        }
        return result;
    }

    public void test_createFolder(String path) {

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

    public int createFolder(WsFolder folder) {
        int result;
        // Create SOAP Action
        String soapAction = NAMESPACE + METHOD_CREATEFOLDER;

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_CREATEFOLDER);
        // add web service method parameter
        PropertyInfo p_folderInfo= new PropertyInfo();
        p_folderInfo.setName("folder");
        p_folderInfo.setValue(folder);
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
                folder.ID = r_folder.getProperty("ID").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WsResultType.Faild;
        }
        return result;
    }

}
