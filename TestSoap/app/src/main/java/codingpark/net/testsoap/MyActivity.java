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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MyActivity extends Activity {
    private static final String TAG     = "MyActivity";

    private Button ok_bt        = null;
    private EditText et1        = null;
    private EditText et2        = null;

    private Handler handler     = null;

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
                        callWS_Test();
                    }
                });
                soapT.start();

            }
        });
    }

    private void callWS_Test() {
        // Namespace
        String NAMESPACE = "http://tempuri.org/";
        // Call web service method name
        String METHODNAME = "Test";
        // EndPoint
        String ENDPOINT = "http://192.168.0.101:22332/ClientWS.asmx";
        // SOAP Action
        String soapAction = "http://tempuri.org/Test";

        // Initial SoapObject
        SoapObject rpc = new SoapObject(NAMESPACE, METHODNAME);

        // add web service method parameter
        //rpc.addProperty("userName", "mrmsadmin@cheese.com");

        // Create soap request object with soap version
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        // Initial envelope's SoapObject
        envelope.bodyOut = rpc;
        // Initial web service implements technology
        envelope.dotNet = true;
        //
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(ENDPOINT);
        try {
            // Call web service
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get webservice return object
        final SoapObject object = (SoapObject) envelope.bodyIn;
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
