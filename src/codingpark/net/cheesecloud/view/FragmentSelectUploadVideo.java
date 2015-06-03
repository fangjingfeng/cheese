package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.OnSelectUploadChangedListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentSelectUploadVideo extends ListFragment implements OnKeyDownListener, LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG         = FragmentSelectUploadVideo.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnSelectUploadChangedListener mListener;

    /**
     * Cursor used to access the results from querying for videos on the SD card.
     */
    private Cursor cursor           = null;
    private ContentResolver cr      = null;
    private Context mContext        = null;

    private LinearLayout mPathBar                   = null;

    private PathBarItemClickListener mPathBatItemListener       = null;

    private String[] video_projection = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.MINI_THUMB_MAGIC
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
    private ArrayList<ItemVideo> mCategoryList       = null;
    /**
     * Store the {@see ItemVideo} objects that query from
     * {@link android.provider.MediaStore.Images}, the data will filtered
     * by BUCKET_ID, this list will show in ITEM_LIST_MODE as data of ListView.
     */
    private ArrayList<ItemVideo> mSubItemList = null;
    /**
     * Store the all {@see ItemVideo} objects that query from
     * {@link android.provider.MediaStore.Images}.
     */
    private ArrayList<ItemVideo> mAllItemList = null;
    /**
     * The {@see CATEGORY_LIST_MODE} list view adapter
     */
    private VideoCategoryAdapter mCategoryAdapter           = null;
    /**
     * The {@see IMAGE_CATEGORY_LIST_MODE} list view adapter
     */
    private VideoItemAdapter mItemAdapter                   = null;
    /**
     * The LayoutInflater object, used by ArrayAdapter to inflate view from
     * layout xml file.
     */
    private LayoutInflater mInflater                        = null;

    private ArrayList<String> mSelectedPath         = null;
    // Store user selected files index in the ListView
    private ArrayList<Integer> mSelectedPositions = null;

    public static FragmentSelectUploadVideo newInstance(String param1, String param2) {
        FragmentSelectUploadVideo fragment = new FragmentSelectUploadVideo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentSelectUploadVideo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        cr = mContext.getContentResolver();
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Initial MediaStore query task
        getLoaderManager().initLoader(0, null, this);
        // Initial the two show mode data list
        mAllItemList = new ArrayList<ItemVideo>();
        mCategoryList = new ArrayList<ItemVideo>();
        mSubItemList = new ArrayList<ItemVideo>();
        mSelectedPath = new ArrayList<String>();
        mSelectedPositions = new ArrayList<Integer>();
        // Intial the two show mode data adapter
        mCategoryAdapter = new VideoCategoryAdapter(mContext, R.layout.select_upload_video_category_mode_item_layout, mCategoryList);
        mItemAdapter = new VideoItemAdapter(mContext, R.layout.select_upload_video_item_mode_item_layout, mSubItemList);
        // Set default list adapter to CATEGORY_LIST_MODE
        mPathBatItemListener = new PathBarItemClickListener();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectUploadChangedListener) activity;
            mContext = activity;
            //setContentView(R.layout.select_upload_image_layout);

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_upload_video, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        switch (mListMode) {
            case CATEGORY_LIST_MODE:
                setListAdapter(mCategoryAdapter);
                //mCategoryAdapter.notifyDataSetChanged();
                break;
            case ITEM_LIST_MODE:
                setListAdapter(mItemAdapter);
                //mItemAdapter.notifyDataSetChanged();
                break;
        }
        mPathBar = (LinearLayout)getView().findViewById(R.id.pathBarContainer);
        setUpdatePathBar(mPathBar);
    }

    /**
     * This method is called from the upload activity and is passed
     * the LinearLayout that should be updated as the directory changes
     * so the user knows which folder they are in.
     *
     * @param pathBar	The label to update as the directory changes
     */
    private void setUpdatePathBar(LinearLayout pathBar) {
        mPathBar = pathBar;
        // Initial path bar default item, Disk, this item is root.
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
        textView.setTag(0);
        String path = "视频";//mContext.getResources().getString(R.string.upload_activity_bottom_bar_default_item_string);
        textView.setText(path);
        textView.setOnClickListener(mPathBatItemListener);
        mPathBar.addView(textView);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mListMode == CATEGORY_LIST_MODE) {
            // Get video item filtered by bucket_id
            ItemVideo category = mCategoryList.get(position);
            mSubItemList.clear();
            for (int i = 0; i < mAllItemList.size(); i++) {
                ItemVideo item = mAllItemList.get(i);
                if (category.bucket_id == item.bucket_id) {
                    try {
                        mSubItemList.add((ItemVideo) item.clone());
                        getThumbPath(item);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            setListAdapter(mItemAdapter);
            mItemAdapter.notifyDataSetChanged();
            mListMode = ITEM_LIST_MODE;
            refreshPathBar();
        } else if (mListMode == ITEM_LIST_MODE) {
            addMultiPosition(position);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String order_clause = MediaStore.Video.Media.BUCKET_ID + " ASC, "
                + MediaStore.Video.Media.DATE_TAKEN + " ASC ";
        return new CursorLoader(mContext,
                uri,
                video_projection,
                null,
                null,
                order_clause) ;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: " + data.getCount());
        if (data != null)  {
            data.moveToPosition(-1);
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
                // Judge the video is exist? If not exists, needn't add it to mAllItemList
                if (!(new File(data.getString(1)).exists())) {
                    continue;
                }
                ItemVideo item =new ItemVideo();
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
                ItemVideo r_category = null;
                try {
                    r_category = (ItemVideo)mAllItemList.get(0).clone();
                    r_category.item_count = 1;
                    int r_bucket_id = mAllItemList.get(0).bucket_id;
                    mCategoryList.add(r_category);
                    for (int i = 1; i < mAllItemList.size(); i++) {
                        if (mAllItemList.get(i).bucket_id == r_bucket_id) {
                            mCategoryList.get(mCategoryList.size() - 1).item_count++;
                        }
                        else {
                            r_category = (ItemVideo)mAllItemList.get(i).clone();
                            r_category.item_count = 1;
                            r_bucket_id = r_category.bucket_id;
                            mCategoryList.add(r_category);
                        }
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < mCategoryList.size(); i++) {
            getThumbPath(mCategoryList.get(i));
        }
        if (mListMode == CATEGORY_LIST_MODE) {
            Log.d(TAG, "Refresh list");
            Log.d(TAG, "mCategoryList: " + mCategoryList.size());
            Log.d(TAG, "mAllItemList: " + mAllItemList.size());
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (mListMode == ITEM_LIST_MODE && !isVisibleToUser) {
            if ((mSelectedPositions != null) || (mSelectedPath != null)) {
                mSelectedPath.clear();
                mSelectedPositions.clear();
                mItemAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public boolean onBackKeyDown() {
        if (mListMode == CATEGORY_LIST_MODE) {
            return false;
        } else {
            clearMultiSelect();
            mListMode = CATEGORY_LIST_MODE;
            setListAdapter(mCategoryAdapter);
            mCategoryAdapter.notifyDataSetChanged();
            refreshPathBar();
            return true;
        }
    }

    /**
     * This will turn off multi-select and hide the multi-select buttons at the
     * bottom of the view.
     */
    public void clearMultiSelect() {

        if(mSelectedPositions != null && !mSelectedPositions.isEmpty())
            mSelectedPositions.clear();

        if(mSelectedPath != null && !mSelectedPath.isEmpty())
            mSelectedPath.clear();

        if (mListMode == ITEM_LIST_MODE) {
            mItemAdapter.notifyDataSetChanged();
            if (mListener != null)
                mListener.onSelectUploadChanged(mSelectedPath);
        }
    }

    private String getThumbPath(ItemVideo item) {
        String path = "";
        Bitmap img = MediaStore.Video.Thumbnails.getThumbnail(
                cr, item.id,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (img != null) {
            item.thumb_path = "";
            Log.d(TAG, "getThumbPath: success");
        } else {
            item.thumb_path = "";
            Log.d(TAG, "getThumbPath: failed");
        }
        return path;
    }

    private void addMultiPosition(int index) {
        String r_path = mSubItemList.get(index).data;
        if (mSelectedPositions.contains(index)) {
            mSelectedPositions.remove(Integer.valueOf(index));
            mSelectedPath.remove(r_path);
        } else {
            mSelectedPositions.add(index);
            mSelectedPath.add(r_path);
        }
        mItemAdapter.notifyDataSetChanged();
        if (mListener != null)
            mListener.onSelectUploadChanged(mSelectedPath);
    }

    private void refreshPathBar() {
        int pathBarCount = mPathBar.getChildCount();
        Log.d(TAG, "pathStackCount: " + pathBarCount);

        if (mListMode == CATEGORY_LIST_MODE) {
            if (pathBarCount > 1)
                mPathBar.removeViewAt(pathBarCount - 1);
        } else if (mListMode == ITEM_LIST_MODE) {
            if (pathBarCount == 1) {
                TextView textView = (TextView)mInflater.inflate(R.layout.path_bar_item_layout, null);
                textView.setTag(1);
                String path = "";
                path = mSubItemList.get(0).bucket_display_name;
                Log.d(TAG, "path is " + path);
                textView.setText(path);
                textView.setOnClickListener(mPathBatItemListener);
                mPathBar.addView(textView);
            }
        }
    }
    /**
     * This class listening path bar item click event.Path bar's item
     * stand for a folder of current path. When user click one item,
     * the current path should switch to the folder and clear the path
     * bar's extra redundant item.
     */
    private class PathBarItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int index = Integer.valueOf(v.getTag().toString());
            if (index == 0) {
                clearMultiSelect();
                mListMode = CATEGORY_LIST_MODE;
                setListAdapter(mCategoryAdapter);
                mCategoryAdapter.notifyDataSetChanged();
                refreshPathBar();
            }
            //updateContent(mFileMgr.switchToDirByIndex(index));
        }
    }


    private static final class CategoryViewHolder {
        public ImageView bucketThumbView    = null;
        public TextView bucketNameView      = null;
        public TextView countView           = null;
    }
    /**
     * The {@see CATEGORY_LIST_MODE} adapter
     */
    private class VideoCategoryAdapter extends ArrayAdapter<ItemVideo> {

        public VideoCategoryAdapter(Context context, int resource, List<ItemVideo> objects) {
            super(context, resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemVideo item = mCategoryList.get(position);
            CategoryViewHolder holder = null;
            if (convertView == null) {
                holder = new CategoryViewHolder();
                convertView = mInflater.inflate(R.layout.select_upload_video_category_mode_item_layout, null);
                holder.bucketThumbView = (ImageView)convertView.findViewById(R.id.bucketVideoView);
                holder.bucketNameView = (TextView)convertView.findViewById(R.id.bucketNameView);
                holder.countView = (TextView)convertView.findViewById(R.id.countTextView);
                convertView.setTag(holder);
            } else {
                holder = (CategoryViewHolder)convertView.getTag();
            }
            Bitmap img = MediaStore.Video.Thumbnails.getThumbnail(cr, item.id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
            if (img == null)
                holder.bucketThumbView.setImageResource(R.drawable.ic_launcher);
            else
                holder.bucketThumbView.setImageBitmap(img);
            holder.bucketNameView.setText(item.bucket_display_name);
            holder.countView.setText(item.item_count + "");
            return convertView;
        }
    }

    private static final class VideoItemViewHolder {
        public ImageView itemThumbView      = null;
        public TextView videoNameView       = null;
        public TextView videoTakeDateView = null;
        public CheckBox videoCheckbox       = null;
    }

    /**
     * The {@SEE ITEM_LIST_MODE} adapter
     */
    private class VideoItemAdapter extends ArrayAdapter<ItemVideo> {
        private CompoundButton.OnCheckedChangeListener mCheckedListener     = null;

        public VideoItemAdapter(Context context, int resource, List<ItemVideo> objects) {
            super(context, resource, objects);
            mCheckedListener = new ItemCheckedListener();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemVideo item = mSubItemList.get(position);
            VideoItemViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.select_upload_video_item_mode_item_layout, null);
                holder = new VideoItemViewHolder();
                holder.itemThumbView = (ImageView)convertView.findViewById(R.id.itemVideoView);
                holder.videoNameView = (TextView)convertView.findViewById(R.id.video_name_view);
                holder.videoTakeDateView = (TextView)convertView.findViewById(R.id.video_take_date_view);
                holder.videoCheckbox = (CheckBox)convertView.findViewById(R.id.video_checkbox);
                holder.videoCheckbox.setOnCheckedChangeListener(mCheckedListener);
                convertView.setTag(holder);
            } else {
                holder = (VideoItemViewHolder)convertView.getTag();
            }

            String path = item.data;
            /*
            if (path == null || path.isEmpty()) {
                holder.itemThumbView.setImageResource(R.drawable.ic_launcher);
            } else {
                holder.itemThumbView.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(cr, item.id, MediaStore.Images.Thumbnails.MICRO_KIND, null));
            }
            */

            Bitmap img = MediaStore.Video.Thumbnails.getThumbnail(cr, item.id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
            if (img == null)
                holder.itemThumbView.setImageResource(R.drawable.ic_launcher);
            else
                holder.itemThumbView.setImageBitmap(img);
            path = path.substring(path.lastIndexOf("/") + 1, path.length());
            holder.videoNameView.setText(path);
            holder.videoTakeDateView.setText(item.date_taken + "");
            holder.videoCheckbox.setTag(String.valueOf(position));

            if (mSelectedPositions != null && mSelectedPositions.contains(position))
                holder.videoCheckbox.setChecked(true);
            else
                holder.videoCheckbox.setChecked(false);

            return convertView;
        }

        /**
         * This class listening ListView item's select CheckBox checked event.
         * When user checked a item, class add this item's index to {@link #mSelectedPositions},
         * and add path which the item stand for to {@link #mSelectedPath}
         */
        private class ItemCheckedListener implements CompoundButton.OnCheckedChangeListener{
            //private static final String TAG     = "ItemSelectedListener";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Index: " + buttonView.getTag() + "\nChecked: " + isChecked);
                int r_index = Integer.valueOf(buttonView.getTag().toString());
                boolean isChanged = false;
                if (isChecked) {
                    if (!mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.add(r_index);
                        mSelectedPath.add(mSubItemList.get(r_index).data);
                        isChanged = true;
                    }
                } else {
                    if (mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.remove((Integer)r_index);
                        mSelectedPath.remove(mSubItemList.get(r_index).data);
                        isChanged = true;
                    }
                }
                Log.d(TAG, "Current selected items: " + mSelectedPositions.toString());
                if (isChanged && mListener != null) {
                    mListener.onSelectUploadChanged(mSelectedPath);
                }

            }
        }
    }


    /**
     * Store the video item information that query from
     * {@link android.provider.MediaStore.Images.Media}
     */
    private static class ItemVideo implements Cloneable{
        /**
         * Associate with _ID(index) field of the {@link android.provider.MediaStore.Images}
         */
        public int id           = -1;
        /**
         * Associate with DATA(origin video path) field of the {@link android.provider.MediaStore.Images}
         */
        public String data = "";
        /**
         * Associate with DATE_TAKEN(The take video date) filed of the {@link android.provider.MediaStore.Images}
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
         * The thumbnails video file path
         */
        public String thumb_path                = "";
        /**
         * This category(BUCKET_ID) contains video items count
         */
        public int item_count                   = 0;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            ItemVideo video = new ItemVideo();
            video.id = id;
            video.data = data;
            video.date_taken = date_taken;
            video.bucket_display_name = bucket_display_name;
            video.bucket_id = bucket_id;
            video.mini_thumb_magic = mini_thumb_magic;
            video.thumb_path = thumb_path;
            video.item_count = item_count;
            return super.clone();
        }
    }
}
