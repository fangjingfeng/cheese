package codingpark.net.testmediastore;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GalleryActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = GalleryActivity.class.getSimpleName();
    /**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;

    private GridView gridView;
    private ArrayList<HashMap<String, String>> list;
    private ContentResolver cr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        findViews();
        /*
        getLoaderManager().initLoader(0, null, this);
        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.IMAGE_ID,
        };
        // Create the cursor pointing to the SDCard
        // MediaStore.Images.Media
        cursor = managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID);
        // Get the column index of the Thumbnails Image ID
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

        GridView sdcardImages = (GridView) findViewById(R.id.sdcard);
        sdcardImages.setAdapter(new ImageAdapter(this));

        // Set up a click listener
        sdcardImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Get the data location of the image
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, // Which columns to return
                        null,       // Return all rows
                        null,
                        null);
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToPosition(position);
                // Get image filename
                String imagePath = cursor.getString(columnIndex);
                // Use this path to do further processing, i.e. full screen display
            }
        });
        */
    }

    private void findViews() {
        gridView = (GridView) findViewById(R.id.gridview);
        list = new ArrayList<HashMap<String, String>>();
        cr = getContentResolver();
        String[] projection = { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA };
        Cursor cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getColumnData(cursor);

        String[] from = { "path" };
        int[] to = { R.id.imageView };
        ListAdapter adapter = new GridAdapter(this, list, R.layout.item, from,
                to);
        gridView.setAdapter(adapter);
        //gridView.setOnItemClickListener(listener);

    }

    /*
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            String image_id = list.get(position).get("image_id");
            Log.i(TAG, "---(^o^)----" + image_id);
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
            Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Images.Media._ID + "=" + image_id, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                Intent intent = new Intent(GalleryActivity.this,
                        ImageViewer.class);
                intent.putExtra("path", path);
                startActivity(intent);
            } else {
                Toast.makeText(ThumbnailActivity.this, "Image doesn't exist!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };
    */

    private void getColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                // Do something with the values.
                Log.i(TAG, _id + " image_id:" + image_id + " path:"
                        + image_path + "---");
                HashMap hash = new HashMap();
                hash.put("image_id", image_id + "");
                hash.put("path", image_path);
                list.add(hash);

            } while (cur.moveToNext());

        }
    }

    class GridAdapter extends SimpleAdapter {

        public GridAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource,
                           String[] from, int[] to) {
            super(context, data, resource, from, to);
            // TODO Auto-generated constructor stub
        }

        // set the imageView using the path of image
        public void setViewImage(ImageView v, String value) {
            try {
                Bitmap bitmap = null;
                if (value != null)
                    bitmap = BitmapFactory.decodeFile(value);
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
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


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Thumbnails._ID};
        return new CursorLoader(this,
                uri,
                projection,
                null,
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID) ;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private class ImageCursorAdapter extends SimpleCursorAdapter {

        public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }
    }



    /**
     * Adapter for our image files.
     */
    private class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            return cursor.getCount();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView: " + position);
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                // Move cursor to current position
                cursor.moveToPosition(position);
                // Get the current value for the requested column
                int imageID = cursor.getInt(columnIndex);
                // Set the content of the image based on the provided URI
                picturesView.setImageURI(Uri.withAppendedPath(
                        MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI, "" + imageID));
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setPadding(8, 8, 8, 8);
                picturesView.setLayoutParams(new GridView.LayoutParams(100, 100));
            }
            else {
                picturesView = (ImageView)convertView;
            }
            return picturesView;
        }
    }
}
