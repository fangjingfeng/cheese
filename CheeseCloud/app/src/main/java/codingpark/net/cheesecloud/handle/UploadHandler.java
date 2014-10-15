package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import codingpark.net.cheesecloud.DevicePathUtils;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.utils.CatalogList;
import codingpark.net.cheesecloud.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.utils.TypeFilter;

public class UploadHandler implements OnClickListener, OnItemLongClickListener{
    private static final String TAG         = "EventHandler";

    // Common list mode: list all files and folders
    public static final int TREEVIEW_MODE           = 1;
    // Catalog list mode: just list the specified type files
    public static final int CATALOG_MODE            = 2;
    // Current selected list mode: default mode is TREEVIEW_MODE
    private int	mlistmode                           = TREEVIEW_MODE;

    private final Context mContext;
    private final FileManager mFileMgr;
    private final CatalogList mCataList;

    private UploadListAdapter mAdapter              = null;
    // Enable/disable show pictures/videos thumbnail
    private boolean thumbnail_flag                  = true;

    //the list used to feed info into the array adapter
    private ArrayList<String> mFileList             = null;
    // Store user selected all file/folder path
    private ArrayList<String> mSelectedPath = null;
    private LinearLayout mPathBar                   = null;

    // The previous selected header tab
    private View preView                            = null;

    private PathBarItemClickListener mPathBatItemListener       = null;
    private SelectedChangedListener mSelectedChangedListener    = null;


    /**
     * Creates an EventHandler object. This object is used to communicate
     * most work from the Main activity to the FileManager class.
     *
     * @param context	The context of the activity_upload activity e.g  Main
     * @param manager	The FileManager object that was instantiated from Main
     */
    public UploadHandler(Context context, final FileManager manager, final CatalogList CataList) {
        mContext = context;
        mFileMgr = manager;
        mCataList = CataList;

        mPathBatItemListener = new PathBarItemClickListener();

        // Initial as ROOT_DISK, ListView list all flash and sdcard
        mFileList = new ArrayList<String>(mFileMgr.switchToRoot());
        mSelectedPath = new ArrayList<String>();
    }

    /**
     * This method is called from the Main activity and this has the same
     * reference to the same object so when changes are made here or there
     * they will display in the same way.
     *
     * @param adapter	The TableRow object
     */
    public void setListAdapter(UploadListAdapter adapter) {
        mAdapter = adapter;
    }

    public void setSelectedChangedListener(SelectedChangedListener listener) {
        mSelectedChangedListener = listener;
    }

    /**
     * This method is called from the upload activity and this has the same
     * reference to the same object so when changes are made here or there
     * they will display in the same way.
     */
    public int getMode() {
        return mlistmode;
    }

    public ArrayList<String> getSelectedPath() {
        return mSelectedPath;
    }

    /**
     * This method is called from the upload activity and is passed
     * the LinearLayout that should be updated as the directory changes
     * so the user knows which folder they are in.
     *
     * @param pathBar	The label to update as the directory changes
     */
    public void setUpdatePathBar(LinearLayout pathBar) {
        mPathBar = pathBar;
        // Initial path bar default item, Disk, this item is root.
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
        textView.setTag(0);
        String path = mContext.getResources().getString(R.string.upload_activity_bottom_bar_default_item_string);
        textView.setText(path);
        textView.setOnClickListener(mPathBatItemListener);
        mPathBar.addView(textView);
    }

    private void refreshPathBar() {
        Log.d(TAG, "Start refresh path bar");
        Stack<String> pathStack = mFileMgr.getPathStack();
        int pathBarCount = mPathBar.getChildCount();
        int pathStackCount = pathStack.size();

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        if (pathBarCount < pathStackCount) {
            // Add extra path to pathBar
            for (int i = pathBarCount; i < pathStackCount; i++) {
                TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
                textView.setTag(i);
                String path = pathStack.get(i);
                path = path.substring(path.lastIndexOf("/") + 1, path.length());
                Log.d(TAG, "path " + i + " is " + path);
                textView.setText(path);
                textView.setOnClickListener(mPathBatItemListener);
                mPathBar.addView(textView);
            }
        } else if (pathBarCount > pathStackCount) {
            // Remove extra path from pathBar
            for (int i = pathBarCount; i > pathStackCount ; i--) {
                mPathBar.removeViewAt(i - 1);
            }

        }
    }


    /**
     * Set this true and thumbnails will be used as the icon for image files. False will
     * show a default image.
     * @param show
     */
    public void setShowThumbnails(boolean show) {
        thumbnail_flag = show;
    }



    /**
     *  This method, handles the button presses of the top buttons found
     *  in the Main activity.
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.back_button:
                if (mlistmode != TREEVIEW_MODE)
                {
                    break;
                }

                if (!mFileMgr.isRoot()) {
                    if (isMultiSelected()) {
                        mAdapter.clearMultiSelect();
                    }
                    updateContent(mFileMgr.switchToPreviousDir());
                }
                break;

            case R.id.header_disk_button:
                refreshFocus(preView,v);
                if(mlistmode == TREEVIEW_MODE) {
                    break;
                }
                mlistmode = TREEVIEW_MODE;
                updateContent(mFileMgr.switchToRoot());
                if(mPathBar != null)
                    refreshPathBar();
                break;

            case R.id.image_button:
                mlistmode = CATALOG_MODE;
                updateContent(mCataList.SetFileTyp(CatalogList.TYPE_PICTURE));
                if(mPathBar != null)
                    refreshPathBar();
                refreshFocus(preView,v);
                break;

            case R.id.movie_button:
                mlistmode = CATALOG_MODE;
                updateContent(mCataList.SetFileTyp(CatalogList.TYPE_MOVIE));
                if(mPathBar != null)
                    refreshPathBar();
                refreshFocus(preView,v);
                break;
			
        }
        switch(getMode()){
            case CATALOG_MODE:
                break;
            case TREEVIEW_MODE:
                if(mFileMgr.isRoot()){
                }else{
                }
                break;
            default:
                break;
        }
    }

    public void setInitView(View v){
        preView = v;
    }

    private void refreshFocus(View pre,View cur) {
        if( pre != cur)
        {
            cur.setSelected(true);
            pre.setSelected(false);
            preView = cur;
        }
    }

    /**
     * will return the data in the ArrayList that holds the dir contents.
     *
     * @param position	the indext of the arraylist holding the dir content
     * @return the data in the arraylist at position (position)
     */
    private String getData(int position) {

        if(position > mFileList.size() - 1 || position < 0)
            return null;

        return mFileList.get(position);
    }

    public String getFilePath(int position){
        final String item = getData(position);
        Log.d(TAG,"item  " + item);
        if(getMode() == UploadHandler.TREEVIEW_MODE)
        {
            String curDir = mFileMgr.getCurrentDir();
            if(curDir.equals(mFileMgr.diskName)) {
                return item;
            }
            else {
                return (mFileMgr.getCurrentDir() + "/" + item);
            }
        }
        return item;
    }

    /**
     * called to update the file contents as the user navigates there
     * phones file system.
     *
     * @param content	an ArrayList of the file/folders in the current directory.
     */
    public void updateContent(ArrayList<String> content) {
        if(!mFileList.isEmpty())
            mFileList.clear();

        mFileList.addAll(content);
        mAdapter.notifyDataSetChanged();

        if(mPathBar != null)
            refreshPathBar();
    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        TextView topView;
        TextView bottomView;
        ImageView icon;
        CheckBox mSelect;	//multi-select check mark icon
    }

    /**
     * This class listening path bar item click event.Path bar's item
     * stand for a folder of current path. When user click one item,
     * the current path should switch to the folder and clear the path
     * bar's extra redundant item.
     */
    private class PathBarItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int index = Integer.valueOf(v.getTag().toString());
            updateContent(mFileMgr.switchToDirByIndex(index));
        }
    }



    /**
     * A nested class to handle displaying a custom view in the ListView that
     * is used in the Main activity. If any icons are to be added, they must
     * be implemented in the getView method. This class is instantiated once in Main
     * and has no reason to be instantiated again.
     */
    public class UploadListAdapter extends ArrayAdapter<String> {
        private final int KB            = 1024;
        private final int MG            = KB * KB;
        private final int GB            = MG * KB;

        private String display_size     = null;
        private String dir_name         = null;
        // Store user selected files index in the ListView
        private ArrayList<Integer> mSelectedPositions = null;
        //private LinearLayout hidden_layout;
        private ThumbnailCreator thumbnail      = null;
        private DevicePathUtils mDevices        = null;
        private ItemCheckedListener mCheckedListener = null;

        public UploadListAdapter() {
            super(mContext, R.layout.upload_item_layout, mFileList);

            thumbnail = new ThumbnailCreator(mContext, 64, 64);
            dir_name = mFileMgr.getCurrentDir();
            mDevices = new DevicePathUtils(mContext);

            mCheckedListener = new ItemCheckedListener();
            mSelectedPositions = new ArrayList<Integer>();
        }

        public void addMultiPosition(int index) {
            String r_path = getFilePath(index);
            if (mSelectedPositions.contains(index)) {
                mSelectedPositions.remove(Integer.valueOf(index));
                mSelectedPath.remove(r_path);
            } else {
                mSelectedPositions.add(index);
                mSelectedPath.add(r_path);
            }
            notifyDataSetChanged();
        }

        /**
         * This will turn off multi-select and hide the multi-select buttons at the
         * bottom of the view.
         *
         * @param if this is true any files/folders the user selected for multi-select
         * 					will be cleared. If false, the data will be kept for later use. Note:
         * 					multi-select copy and move will usually be the only one to pass false,
         * 					so we can later paste it to another folder.
         */
        public void clearMultiSelect() {
            // TODO Handle multiple select

            if(mSelectedPositions != null && !mSelectedPositions.isEmpty())
                mSelectedPositions.clear();

            if(mSelectedPath != null && !mSelectedPath.isEmpty())
                mSelectedPath.clear();

            notifyDataSetChanged();
        }


        public void clearThumbnail() {
            if(thumbnail_flag) {
                thumbnail.clearBitmapCache();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(mlistmode == CATALOG_MODE) {
                return getView_catalog(position,convertView,parent);
            }
            else if (mlistmode == TREEVIEW_MODE) {
                return getView_tree(position,convertView,parent);
            }

            return getView_tree(position,convertView,parent);
        }

        private View getView_catalog(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            File file = new File(mFileList.get(position));

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.upload_item_layout, parent, false);

                holder = new ViewHolder();
                holder.topView = (TextView)convertView.findViewById(R.id.file_name_view);
                holder.bottomView = (TextView)convertView.findViewById(R.id.sub_files_count_view);
                holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
                holder.mSelect = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                // 1. Update CheckBox's tag, this tag used in ItemSelectedListener
                holder.mSelect.setTag(position);
                // 2. Set CheckBox's OnCheckedChangeListener
                holder.mSelect.setOnCheckedChangeListener(mCheckedListener);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
                // 1. Update CheckBox's tag, this tag used in ItemSelectedListener
                holder.mSelect.setTag(position);
                // 2. Set CheckBox's OnCheckedChangeListener
                holder.mSelect.setOnCheckedChangeListener(mCheckedListener);
            }


            if (mSelectedPositions != null && mSelectedPositions.contains(position))
                holder.mSelect.setVisibility(ImageView.VISIBLE);
            else
                holder.mSelect.setVisibility(ImageView.GONE);

            holder.mSelect.setVisibility(ImageView.VISIBLE);

            if(file != null && file.isFile()) {
                String ext = file.toString();
                String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);
    			
    			/* This series of else if statements will determine which 
    			 * icon is displayed 
    			 */
                if (TypeFilter.getInstance().isPdfFile(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.pdf);

                } else if (TypeFilter.getInstance().isMusicFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.music);

                } else if (TypeFilter.getInstance().isPictureFile(sub_ext)) {

                    if(thumbnail_flag && file.length() != 0) {
                        Bitmap thumb = thumbnail.hasBitmapCached(file.getAbsolutePath());

                        if(thumb == null) {

                            holder.icon.setImageResource(R.drawable.image);
                            thumbnail.setBitmapToImageView(file.getAbsolutePath(),
                                    holder.icon);

                        } else {
                            holder.icon.setImageBitmap(thumb);
                        }

                    } else {
                        holder.icon.setImageResource(R.drawable.image);
                    }

                } else if(TypeFilter.getInstance().isMovieFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.movies);

                } else {
                    holder.icon.setImageResource(R.drawable.text);
                }
            }


            if(file.isFile()) {
                double size = file.length();
                if (size > GB)
                    display_size = String.format("%.2f Gb ", (double)size / GB);
                else if (size < GB && size > MG)
                    display_size = String.format("%.2f Mb ", (double)size / MG);
                else if (size < MG && size > KB)
                    display_size = String.format("%.2f Kb ", (double)size/ KB);
                else
                    display_size = String.format("%.2f bytes ", (double)size);

                if(file.isHidden())
                    holder.bottomView.setText("(hidden) | " + display_size);
                else
                    holder.bottomView.setText(display_size);
            }

            holder.topView.setText(file.getName());

            return convertView;
        }

        /**
         * Get view on TreeView mode
         * @param position
         *      Current view position in the list view
         * @param convertView
         * @param parent
         * @return
         */
        private View getView_tree(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int num_items = 0;
            String temp = mFileMgr.getCurrentDir();

            File file = new File(getFilePath(position));
            String[] list = file.list();
            if(list != null)
                num_items = list.length;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.upload_item_layout, parent, false);

                holder = new ViewHolder();
                holder.topView = (TextView)convertView.findViewById(R.id.file_name_view);
                holder.bottomView = (TextView)convertView.findViewById(R.id.sub_files_count_view);
                holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
                holder.mSelect = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
                // 1. Update CheckBox's tag, this tag used in ItemSelectedListener
                holder.mSelect.setTag(position);
                // 2. Set CheckBox's OnCheckedChangeListener
                holder.mSelect.setOnCheckedChangeListener(mCheckedListener);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
                // 1. Update CheckBox's tag, this tag used in ItemSelectedListener
                holder.mSelect.setTag(position);
                // 2. Set CheckBox's OnCheckedChangeListener
                holder.mSelect.setOnCheckedChangeListener(mCheckedListener);
            }
    		
    		/* This will check if the thumbnail cache needs to be cleared by checking
    		 * if the user has changed directories. This way the cache wont show
    		 * a wrong thumbnail image for the new image file 
    		 */
            if(!dir_name.equals(temp) && thumbnail_flag) {
                thumbnail.clearBitmapCache();
                dir_name = temp;
            }


            if (mSelectedPositions != null && mSelectedPositions.contains(position))
                holder.mSelect.setChecked(true);
            else
                holder.mSelect.setChecked(false);

            if(file != null && file.isFile()) {
                String ext = file.toString();
                String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);
    			
    			/* This series of else if statements will determine which 
    			 * icon is displayed 
    			 */
                if (TypeFilter.getInstance().isPdfFile(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.pdf);

                } else if (TypeFilter.getInstance().isMusicFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.music);

                } else if (TypeFilter.getInstance().isPictureFile(sub_ext)) {

                    if(thumbnail_flag && file.length() != 0) {
                        Bitmap thumb = thumbnail.hasBitmapCached(file.getAbsolutePath());
                        if(thumb == null) {
                            holder.icon.setImageResource(R.drawable.image);
                            thumbnail.setBitmapToImageView(file.getAbsolutePath(),
                                    holder.icon);

                        } else {
                            holder.icon.setImageBitmap(thumb);
                        }

                    } else {
                        holder.icon.setImageResource(R.drawable.image);
                    }

                } else if (TypeFilter.getInstance().isZipFile(sub_ext) ||
                        TypeFilter.getInstance().isGZipFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.zip);

                } else if(TypeFilter.getInstance().isMovieFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.movies);

                } else if(TypeFilter.getInstance().isWordFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.word);

                } else if(TypeFilter.getInstance().isExcelFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.excel);

                } else if(TypeFilter.getInstance().isPptFile(sub_ext)) {

                    holder.icon.setImageResource(R.drawable.ppt);

                } else if(TypeFilter.getInstance().isHtml32File(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.html32);

                } else if(TypeFilter.getInstance().isXml32File(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.xml32);

                } else if(TypeFilter.getInstance().isConfig32File(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.config32);

                } else if(TypeFilter.getInstance().isApkFile(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.appicon);

                } else if(TypeFilter.getInstance().isJarFile(sub_ext)) {
                    holder.icon.setImageResource(R.drawable.jar32);

                } else {
                    holder.icon.setImageResource(R.drawable.text);
                }

            } else if (file != null && file.isDirectory()) {
                holder.icon.setImageResource(R.drawable.folder);
            }
            else{
                holder.icon.setImageResource(R.drawable.folder);
            }


            if(file.isFile()) {
                double size = file.length();
                if (size > GB)
                    display_size = String.format("%.2f Gb ", (double)size / GB);
                else if (size < GB && size > MG)
                    display_size = String.format("%.2f Mb ", (double)size / MG);
                else if (size < MG && size > KB)
                    display_size = String.format("%.2f Kb ", (double)size/ KB);
                else
                    display_size = String.format("%.2f bytes ", (double)size);

                if(file.isHidden())
                    holder.bottomView.setText("(hidden) | " + display_size);
                else
                    holder.bottomView.setText(display_size);

            } else {
                String count_unit = mContext.getString(R.string.upload_activity_list_item_count_unit_string);
                if(file.isHidden())
                    holder.bottomView.setText("(hidden) | " + num_items + " " + count_unit);
                else
                    holder.bottomView.setText(num_items + " " + count_unit);
            }

            holder.topView.setText(file.getName());

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
                if (isChecked) {
                    if (!mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.add(r_index);
                        mSelectedPath.add(getFilePath(r_index));
                    }
                } else {
                    if (mSelectedPositions.contains(r_index)) {
                        mSelectedPositions.remove((Integer)r_index);
                        mSelectedPath.remove(getFilePath(r_index));
                    }
                }
                Log.d(TAG, "Current selected items: " + mSelectedPositions.toString());
                if (mSelectedChangedListener != null)
                    mSelectedChangedListener.changed(mSelectedPath);
            }
        }


    }

    public boolean isMultiSelected() {
        return !mSelectedPath.isEmpty();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        Log.d(TAG, "Long clicked!");
        if(mFileMgr.getCurrentDir().equals(mFileMgr.diskName)) {
            return true; //do not respond when in storage list mode
        }
        return true;
    }

    /**
     * Interface definition for a callback to be invoked when user
     * changed item check state
     */
    public interface SelectedChangedListener {
        public void changed(ArrayList<String> selectedPathList);
    }
}
