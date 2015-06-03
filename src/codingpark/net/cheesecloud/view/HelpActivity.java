package codingpark.net.cheesecloud.view;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import codingpark.net.cheesecloud.R;

/**
 * HelpActivity used to show application version and the company information. More, support
 * send email to the app developer and open company home page functions.
 */
public class HelpActivity extends Activity implements OnClickListener {
    // The app developer emails array
    private static final String[] EMAIL = {"shan0xiao0xi@163.com"};
    // Company web home page URL
    private static final String WEB = "http://codingpark.net/blog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help_layout);

        String text = this.getResources().getString(R.string.product_info);

        TextView label = (TextView)findViewById(R.id.help_top_label);
        label.setText(text);

        Button email = (Button)findViewById(R.id.help_email_bt);
        Button web = (Button)findViewById(R.id.help_website_bt);
        email.setOnClickListener(this);
        web.setOnClickListener(this);
    
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent i = new Intent();

        switch(id) {
            case R.id.help_email_bt:
                // Create intent to start system email application
                i.setAction(android.content.Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, EMAIL);
                try {
                    String title = getResources().getString(R.string.send_email_chooser_title);
                    startActivity(Intent.createChooser(i, title));
                } catch(ActivityNotFoundException e) {
                    Toast.makeText(this, "Sorry, could not start the email", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.help_website_bt:
                // Create intent to start system web browser
                i.setAction(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse(WEB));
                try {
                    startActivity(i);

                } catch(ActivityNotFoundException e) {
                    Toast.makeText(this, "Sorry, could not open the website", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
