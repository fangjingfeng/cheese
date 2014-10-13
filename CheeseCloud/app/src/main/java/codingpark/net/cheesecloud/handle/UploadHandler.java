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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.zip.Inflater;

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
    private UploadListAdapter mAdapter = null;
    // Enable/disable show pictures/videos thumbnail
    private boolean thumbnail_flag          = true;

    //the list used to feed info into the array adapter and when multi-select is on
    private ArrayList<String> mFileList, mMultiSelectData;
    // Display current directory path
    //private TextView mPathLabel             = null;
    private LinearLayout mPathBar           = null;

    // The previous selected header tab
    private View preView                    = null;

    private PathBarItemClickListener mPathBatItemListener   = null;


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

    /**
     * This method is called from the upload activity and this has the same
     * reference to the same object so when changes are made here or there
     * they will display in the same way.
     */
    public int getMode() {
        return mlistmode;
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
     *
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
                    /*
                    if(multi_select_flag) {
                        mAdapter.killMultiSelect(true);
                        Toast.makeText(mContext, R.string.Multi_select_off,
                                Toast.LENGTH_SHORT).show();
                    }
                    */
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
        ImageView mSelect;	//multi-select check mark icon
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
        private final int KB = 1024;
        private final int MG = KB * KB;
        private final int GB = MG * KB;
        private String display_size;
        private String dir_name;
        private ArrayList<Integer> positions;
        private LinearLayout hidden_layout;
        private ThumbnailCreator thumbnail;
        private DevicePathUtils mDevices;

        public UploadListAdapter() {
            super(mContext, R.layout.upload_item_layout, mFileList);

            thumbnail = new ThumbnailCreator(mContext, 32, 32);
            dir_name = mFileMgr.getCurrentDir();
            mDevices = new DevicePathUtils(mContext);
        }

        /*
        public void addMultiPosition(int index, String path) {
            if(positions == null)
                positions = new ArrayList<Integer>();

            if(mMultiSelectData == null) {
                positions.add(index);
                add_multiSelect_file(path);

            } else if(mMultiSelectData.contains(path)) {
                if(positions.contains(index))
                    positions.remove(new Integer(index));

                mMultiSelectData.remove(path);

            } else {
                positions.add(index);
                add_multiSelect_file(path);
            }

            notifyDataSetChanged();
        }
        */

        /**
         * This will turn off multi-select and hide the multi-select buttons at the
         * bottom of the view.
         *
         * @param if this is true any files/folders the user selected for multi-select
         * 					will be cleared. If false, the data will be kept for later use. Note:
         * 					multi-select copy and move will usually be the only one to pass false,
         * 					so we can later paste it to another folder.
         */
//        public void killMultiSelect(boolean clearData) {
//            // TODO Handle multiple select
//            /*
//            hidden_layout = (LinearLayout)((Activity)mContext).findViewById(R.id.hidden_buttons);
//            hidden_layout.setVisibility(LinearLayout.GONE);
//            multi_select_flag = false;
//
//            if(positions != null && !positions.isEmpty())
//                positions.clear();
//
//            if(clearData)
//                if(mMultiSelectData != null && !mMultiSelectData.isEmpty())
//                    mMultiSelectData.clear();
//
//            notifyDataSetChanged();
//            */
//        }


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
                holder.topView = (TextView)convertView.findViewById(R.id.top_view);
                holder.bottomView = (TextView)convertView.findViewById(R.id.bottom_view);
                holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
                holder.mSelect = (ImageView)convertView.findViewById(R.id.multiselect_icon);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }


            if (positions != null && positions.contains(position))
                holder.mSelect.setVisibility(ImageView.VISIBLE);
            else
                holder.mSelect.setVisibility(ImageView.GONE);


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
            String filePath = file.getAbsolutePath();

            //num_items = mDevices.getPartitions(filePath);
            //if(num_items <= 0 ){
            String[] list = file.list();

            if(list != null)
                num_items = list.length;
            //}
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.upload_item_layout, parent, false);

                holder = new ViewHolder();
                holder.topView = (TextView)convertView.findViewById(R.id.top_view);
                holder.bottomView = (TextView)convertView.findViewById(R.id.bottom_view);
                holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
                holder.mSelect = (ImageView)convertView.findViewById(R.id.multiselect_icon);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
    		
    		/* This will check if the thumbnail cache needs to be cleared by checking
    		 * if the user has changed directories. This way the cache wont show
    		 * a wrong thumbnail image for the new image file 
    		 */
            if(!dir_name.equals(temp) && thumbnail_flag) {
                thumbnail.clearBitmapCache();
                dir_name = temp;
            }


            if (positions != null && positions.contains(position))
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

        /*
        private void add_multiSelect_file(String src) {
            if(mMultiSelectData == null)
                mMultiSelectData = new ArrayList<String>();

            mMultiSelectData.add(src);
        }
        */
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
}
