package codingpark.net.testmediastore;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
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
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.MINI_THUMB_MAGIC
    };

    /**
     * List the item of the selected category in by date order
     */
    public static final int ITEM_LIST_MODE = 0;
    /**
     * List the all category by category id
     */
    public static final int CATEGORY_LIST_MODE  = 1;
    /**
     * Current list mode
     * 0: {@see ITEM_LIST_MODE}
     * 1: {@see CATEGORY_LIST_MODE}
     */
    private int mListMode                       = CATEGORY_LIST_MODE;

    /**
     * Store the {@see CategoryItem} objects that query from
     * {@link android.provider.MediaStore.Images.Media}. This
     * list will show in CATEGORY_LIST_MODE as data of ListView.
     */
    private ArrayList<ItemImage> mCategoryList       = null;
    /**
     * Store the {@see ItemImage} objects that query from
     * {@link android.provider.MediaStore.Images}, the data will filtered
     * by BUCKET_ID, this list will show in ITEM_LIST_MODE as data of ListView.
     */
    private ArrayList<ItemImage> mSubItemList = null;
    /**
     * Store the all {@see ItemImage} objects that query from
     * {@link android.provider.MediaStore.Images}.
     */
    private ArrayList<ItemImage> mAllItemList = null;
    /**
     * The {@see CATEGORY_LIST_MODE} list view adapter
     */
    private ImageCategoryAdapter mCategoryAdapter           = null;
    /**
     * The {@see IMAGE_CATEGORY_LIST_MODE} list view adapter
     */
    private ImageItemAdapter mItemAdapter                   = null;
    /**
     * The LayoutInflater object, used by ArrayAdapter to inflate view from
     * layout xml file.
     */
    private LayoutInflater mInflater                        = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_upload_image_layout);

        cr = getContentResolver();
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Initial MediaStore query task
        getLoaderManager().initLoader(0, null, this);
        // Initial the two show mode data list
        mAllItemList = new ArrayList<ItemImage>();
        mCategoryList = new ArrayList<ItemImage>();
        mSubItemList = new ArrayList<ItemImage>();
        // Intial the two show mode data adapter
        mCategoryAdapter = new ImageCategoryAdapter(this, R.layout.select_upload_image_category_mode_item_layout, mCategoryList);
        mItemAdapter = new ImageItemAdapter(this, R.layout.select_upload_image_item_mode_item_layout, mSubItemList);
        // Set default list adapter to CATEGORY_LIST_MODE
        this.setListAdapter(mCategoryAdapter);
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar select_upload_image_category_mode_item_layout clicks here. The action bar will
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
        String order_clause = MediaStore.Images.Media.BUCKET_ID + " ASC, "
               + MediaStore.Images.Media.DATE_TAKEN + " ASC ";
        return new CursorLoader(this,
                uri,
                image_projection,
                null,
                null,
                order_clause) ;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (data != null)  {
            // Clear mAllItemList item
            mAllItemList.clear();
            // Traverse the cursor, store the data to mAllItemList
            while(data.moveToNext()) {
                Log.d(TAG, "###################################################3");
                Log.d(TAG, "ID: " + data.getInt(0));
                Log.d(TAG, "DATA: " + data.getString(1));
                Log.d(TAG, "DATE_TAKEN: " + data.getInt(2));
                Log.d(TAG, "BUCKET_DISPLAY_NAME: " + data.getString(3));
                Log.d(TAG, "BUCKET_ID: " + data.getInt(4));
                Log.d(TAG, "MINI_THUMB_MAGIC: " + data.getInt(5));
                Log.d(TAG, "###################################################3");
                // Judge the image is exist? If not exists, needn't add it to mAllItemList
                if (!(new File(data.getString(1)).exists())) {
                    continue;
                }
                ItemImage item =new ItemImage();
                item.id = data.getInt(0);
                item.data = data.getString(1);
                item.date_taken = data.getInt(2);
                item.bucket_display_name = data.getString(3);
                item.bucket_id = data.getInt(4);
                item.mini_thumb_magic = data.getInt(5);
                mAllItemList.add(item);
            }
            // Traverse mAllItemList, filter by bucket id, add unique bucket to mCategoryList
            mCategoryList.clear();
            if (mAllItemList.size() > 0) {
                ItemImage r_category = null;
                try {
                    r_category = (ItemImage)mAllItemList.get(0).clone();
                    r_category.item_count = 1;
                    int r_bucket_id = mAllItemList.get(0).bucket_id;
                    mCategoryList.add(r_category);
                    for (int i = 1; i < mAllItemList.size(); i++) {
                        if (mAllItemList.get(i).bucket_id == r_bucket_id) {
                            mCategoryList.get(mCategoryList.size() - 1).item_count++;
                        }
                        else {
                            r_category = (ItemImage)mAllItemList.get(i).clone();
                            r_category.item_count = 1;
                            r_bucket_id = r_category.bucket_id;
                            mCategoryList.add(r_category);
                        }
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            if (mListMode == CATEGORY_LIST_MODE) {
                getListView().deferNotifyDataSetChanged();
            }
        }
        for (int i = 0; i < mCategoryList.size(); i++) {
            getThumbPath(mCategoryList.get(i));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private long lastPhotoId        = 0;
    private String getThumbPath(ItemImage item) {
        String path = "";
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                cr, item.id,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String thumbPath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            File thumb = new File(thumbPath);
            if (thumb.exists())
                item.thumb_path = thumbPath;
            else
                item.thumb_path = "";
            Log.d(TAG, "getThumbPath: " + item.thumb_path);
        } else {
            if (lastPhotoId == item.id) {
                item.thumb_path = "";
                Log.d(TAG, "getThumbPath: " + "empty");
            } else {
                MediaStore.Images.Thumbnails.getThumbnail(cr,
                        item.id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                lastPhotoId = item.id;
                getThumbPath(item);
            }
        }
        return path;
    }

    private static final class ViewHolder {
        public ImageView bucketThumbView    = null;
        public TextView bucketNameView      = null;
        public TextView countView           = null;
    }

    /**
     * The {@see CATEGORY_LIST_MODE} adapter
     */
    private class ImageCategoryAdapter extends ArrayAdapter<ItemImage> {

        public ImageCategoryAdapter(Context context, int resource, List<ItemImage> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemImage item = mCategoryList.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.select_upload_image_category_mode_item_layout, null);
                holder.bucketThumbView = (ImageView)convertView.findViewById(R.id.bucketImageView);
                holder.bucketNameView = (TextView)convertView.findViewById(R.id.bucketNameView);
                holder.countView = (TextView)convertView.findViewById(R.id.countTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.bucketThumbView.setImageResource(R.drawable.ic_launcher);
            holder.bucketThumbView.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(cr, item.id, MediaStore.Images.Thumbnails.MICRO_KIND, null));
            holder.bucketNameView.setText(item.bucket_display_name);
            holder.countView.setText(item.item_count + "");
            return convertView;
        }
    }

    /**
     * The {@SEE ITEM_LIST_MODE} adapter
     */
    private class ImageItemAdapter extends ArrayAdapter<ItemImage> {

        public ImageItemAdapter(Context context, int resource, List<ItemImage> objects) {
            super(context, resource, objects);
        }
    }


    /**
     * Store the image item information that query from
     * {@link android.provider.MediaStore.Images.Media}
     */
    private static class ItemImage implements Cloneable{
        /**
         * Associate with _ID(index) field of the {@link android.provider.MediaStore.Images}
         */
        public int id           = -1;
        /**
         * Associate with DATA(origin image path) field of the {@link android.provider.MediaStore.Images}
         */
        public String data = "";
        /**
         * Associate with DATE_TAKEN(The take image date) filed of the {@link android.provider.MediaStore.Images}
         */
        public int date_taken    = 0;
        /**
         * Associate with BUCKET_DISPLAY_NAME(The category name) field of the
         * {@link android.provider.MediaStore.Images}
         */
        public String bucket_display_name       = "";
        /**
         * Associate with BUCKET_ID(The category id) field of the
         * {@link android.provider.MediaStore.Images}
         */
        public int bucket_id                    = -1;
        /**
         * Associate with MINI_THUMB_MAGIC(The thumbnails ID) field of the
         * {@link android.provider.MediaStore.Images}
         */
        public int mini_thumb_magic             = -1;
        /**
         * The thumbnails image file path
         */
        public String thumb_path                = "";
        /**
         * This category(BUCKET_ID) contains image items count
         */
        public int item_count                   = 0;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            ItemImage image = new ItemImage();
            image.id = id;
            image.data = data;
            image.date_taken = date_taken;
            image.bucket_display_name = bucket_display_name;
            image.bucket_id = bucket_id;
            image.mini_thumb_magic = mini_thumb_magic;
            image.thumb_path = thumb_path;
            image.item_count = item_count;
            return super.clone();
        }
    }
}
