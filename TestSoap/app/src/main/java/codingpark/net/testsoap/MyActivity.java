package codingpark.net.testsoap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {
    private static final String TAG     = "MyActivity";

    private Button ok_bt        = null;
    private EditText et1        = null;
    private EditText et2        = null;

    private Handler handler     = null;

    // Web services server configurations
    // Namespace
    String NAMESPACE = "http://tempuri.org/";
    // EndPoint
    String ENDPOINT = "http://192.168.0.108:22332/ClientWS.asmx";


    // Web Services method name
    public static final String METHOD_LOGIN     = "UserLogin";
    public static final String METHOD_TEST      = "Test";

    private static String session_id            = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        handler = new Handler();
        initUI();
        initHandler();
    }

    private void initUI() {
        ok_bt = (Button)findViewById(R.id.ok_bt);
        et1 = (EditText)findViewById(R.id.editText);
        et2 = (EditText)findViewById(R.id.editText2);
    }

    private void initHandler() {
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ok_bt clicked!!");
                // Call web service function
                Thread soapT = new Thread(new Runnable() {
                     @Override
                    public void run() {
                         callWS_Login("kkkk", "kkk");
                         callWS_Test();
                    }
                });
                soapT.start();

            }
        });
    }

    public int callWS_Login(String username, String password) {
        // 1. Create SOAP Action
        String soapAction = NAMESPACE + METHOD_LOGIN;//"http://tempuri.org/Test";

        // 2. Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_LOGIN);
        // add web service method parameter
        rpc.addProperty("user", username);
        rpc.addProperty("passwordMd5", password);
        //rpc.addProperty("userInfo", null);

        // 3. Initial envelope
        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // Initial envelope's SoapObject
        envelope.bodyOut = rpc;
        // Initial web service implements technology(.Net)
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        // 4. Initial http transport
        HttpTransportSE transport = new HttpTransportSE(ENDPOINT);

        // 5. Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        //paraHttpHeaders.add(new HeaderProperty("Cookie", "ASP.NET_SessionId=05mmnhopepf3pk10zkplh4mt"));

        // 6. Call WS, store the return http header
        // Store http header values after call WS
        List resultHttpHeaderList = null;
        try {
            resultHttpHeaderList = transport.call(soapAction, envelope, paraHttpHeaders);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. Process return data
        // Get webservice return object
        final SoapObject object = (SoapObject) envelope.bodyIn;
        // Convert return object to local entity
        //object.getProperty("key")
        //String result = object.getProperty(0).toString();
        Log.d(TAG, object.toString());
        // Print Login return http header key/values
        for (Object o : resultHttpHeaderList) {
            HeaderProperty p = (HeaderProperty)o;
            if (p.getKey()!=null && p.getKey().equals("Set-Cookie")) {
                Log.d(TAG, "key: " + p.getKey() + "\t" + "values:" + p.getValue());
                session_id = p.getValue();
            }
        }
        Log.d(TAG, "************************************************");
        // Refresh UI elements
        handler.post(new Runnable() {
            @Override
            public void run() {
                //et1.setText(object.getProperty("TestResult").toString());
            }
        });

        return 0;
    }

//    private class WsFile {
//        /// <summary>
//        /// 获取一个值，该值指示对象是否被删除
//        /// </summary>
//        private bool _isDeleted;
//
//        /// <summary>
//        /// 获取对象的GUID
//        /// </summary>
//        private Guid _guid;
//
//
//        /// <summary>
//        /// 已经使用空间大小
//        /// </summary>
//        private long _usedSpaceSizeKB;
//
//        /// <summary>
//        /// 获取一个值，该值指示对象是否被回收
//        /// </summary>
//        private bool _isRecycled;
//
//        private string _expName;
//        #endregion
//
//        #region 属性
//        /// <summary>
//        /// 获取或设置是否为预览文件
//        /// </summary>
//        public bool IsPreview { get; set; }
//        /// <summary>
//        /// 获取或设置逻辑文件的全名
//        /// </summary>
//        public string FullName { get; set; }
//
//        /// <summary>
//        /// 获取逻辑文件的扩展文件名（不含“.”）
//        /// </summary>
//        public string ExpName { get { return (string.IsNullOrEmpty(_expName) && FullName != null) ? FullName.Substring(FullName.LastIndexOf('.') + 1, FullName.Length - FullName.LastIndexOf('.') - 1) : _expName; } set { _expName = value; } }
//
//        /// <summary>
//        /// 获取或设置逻辑文件所在的逻辑文件夹ID
//        /// </summary>
//        public string FatherID { get; set; }
//
//        /// <summary>
//        /// 获取或设置该实例的创建日期时间
//        /// </summary>
//        public System.DateTime CreatDate { get; set; }
//
//        /// <summary>
//        /// 获取或设置文件扩展名
//        /// </summary>
//        public string Extend { get { return (string.IsNullOrEmpty(_expName) && FullName != null) ? FullName.Substring(FullName.LastIndexOf('.') + 1, FullName.Length - FullName.LastIndexOf('.') - 1) : _expName; } set { _expName = value; } }
//
//        /// <summary>
//        /// 获取或设置该逻辑文件的创建者（用户）的ID
//        /// </summary>
//        public string CreaterID { get; set; }
//
//        /// <summary>
//        /// 获取或设置该文件对应的物理信息
//        /// </summary>
//        public Cheese.MRMSweb.Base.Store.FileInfo PhyInfo { get; set; }
//
//        /// <summary>
//        /// 获取或设置 ，该值指示本实例是否显示在逻辑文件列表中
//        /// </summary>
//        public bool IsShow { get; set; }
//
//        /// <summary>
//        /// 获取一个值，该值指示对象是否被删除
//        /// </summary>
//        bool IsDeleted
//        {
//            get { return _isDeleted; }
//        }
//
//        /// <summary>
//        /// 获取对象的GUID
//        /// </summary>
//        public Guid ID { get; set; }
//
//        /// <summary>
//        /// 获取或设置名称(备注)
//        /// </summary>
//        public string Name { get; set; }
//
//        /// <summary>
//        /// 获取或设置实例所使用的存储空间容量（KB）
//        /// </summary>
//        public long UsedSpaceSizeKB { get; set; }
//
//
//        /// <summary>
//        /// 获取或设置对象被删除的日期时间
//        /// </summary>
//        public DateTime RecycleDate { get; set; }
//
//        /// <summary>
//        /// 获取或设置该对象的删除者（用户）的相关信息
//        /// </summary>
//        public WsGuidOwner Recycler { get; set; }
//
//        /// <summary>
//        /// 获取一个值，该值指示对象是否被回收
//        /// </summary>
//        public bool IsRecycled
//        {
//            get { return _isRecycled; }
//        }
//        /// <summary>
//        /// 获取或设置转码类型
//        /// </summary>
//        public TransCodeType TranCodeType { get; set; }
//
//        /// <summary>
//        /// 下载次数
//        /// </summary>
//        public int DownCount { get; set; }
//        /// <summary>
//        /// 查看次数
//        /// </summary>
//        public int LookCount { get; set; }
//        /// <summary>
//        /// 文件实际大小
//        /// </summary>
//        public long SizeB { get; set; }
//        /// <summary>
//        /// 文件的MD5码
//        /// </summary>
//        public string MD5 { get; set; }
//    }

    private void start_uploading(String path) {

    }



    //private void callWS_Check

    private void callWS_UploadFile() {

    }

    private void callWS_Test() {
        // Namespace
        String NAMESPACE = "http://tempuri.org/";
        // Call web service method name
        String METHODNAME = "Test";
        // EndPoint
        String ENDPOINT = "http://192.168.0.108:22332/ClientWS.asmx";
        // SOAP Action
        String soapAction = "http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHODNAME);

        // add web service method parameter
        //rpc.addProperty("userName", "mrmsadmin@cheese.com");

        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);

        // Initial envelope's SoapObject
        envelope.bodyOut = rpc;
        // Initial web service implements technology
        envelope.dotNet = true;
        //
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(ENDPOINT);

        // 5. Set http header cookies values before call WS
        List<HeaderProperty> paraHttpHeaders = new ArrayList<HeaderProperty>();
        paraHttpHeaders.add(new HeaderProperty("Cookie", session_id));

        // 6. Call WS, store the return http header
        // Store http header values after call WS
        List resultHttpHeaderList = null;
        try {
            resultHttpHeaderList = transport.call(soapAction, envelope, paraHttpHeaders);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get webservice return object
        final SoapObject object = (SoapObject) envelope.bodyIn;
        for (Object o : resultHttpHeaderList) {
            HeaderProperty p = (HeaderProperty)o;
            Log.d(TAG, "key: " + p.getKey() + "\t" + "values:" + p.getValue());
        }
        // Convert return object to local entity
        //String result = object.getProperty(0).toString();
        Log.d(TAG, object.toString());
        // Refresh UI elements
        //et1.setText(object.toString());
        handler.post(new Runnable() {
            @Override
            public void run() {
                et1.setText(object.getProperty("TestResult").toString());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
