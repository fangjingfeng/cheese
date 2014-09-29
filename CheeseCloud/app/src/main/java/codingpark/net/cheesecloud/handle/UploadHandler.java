package codingpark.net.cheesecloud.handle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import codingpark.net.cheesecloud.DevicePath;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.model.CatalogList;
import codingpark.net.cheesecloud.utils.ThumbnailCreator;
import codingpark.net.cheesecloud.utils.TypeFilter;

/**
 * This class sits between the Upload activity and the FileManager class.
 * To keep the FileManager class modular, this class exists to handle 
 * UI events and communicate that information to the FileManger class
 *
 * This class is responsible for the buttons onClick method. If one needs
 * to change the functionality of the buttons found from the Main activity
 * or add button logic, this is the class that will need to be edited.
 *
 * This class is responsible for handling the information that is displayed
 * from the list view (the files and folder) with a a nested class TableRow.
 * The TableRow class is responsible for displaying which icon is shown for each
 * entry. For example a folder will display the folder icon, a Word doc will 
 * display a word icon and so on. If more icons are to be added, the TableRow 
 * class must be updated to display those changes. 
 *
 */
public class UploadHandler implements OnClickListener, OnItemLongClickListener{
    private static final String TAG         = "EventHandler";

    /*
     * Unique types to control which file operation gets
     * performed in the background
     */
    private static final int SEARCH_TYPE    = 0x00;

    // Common list mode: list all files and folders
    public static final int TREEVIEW_MODE   = 1;
    // Catalog list mode: just list the specified type files
    public static final int CATALOG_MODE    = 2;
    // Current selected list mode: default mode is TREEVIEW_MODE
    private int	mlistmode                   = TREEVIEW_MODE;

    private final Context mContext;
    private final FileManager mFileMgr;
    private final CatalogList mCataList;
    private UploadListAdapter mDelegate     = null;
    // Enable/Disable multiple select mode
    private boolean multi_select_flag       = false;
    // Enable/disable show pictures/videos thumbnail
    private boolean thumbnail_flag          = true;
    // Text display color
    private int mColor                      = Color.BLACK;

    //the list used to feed info into the array adapter and when multi-select is on
    private ArrayList<String> mDataSource, mMultiSelectData;
    // Display current directory path
    private TextView mPathLabel             = null;

    private View preView                    = null;

    public static final int ENABLE_TOOLBTN  = 1;
    public static final int DISABLE_TOOLBTN = 2;
    public void UpdateButtons(int mode)
    {
        /*
        ImageButton multi = (ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);

        switch(mode)
        {
            case ENABLE_TOOLBTN:
                multi.setEnabled(true);
                break;
            case DISABLE_TOOLBTN:
                multi.setEnabled(false);
                break;
        }
        */
    }

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

        mDataSource = new ArrayList<String>(mFileMgr.getHomeDir(FileManager.ROOT_FLASH));
    }

    /**
     * This method is called from the Main activity and this has the same
     * reference to the same object so when changes are made here or there
     * they will display in the same way.
     *
     * @param adapter	The TableRow object
     */
    public void setListAdapter(UploadListAdapter adapter) {
        mDelegate = adapter;
    }

    /**
     * This method is called from the Main activity and this has the same
     * reference to the same object so when changes are made here or there
     * they will display in the same way.
     */
    public int getMode() {
        return mlistmode;
    }
    /**
     * This method is called from the Main activity and is passed
     * the TextView that should be updated as the directory changes
     * so the user knows which folder they are in.
     *
     * @param path	The label to update as the directory changes
     */
    public void setUpdateLabels(TextView path) {
        mPathLabel = path;
    }

    /**
     * Set the list text display color
     * @param color
     */
    public void setTextColor(int color) {
        mColor = color;
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
     * Indicates whether the user wants to select
     * multiple files or folders at a time.
     * <br><br>
     * false by default
     *
     * @return	true if the user has turned on multi selection
     */
    public boolean isMultiSelected() {
        return multi_select_flag;
    }

    /**
     * Use this method to determine if the user has selected multiple files/folders
     *
     * @return	returns true if the user is holding multiple objects (multi-select)
     */
    public boolean hasMultiSelectData() {
        return (mMultiSelectData != null && mMultiSelectData.size() > 0);
    }

    /**
     * Will search for a file then display all files with the
     * search parameter in its name
     *
     * @param name	the name to search for
     */
    public void searchForFile(String name) {
        new BackgroundWork(SEARCH_TYPE).execute(name);
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
                    if(multi_select_flag) {
                        mDelegate.killMultiSelect(true);
                        Toast.makeText(mContext, R.string.Multi_select_off,
                                Toast.LENGTH_SHORT).show();
                    }
                    updateDirectory(mFileMgr.getPreviousDir());
                    if(mPathLabel != null)
                        mPathLabel.setText(mFileMgr.getCurrentDir());
                }
                break;

            case R.id.home_flash_button:
                refreshFocus(preView,v);
                if(mFileMgr.whichRoot() == FileManager.ROOT_FLASH &&
                        mlistmode == TREEVIEW_MODE)
                {
                    break;
                }
                mlistmode = TREEVIEW_MODE;
                if(multi_select_flag) {
                    mDelegate.killMultiSelect(true);
                    Toast.makeText(mContext, R.string.Multi_select_off,
                            Toast.LENGTH_SHORT).show();
                }
                updateDirectory(mFileMgr.getHomeDir(FileManager.ROOT_FLASH));
                if(mPathLabel != null)
                    mPathLabel.setText(mFileMgr.getCurrentDir());
                break;

            case R.id.image_button:
                mlistmode = CATALOG_MODE;
                setFileList(mCataList.SetFileTyp(CatalogList.TYPE_PICTURE));
                if(mPathLabel != null)
                    mPathLabel.setText(mContext.getResources().getString(R.string.image));
                refreshFocus(preView,v);
                break;

            case R.id.movie_button:
                mlistmode = CATALOG_MODE;
                setFileList(mCataList.SetFileTyp(CatalogList.TYPE_MOVIE));
                if(mPathLabel != null)
                    mPathLabel.setText(mContext.getResources().getString(R.string.video));
                refreshFocus(preView,v);
                break;
			
        }
        switch(getMode()){
            case CATALOG_MODE:
                UpdateButtons(DISABLE_TOOLBTN);
                break;
            case TREEVIEW_MODE:
                if(mFileMgr.isRoot()){
                    UpdateButtons(DISABLE_TOOLBTN);
                }else{
                    UpdateButtons(ENABLE_TOOLBTN);
                }
                break;
            default:
                UpdateButtons(DISABLE_TOOLBTN);
                break;
        }
    }

    public void getInitView(View v){
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
    public String getData(int position) {

        if(position > mDataSource.size() - 1 || position < 0)
            return null;

        return mDataSource.get(position);
    }

    public String getCurrentFilePath(int position){
        final String item = getData(position);
        Log.d(TAG,"item  " + item);
        if(getMode() == UploadHandler.TREEVIEW_MODE)
        {
            String curDir = mFileMgr.getCurrentDir();
            if(curDir.equals(mFileMgr.flashList) ||
                    curDir.equals(mFileMgr.sdcardList) ||
                    curDir.equals(mFileMgr.usbhostList)){
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
    public void updateDirectory(ArrayList<String> content) {
        if(!mDataSource.isEmpty())
            mDataSource.clear();

        for(String data : content)
            mDataSource.add(data);

        mDelegate.notifyDataSetChanged();
    }

    /**
     * called to refresh the file list
     *
     * @param content	an ArrayList of the file/folders in the current directory.
     */
    public void setFileList(ArrayList<String> content) {
        if(mDataSource.equals(content))
        {
            return;
        }
        if(!mDataSource.isEmpty())
            mDataSource.clear();

        mDataSource.addAll(content);
		
		/*
		 * File list have been change,so clear the thumbnail
		 */
        mDelegate.clearThumbnail();
        mDelegate.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView topView;
        TextView bottomView;
        ImageView icon;
        ImageView mSelect;	//multi-select check mark icon
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
        private DevicePath mDevices;

        public UploadListAdapter() {
            super(mContext, R.layout.tablerow, mDataSource);

            thumbnail = new ThumbnailCreator(mContext, 32, 32);
            dir_name = mFileMgr.getCurrentDir();
            mDevices = new DevicePath(mContext);
        }

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

        /**
         * This will turn off multi-select and hide the multi-select buttons at the
         * bottom of the view.
         *
         * @param clearData if this is true any files/folders the user selected for multi-select
         * 					will be cleared. If false, the data will be kept for later use. Note:
         * 					multi-select copy and move will usually be the only one to pass false,
         * 					so we can later paste it to another folder.
         */
        public void killMultiSelect(boolean clearData) {
            // TODO Handle multiple select
            /*
            hidden_layout = (LinearLayout)((Activity)mContext).findViewById(R.id.hidden_buttons);
            hidden_layout.setVisibility(LinearLayout.GONE);
            multi_select_flag = false;

            if(positions != null && !positions.isEmpty())
                positions.clear();

            if(clearData)
                if(mMultiSelectData != null && !mMultiSelectData.isEmpty())
                    mMultiSelectData.clear();

            notifyDataSetChanged();
            */
        }

        public String getFilePermissions(File file) {
            String per = "-";

            if(file.isDirectory())
                per += "d";
            if(file.canRead())
                per += "r";
            if(file.canWrite())
                per += "w";

            return per;
        }

        public void clearThumbnail() {
            if(thumbnail_flag) {
                thumbnail.clearBitmapCache();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(mlistmode == CATALOG_MODE)
            {
                return getView_catalog(position,convertView,parent);
            }
            else if (mlistmode == TREEVIEW_MODE)
            {
                return getView_tree(position,convertView,parent);
            }

            return getView_tree(position,convertView,parent);
        }

        private View getView_catalog(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            File file = new File(mDataSource.get(position));

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.tablerow, parent, false);

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

            holder.topView.setTextColor(mColor);
            holder.bottomView.setTextColor(mColor);

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

            String permission = getFilePermissions(file);

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
                    holder.bottomView.setText("(hidden) | " + display_size +" | "+ permission);
                else
                    holder.bottomView.setText(display_size +" | "+ permission);

            }

            holder.topView.setText(file.getName());

            return convertView;
        }

        private View getView_tree(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int num_items = 0;
            String temp = mFileMgr.getCurrentDir();
            File file = new File(getCurrentFilePath(position));
            String filePath = file.getAbsolutePath();

            num_items = mDevices.getPartitions(filePath);
            if(num_items <= 0 ){
                String[] list = file.list();

                if(list != null)
                    num_items = list.length;
            }
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.tablerow, parent, false);

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

            holder.topView.setTextColor(mColor);
            holder.bottomView.setTextColor(mColor);

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

            String permission = getFilePermissions(file);

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
                    holder.bottomView.setText("(hidden) | " + display_size +" | "+ permission);
                else
                    holder.bottomView.setText(display_size +" | "+ permission);

            } else {
                if(file.isHidden())
                    holder.bottomView.setText("(hidden) | " + num_items + " items | " + permission);
                else
                    holder.bottomView.setText(num_items + " items | " + permission);
            }

            holder.topView.setText(file.getName());

            return convertView;
        }

        private void add_multiSelect_file(String src) {
            if(mMultiSelectData == null)
                mMultiSelectData = new ArrayList<String>();

            mMultiSelectData.add(src);
        }
    }

    /**
     * A private inner class of EventHandler used to perform time extensive 
     * operations. So the user does not think the the application has hung, 
     * operations such as copy/past, search, unzip and zip will all be performed 
     * in the background. This class extends AsyncTask in order to give the user
     * a progress dialog to show that the app is working properly.
     *
     * (note): this class will eventually be changed from using AsyncTask to using
     * Handlers and messages to perform background operations. 
     */
    private class BackgroundWork extends AsyncTask<String, Void, ArrayList<String>> {
        private String file_name;
        private ProgressDialog pr_dialog;
        private int type;
        private int copy_rtn;
        private static final String WAKE_LOCK = "wakelock";
        private WakeLock wl = null;

        private BackgroundWork(int type) {
            this.type = type;
        }

        /**
         * This is done on the EDT thread. this is called before
         * doInBackground is called
         */
        @Override
        protected void onPreExecute() {
    		/*
    		 * lock standby when it is in file operation
    		 * */
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK);
            wl.acquire();

            switch(type) {
                case SEARCH_TYPE:
                    pr_dialog = ProgressDialog.show(mContext, "Searching",
                            "Searching current file system...",
                            true, true);
                    break;

            }
        }

        /**
         * background thread here
         */
        @Override
        protected ArrayList<String> doInBackground(String... params) {

            switch(type) {
                case SEARCH_TYPE:
                    file_name = params[0];
                    ArrayList<String> found = mFileMgr.searchInDirectory(mFileMgr.getCurrentDir(),
                            file_name);
                    return found;
                default:
                    return null;

            }
        }

        /**
         * This is called when the background thread is finished. Like onPreExecute, anything
         * here will be done on the EDT thread.
         */
        @Override
        protected void onPostExecute(final ArrayList<String> file) {
            final CharSequence[] names;
            int len = file != null ? file.size() : 0;
			
			/*
			 * unlock standby
			 */
            if(wl != null)
                wl.release();

            switch(type) {
                case SEARCH_TYPE:
                    if(len == 0) {
                        Toast.makeText(mContext, "Couldn't find " + file_name,
                                Toast.LENGTH_SHORT).show();

                    } else {
                        names = new CharSequence[len];

                        for (int i = 0; i < len; i++) {
                            String entry = file.get(i);
                            names[i] = entry.substring(entry.lastIndexOf("/") + 1, entry.length());
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Found " + len + " file(s)");
                        builder.setItems(names, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int position) {
                                String path = file.get(position);
//								updateDirectory(mFileMgr.getNextDir(path.
//													substring(0, path.lastIndexOf("/")), true));
								/*
								 * when it is a directory, open it,otherwise play it
								 * */
                                File f = new File(path);
                                String item_ext = null;

                                try {
                                    item_ext = path.substring(path.lastIndexOf(".") + 1, path.length());

                                } catch(IndexOutOfBoundsException e) {
                                    item_ext = "";
                                }
                                if(f.exists())
                                {
                                    if(f.isDirectory())
                                    {
                                        if(f.canRead()) {
                                            updateDirectory(mFileMgr.getNextDir(path));
                                            mPathLabel.setText(mFileMgr.getCurrentDir());
                                        }
                                    }
                                    else if (TypeFilter.getInstance().isMusicFile(item_ext)) {
                                        Intent picIntent = new Intent();
                                        picIntent.setAction(android.content.Intent.ACTION_VIEW);
                                        picIntent.setDataAndType(Uri.fromFile(f), "audio/*");
                                        try{
                                            mContext.startActivity(picIntent);
                                        }catch(ActivityNotFoundException e)
                                        {
                                            Log.e("EventHandler", "can not find activity to open it");
                                        }
                                    }
                                    else if(TypeFilter.getInstance().isPictureFile(item_ext)) {
                                        Intent picIntent = new Intent();
                                        picIntent.setAction(android.content.Intent.ACTION_VIEW);
                                        picIntent.setDataAndType(Uri.fromFile(f), "image/*");
                                        try
                                        {
                                            mContext.startActivity(picIntent);
                                        }catch(ActivityNotFoundException e)
                                        {
                                            Log.e("EventHandler", "can not find activity to open it");
                                        }
                                    }
							    	/*video file selected--add more video formats*/
                                    else if(TypeFilter.getInstance().isMovieFile(item_ext)) {
                                        Intent movieIntent = new Intent();
                                        movieIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
                                        movieIntent.setAction(android.content.Intent.ACTION_VIEW);
                                        movieIntent.setDataAndType(Uri.fromFile(f), "video/*");
                                        try{
                                            mContext.startActivity(movieIntent);
                                        }catch(ActivityNotFoundException e)
                                        {
                                            Log.e("EventHandler", "can not find activity to open it");
                                        }
                                    }
                                    else if(TypeFilter.getInstance().isApkFile(item_ext)){
                                        Intent apkIntent = new Intent();
                                        apkIntent.setAction(android.content.Intent.ACTION_VIEW);
                                        apkIntent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                                        try {
                                            mContext.startActivity(apkIntent);
                                        } catch (ActivityNotFoundException e) {
                                            Log.e("EventHandler", "can not find activity to open it");
                                        }

                                    }
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    pr_dialog.dismiss();
                    break;

            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        Log.d(TAG, "Long clicked!");
        if(mFileMgr.getCurrentDir().equals(mFileMgr.sdcardList) ||
                mFileMgr.getCurrentDir().equals(mFileMgr.usbhostList) ||
                mFileMgr.getCurrentDir().equals(mFileMgr.flashList)){
            return true; //do not respond when in storage list mode
        }
        return true;
    }
}
