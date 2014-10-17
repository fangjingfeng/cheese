package codingpark.net.cheesecloud.handle;

import android.graphics.Path;
import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.wsi.FileInfo;
import codingpark.net.cheesecloud.wsi.WsFile;
import codingpark.net.cheesecloud.wsi.WsGuidOwner;

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


    // Web services server configurations
    // Namespace
    String NAMESPACE        = "http://tempuri.org/";
    // EndPoint
    String ENDPOINT         = "http://192.168.0.108:22332/ClientWS.asmx";

    private static String session_id            = "";

    private ClientWS() {
    }

    public static ClientWS getInstance() {
        if (client == null)
            client = new ClientWS();
        return client;
    }

    public void test_userLogin() {
        WsGuidOwner owner = new WsGuidOwner();
        owner.CreateDate = "2014-10-17 16:44:23";

        userLogin("mrmsadmin@cheese.com", "cheese", owner);

    }

    public void userLogin(String username, String password, WsGuidOwner userinfo) {
        // 1. Create SOAP Action
        String soapAction = NAMESPACE + METHOD_USERLOGIN;//"http://tempuri.org/Test";

        // 2. Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_USERLOGIN);
        // add web service method parameter
        rpc.addProperty("user", username);
        rpc.addProperty("passwordMd5", password);
        //rpc.addProperty("userInfo", userinfo);

        PropertyInfo p_userInfo = new PropertyInfo();
        p_userInfo.setName("userInfo");
        p_userInfo.setValue(userinfo);
        p_userInfo.setType(WsGuidOwner.class);
        rpc.addPropertyIfValue(p_userInfo);

        // 3. Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // Initial envelope's SoapObject
        envelope.bodyOut = rpc;
        // Initial web service implements technology(.Net)
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);
        //---------------------------------------------------------------------------------------
        // MARSHALLING:
        //---------------------------------------------------------------------------------------
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // 4. Initial http transport
        HttpTransportSE transport = new HttpTransportSE(ENDPOINT);
        transport.debug = true;

        // 5. Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();

        // 6. Call WS, store the return http header
        // Store http header values after call WS
        List resultHttpHeaderList = null;
        try {
            resultHttpHeaderList = transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. Process return data
        // Get webservice return object
        final SoapObject object = (SoapObject) envelope.bodyIn;
        // Convert return object to local entity
        //Log.d(TAG, object.toString());
        // Print Login return http header key/values
        for (Object o : resultHttpHeaderList) {
            HeaderProperty p = (HeaderProperty)o;
            if (p.getKey()!=null && p.getKey().equals("Set-Cookie")) {
                Log.d(TAG, "key: " + p.getKey() + "\t" + "values:" + p.getValue());
                session_id = p.getValue();
            }
        }
        Log.d(TAG, "************************************************");
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

    public void checkedFileInfo(WsFile wsFile) {
        // 1. Create SOAP Action
        String soapAction = NAMESPACE + METHOD_CHECKEDFILEINFO;//"http://tempuri.org/Test";

        // 2. Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_CHECKEDFILEINFO);
        // add web service method parameter

        PropertyInfo p_fileInfo = new PropertyInfo();
        p_fileInfo.setName("file");
        p_fileInfo.setValue(wsFile);
        p_fileInfo.setType(WsFile.class);
        rpc.addPropertyIfValue(p_fileInfo);

        // 3. Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // Initial envelope's SoapObject
        envelope.bodyOut = rpc;
        // Initial web service implements technology(.Net)
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        envelope.addMapping(NAMESPACE, WsFile.class.getSimpleName(), WsFile.class);
        envelope.addMapping(NAMESPACE, FileInfo.class.getSimpleName(), FileInfo.class);
        envelope.addMapping(NAMESPACE, WsGuidOwner.class.getSimpleName(), WsGuidOwner.class);

        //---------------------------------------------------------------------------------------
        // MARSHALLING:
        //---------------------------------------------------------------------------------------
        Marshal floatMarshal = new MarshalFloat();
        floatMarshal.register(envelope);

        // 4. Initial http transport
        HttpTransportSE transport = new HttpTransportSE(ENDPOINT);
        transport.debug = true;

        // 5. Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();

        // 6. Call WS, store the return http header
        // Store http header values after call WS
        List resultHttpHeaderList = null;
        try {
            resultHttpHeaderList = transport.call(soapAction, envelope, paraHttpHeaders);
            Log.d(TAG, "Request: \n" + transport.requestDump);
            Log.d(TAG, "Response: \n" + transport.responseDump);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. Process return data
        // Get webservice return object
        final SoapObject object = (SoapObject) envelope.bodyIn;
        // Convert return object to local entity
        //Log.d(TAG, object.toString());
        // Print Login return http header key/values
        for (Object o : resultHttpHeaderList) {
            HeaderProperty p = (HeaderProperty)o;
            if (p.getKey()!=null && p.getKey().equals("Set-Cookie")) {
                Log.d(TAG, "key: " + p.getKey() + "\t" + "values:" + p.getValue());
                session_id = p.getValue();
            }
        }
        Log.d(TAG, "************************************************");
    }


    public void uploadFile() {

    }

}
