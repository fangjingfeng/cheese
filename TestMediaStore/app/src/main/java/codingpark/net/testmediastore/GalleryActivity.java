package codingpark.net.testmediastore;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GalleryActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = GalleryActivity.class.getSimpleName();
    /**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor           = null;
    private ContentResolver cr      = null;

    private String[] image_projection = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DESCRIPTION,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.MINI_THUMB_MAGIC
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_upload_image_category_layout);

        cr = getContentResolver();

        getLoaderManager().initLoader(0, null, this);
        //this.setListAdapter(new GridAdapter(this, null, 0, null, null));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


    class GridAdapter extends SimpleAdapter {
        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        public GridAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        /*
        public GridAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource,
                           String[] from, int[] to) {
            super(context, data, resource, from, to);
            // TODO Auto-generated constructor stub
        }
        */

        // set the imageView using the path of image
        public void setViewImage(ImageView v, String value) {
            try {
                Bitmap bitmap = null;
                if (value != null) {
                    bitmap = BitmapFactory.decodeFile(value);
                }
                if (bitmap != null) {
                    Bitmap newBit = Bitmap
                            .createScaledBitmap(bitmap, 100, 80, true);
                    v.setImageBitmap(newBit);
                }
            } catch (NumberFormatException nfe) {
                v.setImageURI(Uri.parse(value));
            }
        }
    }

    private static final class ViewHolder {
        public ImageView bucketThumbView    = null;
        public TextView bucketNameView      = null;
        public TextView countView           = null;
    }

    private class ImageCategoryAdapter extends CursorAdapter {
        private LayoutInflater inflater     = null;
        public ImageCategoryAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            inflater = (LayoutInflater)GalleryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = inflater.inflate(R.layout.select_upload_image_fragment_item, parent);
            ViewHolder holder = new ViewHolder();
            holder.bucketThumbView = (ImageView)view.findViewById(R.id.bucketImageView);
            holder.bucketNameView = (TextView)view.findViewById(R.id.bucketNameView);
            holder.countView = (TextView)view.findViewById(R.id.countTextView);
            view.setTag(holder);
            initUI(holder, cursor);
            return view;
        }

        private void initUI(ViewHolder holder, Cursor cursor) {

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // CursorAdapter already help us check the view is null
            ViewHolder holder = (ViewHolder)view.getTag();
            initUI(holder, cursor);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar select_upload_image_fragment_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(this,
                uri,
                image_projection,
                null,
                null,
                MediaStore.Images.Media.BUCKET_ID) ;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (data != null)  {
            while(data.moveToNext()) {
                Log.d(TAG, "###################################################3");
                Log.d(TAG, "ID: " + data.getInt(0));
                Log.d(TAG, "DATA: " + data.getString(1));
                Log.d(TAG, "DATE_TAKEN: " + data.getInt(2));
                Log.d(TAG, "DISPLAY_NAME: " + data.getString(3));
                Log.d(TAG, "DESCRIPTION: " + data.getString(4));
                Log.d(TAG, "BUCKET_DISPLAY_NAME: " + data.getString(5));
                Log.d(TAG, "BUCKET_ID: " + data.getInt(6));
                Log.d(TAG, "MINI_THUMB_MAGIC: " + data.getInt(7));
                Log.d(TAG, "###################################################3");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
