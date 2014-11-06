package codingpark.net.cheesecloud.view;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import codingpark.net.cheesecloud.R;

public class CloudFilesActivity extends ListActivity {
    private static final String TAG         = CloudFilesActivity.class.getSimpleName();

    public static final String LIST_MODE_KEY        = "list_mode_key";
    /**
     * List user owned cloud folder files
     */
    public static final int MY_CLOUD_LIST_MODE      = 0;
    /**
     * List cloud shared disks
     */
    public static final int RESOURCELIB_LIST_MODE   = 1;
    /**
     * Current list mode
     */
    private static int mListMode            = MY_CLOUD_LIST_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        // 1. Show back arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the initial list mode
        Intent recIntent = getIntent();
        mListMode = recIntent.getIntExtra(LIST_MODE_KEY, MY_CLOUD_LIST_MODE);

        setContentView(R.layout.activity_cloud_files);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_files, menu);
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

        // Handle action bar event
        // 1. R.id.home: Action Bar up button clicked
        switch (id) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    //private class CloudListAdapter extends ArrayAdapter<Download>
}
