package codingpark.net.cheesecloud;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import codingpark.net.cheesecloud.handle.EventHandler;
import codingpark.net.cheesecloud.handle.FileManager;
import codingpark.net.cheesecloud.model.CatalogList;
import codingpark.net.cheesecloud.utils.CropImage;
import codingpark.net.cheesecloud.utils.FileOperateCallbacks;
import codingpark.net.cheesecloud.utils.TypeFilter;
import codingpark.net.cheesecloud.view.HelpActivity;

/**
 * This is the main activity. The activity that is presented to the user
 * as the application launches.
 * <br>
 * <p>
 * This class handles creating the buttons and
 * text views. This class relies on the class EventHandler to handle all button
 * press logic and to control the data displayed on its ListView. This class
 * also relies on the FileManager class to handle all file operations such as
 * copy/paste zip/unzip etc. However most interaction with the FileManager class
 * is done via the EventHandler class. Also the SettingsMangager class to load
 * and save user settings. 
 * <br>
 * <p>
 * The design objective with this class is to control only the look of the
 * GUI (option menu, context menu, ListView, buttons and so on) and rely on other
 * supporting classes to do the heavy task.
 *
 */
public final class UploadActivity extends ListActivity implements FileOperateCallbacks {
    private static final String PREFS_NAME              = "ManagerPrefsFile";	//user preference file name
    private static final String PREFS_HIDDEN            = "hidden";
    private static final String PREFS_COLOR             = "color";
    private static final String PREFS_THUMBNAIL         = "thumbnail";
    private static final String PREFS_SORT              = "sort";

    private static final int D_MENU_DELETE              = 0x05;			//context menu directory delete
    private static final int D_MENU_RENAME              = 0x06;			//context menu rename
    private static final int D_MENU_COPY                = 0x07;			//context menu copy
    private static final int D_MENU_PASTE               = 0x08;			//context menu paste
    private static final int D_MENU_ZIP                 = 0x0e;			//context menu zip
    private static final int D_MENU_UNZIP               = 0x0f;			//context menu unzip
    private static final int D_MENU_MOVE                = 0x30;			//context menu move
    // TODO Support transfer file throught bluetooth
    private static final int F_MENU_BLUETOOTH           = 0x11;			//context menu bluetooth ???
    private static final int F_MENU_MOVE                = 0x20;			//context menu
    private static final int F_MENU_DELETE              = 0x0a;			//context menu id
    private static final int F_MENU_RENAME              = 0x0b;			//context menu id
    private static final int F_MENU_ATTACH              = 0x0c;			//context menu id
    private static final int F_MENU_COPY                = 0x0d;			//context menu id
    private static final int SETTING_REQ                = 0x10;			//request code for intent

    private FileManager mFileMag;
    private EventHandler mHandler;
    private EventHandler.TableRow mTable;
    private CatalogList mCataList;
    private DevicePath  mDevicePath;

    private SharedPreferences mSettings;
    private boolean mReturnIntent = false;
    private boolean mHoldingFile = false;
    private boolean mHoldingZip = false;
    private boolean mHoldingMkdir = false;
    private boolean mHoldingSearch = false;
    private boolean mUseBackKey = true;
    private String mCopiedTarget;
    private String mZippedTarget;
    private String mSelectedListItem;				//item from context menu
    private TextView  mPathLabel, mDetailLabel;

    private BroadcastReceiver mReceiver;

    private String TAG = "FileManager.Main";

    private String openType;
    private File openFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Don't hide actionbar, need it to display menu
        if(android.os.Build.VERSION.SDK_INT < 11) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
        */

        setContentView(R.layout.main);

        /*read fragment_settings*/
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        boolean hide = mSettings.getBoolean(PREFS_HIDDEN, false);
        boolean thumb = mSettings.getBoolean(PREFS_THUMBNAIL, true);
        int color = mSettings.getInt(PREFS_COLOR, -1);
        int sort = mSettings.getInt(PREFS_SORT, 1);

        mFileMag = new FileManager(this);
        mFileMag.setShowHiddenFiles(hide);
        mFileMag.setSortType(sort);

        mCataList = new CatalogList(this);
        mDevicePath = new DevicePath(this);

        mHandler = new EventHandler(UploadActivity.this, this, mFileMag, mCataList);
        mHandler.setTextColor(color);
        mHandler.setShowThumbnails(thumb);
        mTable = mHandler.new TableRow();

        /**
         * sets the ListAdapter for our ListActivity and
         * gives our EventHandler class the same adapter
         */
        mHandler.setListAdapter(mTable);
        setListAdapter(mTable);
        getListView().setOnItemLongClickListener(mHandler);
        
        /* register context menu for our list view */
        registerForContextMenu(getListView());

        mDetailLabel = (TextView)findViewById(R.id.detail_label);
        mPathLabel = (TextView)findViewById(R.id.path_label);
        mHandler.setUpdateLabels(mPathLabel, mDetailLabel);
		
        /*
         * Start refresh list
         * if this component is started by other applications
         *      then: change to the specific file path
         * else
         *      then: list storage list
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.i(TAG, "intent action ="+getIntent().getAction());
            String path = bundle.getString("Path");
            Log.i(TAG, "path = "+path);
            if (path != null) {
                if(mDevicePath.getUsbStoragePath().contains(path)) {
                    mPathLabel.setText(mFileMag.getCurrentDir());
                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_USBHOST));
                    getFocusForButton(R.id.home_usbhost_button);
                }
                else if (path.equals(mDevicePath.getInterStoragePath())) {
                    mPathLabel.setText(mFileMag.getCurrentDir());
                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_SDCARD));
                    getFocusForButton(R.id.home_sdcard_button);
                }
                else if (path.equals(mDevicePath.getSdStoragePath()) ) {
                    mPathLabel.setText(mFileMag.getCurrentDir());
                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
                    getFocusForButton(R.id.home_flash_button);
                }
            }
            else {
                //default path
                mPathLabel.setText(mFileMag.getCurrentDir());
                mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
                getFocusForButton(R.id.home_flash_button);
            }
        } else {
            //default path
            mPathLabel.setText(mFileMag.getCurrentDir());
            mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
            getFocusForButton(R.id.home_flash_button);
        }

		/* setup buttons */
        int[] img_button_id = {R.id.home_flash_button,
                R.id.home_sdcard_button,
                R.id.home_usbhost_button,
                R.id.back_button,
                R.id.multiselect_button,
                R.id.image_button,
                R.id.movie_button};

        int[] button_id = {R.id.hidden_paste,
                R.id.hidden_copy,
                R.id.hidden_attach,
                R.id.hidden_delete,
                R.id.hidden_move};

        ImageButton[] bimg = new ImageButton[img_button_id.length];
        Button[] bt = new Button[button_id.length];

        for(int i = 0; i < img_button_id.length; i++) {
            bimg[i] = (ImageButton)findViewById(img_button_id[i]);
            bimg[i].setOnClickListener(mHandler);

            if(i < 5) {
                bt[i] = (Button)findViewById(button_id[i]);
                bt[i].setOnClickListener(mHandler);
            }
        }

        if( getIntent().getAction() != null ){
            if(getIntent().getAction().equals(Intent.ACTION_GET_CONTENT)) {
                bimg[5].setVisibility(View.GONE);

                mReturnIntent = true;
            }
        }

        //register reciver to process sdcard out message
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tmpstring = intent.getData().getPath();

                ArrayList<String> dataOfUsb = mDevicePath.getUsbStoragePath();
                ArrayList<String> dataOfSd = mDevicePath.getSdStoragePath();
                ArrayList<String> dataOfFlash = mDevicePath.getInterStoragePath();

                if(intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED) ||
                        intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL))
                {
                    Log.d(TAG, tmpstring);
                    try
                    {
                        Thread.currentThread().sleep(1000);
                    }
                    catch(Exception e) {};
                    switch(mHandler.getMode())
                    {
                        case EventHandler.TREEVIEW_MODE:

                            if(dataOfSd.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.sdcard_out));
                                if(mFileMag.getCurrentDir().equals(mFileMag.sdcardList)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_SDCARD));
                                }else if(mFileMag.getCurrentDir().startsWith(tmpstring)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_SDCARD));
                                }
                            }
                            else if(dataOfUsb.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.usb_out));
                                if(mFileMag.getCurrentDir().equals(mFileMag.usbhostList)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_USBHOST));
                                }else if(mFileMag.getCurrentDir().startsWith(tmpstring)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_USBHOST));
                                }
                            }
                            else if(dataOfFlash.contains(tmpstring)){
                                DisplayToast(getResources().getString(R.string.flash_out));
                                if(mFileMag.getCurrentDir().equals(mFileMag.flashList)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
                                }else if(mFileMag.getCurrentDir().startsWith(tmpstring)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
                                }
                            }
                            else
                            {
                                return;
                            }
                            if(mHandler.isMultiSelected())
                            {
                                mTable.killMultiSelect(true);
                                DisplayToast(getResources().getString(R.string.Multi_select_off));
                            }
                            if(mPathLabel != null)
                                mPathLabel.setText(mFileMag.getCurrentDir());
                            break;
                        //anyway,remove the list in media storage
                        case EventHandler.CATALOG_MODE:
                            ArrayList<String> content = null;
                            if(dataOfSd.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.sdcard_out));
                                content = mCataList.DisAttachMediaStorage(tmpstring);
                            }
                            else if(dataOfUsb.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.usb_out));
                                content = mCataList.DisAttachMediaStorage(tmpstring);
                            }
                            else if(dataOfFlash.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.flash_out));
                                content = mCataList.DisAttachMediaStorage(tmpstring);
                            }
                            else
                            {
                                return;
                            }
                            if(content != null)
                            {
                                mHandler.setFileList(content);
                            }
                            break;
                    }
                }
                else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED))
                {
                    switch(mHandler.getMode())
                    {
                        case EventHandler.TREEVIEW_MODE:
                            if(dataOfSd.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.sdcard_in));
                                if(mFileMag.getCurrentDir().equals(mFileMag.sdcardList)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_SDCARD));
                                }else if(mFileMag.getCurrentDir().startsWith(tmpstring)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_SDCARD));
                                }
                            }
                            else if(dataOfUsb.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.usb_in));
                                if(mFileMag.getCurrentDir().equals(mFileMag.usbhostList)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_USBHOST));
                                }else if(mFileMag.getCurrentDir().startsWith(tmpstring)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_USBHOST));
                                }
                            }
                            else if(dataOfFlash.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.flash_in));
                                if(mFileMag.getCurrentDir().equals(mFileMag.flashList)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
                                }else if(mFileMag.getCurrentDir().startsWith(tmpstring)){
                                    mHandler.updateDirectory(mFileMag.getHomeDir(FileManager.ROOT_FLASH));
                                }
                            }
                            else
                            {
                                return;
                            }
                            break;
                        case EventHandler.CATALOG_MODE:
                            if(dataOfSd.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.sdcard_in));
                                mCataList.AttachMediaStorage(tmpstring);
                            }
                            else if(dataOfUsb.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.usb_in));
                                mCataList.AttachMediaStorage(tmpstring);
                            }
                            else if(dataOfFlash.contains(tmpstring))
                            {
                                DisplayToast(getResources().getString(R.string.flash_in));
                                mCataList.AttachMediaStorage(tmpstring);
                            }
                            else
                            {
                                return;
                            }

                            mHandler.setFileList(mCataList.listSort());

                            break;
                    }
                }
                if(mFileMag.isRoot()){
                    mHandler.UpdateButtons(EventHandler.DISABLE_TOOLBTN);
                    closeOptionsMenu();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);
    }

    private void getFocusForButton(int id)
    {
        View v = findViewById(id);
        mHandler.getInitView(v);
        v.setSelected(true);
        mHandler.UpdateButtons(EventHandler.DISABLE_TOOLBTN);
    }

    private void DisplayToast(String str){
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    /*(non Java-Doc)
     * Returns the file that was selected to the intent that
     * called this activity. usually from the caller is another application.
     */
    private boolean returnIntentResults(File data) {
        mReturnIntent = false;
        String action = getIntent().getAction();
        String type = getIntent().getType();
        Intent ret;
        Set<String> categories = getIntent().getCategories();
        if(action.equals(Intent.ACTION_GET_CONTENT)){
            if(categories != null && categories.contains(Intent.CATEGORY_OPENABLE)){
                ret = new Intent();
                Log.d("MessageCompose","file uri: " + Uri.fromFile(data).toString());
                ret.setData(Uri.fromFile(data));
                setResult(RESULT_OK, ret);
            }else{
                if(type != null && type.equals("image/*")){
                    CropImage crop = new CropImage(this, getIntent(), data.getAbsolutePath());
                    ret = crop.saveResourceToIntent();
                    setResult(RESULT_OK, ret);
                }
            }
            finish();
            return true;
        }
        return false;
    }

    private String getCurrentFileName(int position){
        return mHandler.getCurrentFilePath(position);
    }
    /**
     *  To add more functionality and let the user interact with more
     *  file types, this is the function to add the ability.
     *
     *  (note): this method can be done more efficiently
     */
    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        final String item = getCurrentFileName(position);
        File file = new File(item);
        boolean multiSelect = mHandler.isMultiSelected();

        String item_ext = null;

        try {
            item_ext = item.substring(item.lastIndexOf(".") + 1, item.length());

        } catch(IndexOutOfBoundsException e) {
            item_ext = "";
        }
    	
    	/*
    	 * If the user has multi-select on, we just need to record the file
    	 * not make an intent for it.
    	 */
        if(multiSelect) {
            mTable.addMultiPosition(position, file.getPath());

        } else {
            if (file.isDirectory()) {
                if(file.canRead()) {
                    mHandler.updateDirectory(mFileMag.getNextDir(item));
                    mPathLabel.setText(mFileMag.getCurrentDir());
		    		
		    		/*set back button switch to true 
		    		 * (this will be better implemented later)
		    		 */
                    if(!mUseBackKey)
                        mUseBackKey = true;

                } else {
                    Toast.makeText(this, "Can't read folder due to permissions",
                            Toast.LENGTH_SHORT).show();
                }
                if(mFileMag.isRoot()){
                    mHandler.UpdateButtons(EventHandler.DISABLE_TOOLBTN);
                }else{
                    mHandler.UpdateButtons(EventHandler.ENABLE_TOOLBTN);
                }
            }
	    	
	    	/*music file selected--add more audio formats*/
            else if (TypeFilter.getInstance().isMusicFile(item_ext)) {

                if(mReturnIntent) {
                    returnIntentResults(file);
                } else {
                    Intent picIntent = new Intent();
                    picIntent.setAction(android.content.Intent.ACTION_VIEW);
                    picIntent.setDataAndType(Uri.fromFile(file), "audio/*");
                    startActivity(picIntent);
                }
            }
	    	
	    	/*photo file selected*/
            else if(TypeFilter.getInstance().isPictureFile(item_ext)) {

                if (file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        Intent picIntent = new Intent();
                        picIntent.setAction(android.content.Intent.ACTION_VIEW);
                        picIntent.setDataAndType(Uri.fromFile(file), "image/*");
                        startActivity(picIntent);
                    }
                }
            }
	    	
	    	/*video file selected--add more video formats*/
            else if(TypeFilter.getInstance().isMovieFile(item_ext)) {

                if (file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        Intent movieIntent = new Intent();

                        //add by Bevis, for VideoPlayer to create playlist
                        //movieIntent.putExtra(MediaStore.PLAYLIST_TYPE, MediaStore.PLAYLIST_TYPE_CUR_FOLDER);

                        movieIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
                        movieIntent.setAction(android.content.Intent.ACTION_VIEW);
                        movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
                        startActivity(movieIntent);
                    }
                }
            }
	    	
	    	/*zip file */
            else if(TypeFilter.getInstance().isZipFile(item_ext)) {

                if(mReturnIntent) {
                    returnIntentResults(file);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    AlertDialog alert;
                    mZippedTarget = item;
                    CharSequence[] option = {"Extract here", "Extract to..."};

                    builder.setTitle("Extract");
                    builder.setItems(option, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case 0:
                                    String dir = mFileMag.getCurrentDir();
                                    mHandler.unZipFile(item, dir + "/");
                                    break;

                                case 1:
                                    mDetailLabel.setText("Holding " + item +
                                            " to extract");
                                    mHoldingZip = true;
                                    break;
                            }
                        }
                    });

                    alert = builder.create();
                    alert.show();
                }
            }
	    	
	    	/* gzip files, this will be implemented later */
            else if(TypeFilter.getInstance().isGZipFile(item_ext)) {

                if(mReturnIntent) {
                    returnIntentResults(file);

                } else {
                }
            }
	    	
	    	/*pdf file selected*/
            else if(TypeFilter.getInstance().isPdfFile(item_ext)) {

                if(file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        Intent pdfIntent = new Intent();
                        pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
                        pdfIntent.setDataAndType(Uri.fromFile(file),
                                "application/pdf");

                        try {
                            startActivity(pdfIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(this, "Sorry, couldn't find a pdf viewer",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
	    	
	    	/*Android application file*/
            else if(TypeFilter.getInstance().isApkFile(item_ext)){

                if(file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        Intent apkIntent = new Intent();
                        apkIntent.setAction(android.content.Intent.ACTION_VIEW);
                        apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(apkIntent);
                    }
                }
            }
	    	
	    	/* HTML file */
            else if(TypeFilter.getInstance().isHtml32File(item_ext)) {

                if(file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        Intent htmlIntent = new Intent();
                        htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
                        htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");

                        try {
                            startActivity(htmlIntent);
                        } catch(ActivityNotFoundException e) {
                            Toast.makeText(this, "Sorry, couldn't find a HTML viewer",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
	    	
	    	/* text file*/
            else if(TypeFilter.getInstance().isTxtFile(item_ext)) {

                if(file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        Intent txtIntent = new Intent();
                        txtIntent.setAction(android.content.Intent.ACTION_VIEW);
                        txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

                        try {
                            startActivity(txtIntent);
                        } catch(ActivityNotFoundException e) {
                            txtIntent.setType("text/*");
                            startActivity(txtIntent);
                        }
                    }
                }
            }
	    	
	    	/* generic intent */
            else {
                if(file.exists()) {
                    if(mReturnIntent) {
                        returnIntentResults(file);

                    } else {
                        openFile = file;
                        selectFileType_dialog();
                    }
                }
            }
        }
    }

    private void selectFileType_dialog() {
        String mFile = UploadActivity.this.getResources().getString(R.string.open_file);
        String mText = UploadActivity.this.getResources().getString(R.string.text);
        String mAudio = UploadActivity.this.getResources().getString(R.string.audio);
        String mVideo = UploadActivity.this.getResources().getString(R.string.video);
        String mImage = UploadActivity.this.getResources().getString(R.string.image);
        CharSequence[] FileType = {mText,mAudio,mVideo,mImage};
        AlertDialog.Builder builder;
        AlertDialog dialog;
        builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setTitle(mFile);
        builder.setIcon(R.drawable.help);
        builder.setItems(FileType, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent mIntent = new Intent();
                switch(which) {
                    case 0:
                        openType = "text/*";
                        break;
                    case 1:
                        openType = "audio/*";
                        break;
                    case 2:
                        openType = "video/*";
                        break;
                    case 3:
                        openType = "image/*";
                        break;
                }
                mIntent.setAction(android.content.Intent.ACTION_VIEW);
                mIntent.setDataAndType(Uri.fromFile(openFile), openType);
                try {
                    startActivity(mIntent);
                } catch(ActivityNotFoundException e) {
                    Toast.makeText(UploadActivity.this, "Sorry, couldn't find anything " +
                                    "to open " + openFile.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences.Editor editor = mSettings.edit();
        boolean check;
        boolean thumbnail;
        int color, sort, space;
    	
    	/* resultCode must equal RESULT_CANCELED because the only way
    	 * out of that activity is pressing the back button on the phone
    	 * this publishes a canceled result code not an ok result code
    	 */
        if(requestCode == SETTING_REQ && resultCode == RESULT_CANCELED) {
            //save the information we get from settings activity
            check = data.getBooleanExtra("HIDDEN", false);
            thumbnail = data.getBooleanExtra("THUMBNAIL", true);
            color = data.getIntExtra("COLOR", -1);
            sort = data.getIntExtra("SORT", 0);
            space = data.getIntExtra("SPACE", View.VISIBLE);

            editor.putBoolean(PREFS_HIDDEN, check);
            editor.putBoolean(PREFS_THUMBNAIL, thumbnail);
            editor.putInt(PREFS_COLOR, color);
            editor.putInt(PREFS_SORT, sort);
            editor.commit();

            mFileMag.setShowHiddenFiles(check);
            mFileMag.setSortType(sort);
            mHandler.setTextColor(color);
            mHandler.setShowThumbnails(thumbnail);
            if(mHandler.getMode() == EventHandler.TREEVIEW_MODE){
                mHandler.updateDirectory(mFileMag.getNextDir(mFileMag.getCurrentDir()));
            }
        }
    }

    /* ================Menus, options menu and context menu start here=================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int mode = mHandler.getMode();
        switch(mode){
            case EventHandler.TREEVIEW_MODE:
                if(mFileMag.isRoot()){
                    menu.findItem(R.id.action_new_dir).setEnabled(mHoldingMkdir);
                    menu.findItem(R.id.action_search).setEnabled(mHoldingSearch);
                }else{
                    menu.findItem(R.id.action_new_dir).setEnabled(!mHoldingMkdir);
                    menu.findItem(R.id.action_search).setEnabled(!mHoldingSearch);
                }
                break;
            case EventHandler.CATALOG_MODE:
                menu.findItem(R.id.action_new_dir).setEnabled(mHoldingMkdir);
                menu.findItem(R.id.action_search).setEnabled(!mHoldingSearch);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new_dir:
                showDialog(R.id.action_new_dir);
                return true;

            case R.id.action_search:
                showDialog(R.id.action_search);
                return true;

            case R.id.action_logout:
                finish();
                return true;
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                this.startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);

        boolean multi_data = mHandler.hasMultiSelectData();
        AdapterContextMenuInfo _info = (AdapterContextMenuInfo)info;
        if(info == null)
        {
            return;
        }
        mSelectedListItem = mHandler.getData(_info.position);

        if(mHandler.getMode() != EventHandler.TREEVIEW_MODE)
        {
            return;
        }
    	/* is it a directory and is multi-select turned off */
        if(mFileMag.isDirectory(mSelectedListItem) && !mHandler.isMultiSelected()) {
            menu.setHeaderTitle(getResources().getString(R.string.Folder_operations));
            menu.add(0, D_MENU_DELETE, 0, getResources().getString(R.string.Delete_Folder));
            menu.add(0, D_MENU_RENAME, 0, getResources().getString(R.string.Rename_Folder));
            menu.add(0, D_MENU_COPY, 0, getResources().getString(R.string.Copy_Folder));
            menu.add(0, D_MENU_MOVE, 0, getResources().getString(R.string.Move_Folder));
            menu.add(0, D_MENU_ZIP, 0, getResources().getString(R.string.Zip_Folder));
            menu.add(0, D_MENU_PASTE, 0, getResources().getString(R.string.Paste_into_folder)).setEnabled(mHoldingFile ||
                    multi_data);
            menu.add(0, D_MENU_UNZIP, 0, getResources().getString(R.string.Extract_here)).setEnabled(mHoldingZip);
    		
        /* is it a file and is multi-select turned off */
        } else if(!mFileMag.isDirectory(mSelectedListItem) && !mHandler.isMultiSelected()) {
            menu.setHeaderTitle(getResources().getString(R.string.File_Operations));
            menu.add(0, F_MENU_DELETE, 0, getResources().getString(R.string.Delete_File));
            menu.add(0, F_MENU_RENAME, 0, getResources().getString(R.string.Rename_File));
            menu.add(0, F_MENU_COPY, 0, getResources().getString(R.string.Copy_File));
            menu.add(0, F_MENU_MOVE, 0, getResources().getString(R.string.Move_File));
            menu.add(0, F_MENU_ATTACH, 0, getResources().getString(R.string.Email_File));
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
                menu.add(0, F_MENU_BLUETOOTH, 0, getResources().getString(R.string.Bluetooth_File));
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case D_MENU_DELETE:
            case F_MENU_DELETE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.Warning));
                builder.setIcon(R.drawable.warning);
                builder.setMessage(getResources().getString(R.string.Deleting) + mSelectedListItem +
                        getResources().getString(R.string.cannot_be_undone));
                builder.setCancelable(false);

                builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.Delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.deleteFile(mFileMag.getCurrentDir() + "/" + mSelectedListItem);
                    }
                });
                AlertDialog alert_d = builder.create();
                alert_d.show();
                return true;

            case D_MENU_RENAME:
                showDialog(D_MENU_RENAME);
                return true;

            case F_MENU_RENAME:
                showDialog(F_MENU_RENAME);
                return true;

            case F_MENU_ATTACH:
                File file = new File(mFileMag.getCurrentDir() +"/"+ mSelectedListItem);
                Intent mail_int = new Intent();
                mail_int.setAction(android.content.Intent.ACTION_SEND);
                mail_int.setType("application/mail");
                mail_int.putExtra(Intent.EXTRA_BCC, "");
                mail_int.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                try{
                    startActivity(mail_int);
                }
                catch(ActivityNotFoundException e)
                {
                    DisplayToast(getResources().getString(R.string.Activity_No_Found));
                    Log.e(TAG,"activity no found");
                }
                return true;

            case F_MENU_BLUETOOTH:
                File shareFile = new File(mFileMag.getCurrentDir() + "/" + mSelectedListItem);
                Intent bluetooth = new Intent();
                bluetooth.setAction(Intent.ACTION_SEND);
                bluetooth.setType("*/*");
                ComponentName component = new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
                bluetooth.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
                bluetooth.setComponent(component);
                try{
                    startActivity(bluetooth);
                }
                catch(ActivityNotFoundException e)
                {
                    DisplayToast(getResources().getString(R.string.Activity_No_Found));
                    Log.e(TAG,"activity no found");
                }
                return true;
            case F_MENU_MOVE:
            case D_MENU_MOVE:
            case F_MENU_COPY:
            case D_MENU_COPY:
                if(item.getItemId() == F_MENU_MOVE || item.getItemId() == D_MENU_MOVE){
                    mHandler.setDeleteAfterCopy(true);
                }else{
                    mHandler.setDeleteAfterCopy(false);
                }

                mHoldingFile = true;

                mCopiedTarget = mFileMag.getCurrentDir() +"/"+ mSelectedListItem;
                mDetailLabel.setText(getResources().getString(R.string.Holding) + mSelectedListItem);
                return true;


            case D_MENU_PASTE:
                boolean multi_select = mHandler.hasMultiSelectData();

                if(multi_select) {
                    mHandler.copyFileMultiSelect(mFileMag.getCurrentDir() +"/"+ mSelectedListItem);

                } else if(mHoldingFile && mCopiedTarget.length() > 1) {

                    mHandler.copyFile(mCopiedTarget, mFileMag.getCurrentDir() +"/"+ mSelectedListItem);
                    mDetailLabel.setText("");
                }

                mHoldingFile = false;
                return true;

            case D_MENU_ZIP:
                String dir = mFileMag.getCurrentDir();

                mHandler.zipFile(dir + "/" + mSelectedListItem);
                return true;

            case D_MENU_UNZIP:
                if(mHoldingZip && mZippedTarget.length() > 1) {
                    String current_dir = mFileMag.getCurrentDir() + "/" + mSelectedListItem + "/";
                    String old_dir = mZippedTarget.substring(0, mZippedTarget.lastIndexOf("/"));
                    String name = mZippedTarget.substring(mZippedTarget.lastIndexOf("/") + 1, mZippedTarget.length());
                    if(new File(mZippedTarget).canRead() && new File(current_dir).canWrite()) {
                        mHandler.unZipFileToDir(name, current_dir, old_dir);
                        mPathLabel.setText(current_dir);

                    } else {
                        Toast.makeText(this, getResources().getString(R.string.no_permission) + name,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                mHoldingZip = false;
                mDetailLabel.setText("");
                mZippedTarget = "";
                return true;
        }
        return false;
    }

    /* ================Menus, options menu and context menu end here=================*/
    @Override
    protected void  onPrepareDialog(int id, Dialog dialog)
    {
        switch(id) {
            case R.id.action_new_dir:
                TextView label = (TextView)dialog.findViewById(R.id.input_label);
                label.setText(mFileMag.getCurrentDir());
                break;
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog = new Dialog(UploadActivity.this);

        switch(id) {
            case R.id.action_new_dir:
                dialog.setContentView(R.layout.input_layout);
                dialog.setTitle(getResources().getString(R.string.Create_Directory));
                dialog.setCancelable(false);

                ImageView icon = (ImageView)dialog.findViewById(R.id.input_icon);
                icon.setImageResource(R.drawable.newfolder);

                TextView label = (TextView)dialog.findViewById(R.id.input_label);
                label.setText(mFileMag.getCurrentDir());
                final EditText input = (EditText)dialog.findViewById(R.id.input_inputText);

                Button cancel = (Button)dialog.findViewById(R.id.input_cancel_b);
                Button create = (Button)dialog.findViewById(R.id.input_create_b);

                create.setOnClickListener(new OnClickListener() {
                    public void onClick (View v) {
                        if (input.getText().length() >= 1) {
                            if (mFileMag.createDir(mFileMag.getCurrentDir() + "/", input.getText().toString()) == 0){
                                Toast.makeText(UploadActivity.this,
                                        "Folder " + input.getText().toString() + " created",
                                        Toast.LENGTH_LONG).show();

                                input.setText("");
                            }
                            else{
                                Toast.makeText(UploadActivity.this, getResources().getString(R.string.not_created), Toast.LENGTH_SHORT).show();
                            }
                        }

                        input.setText("");
                        dialog.dismiss();
                        String temp = mFileMag.getCurrentDir();
                        mHandler.updateDirectory(mFileMag.getNextDir(temp));
                    }
                });
                cancel.setOnClickListener(new OnClickListener() {
                    public void onClick (View v) {
                        input.setText("");
                        dialog.dismiss();
                    }
                });
                break;
            case D_MENU_RENAME:
            case F_MENU_RENAME:
                dialog.setContentView(R.layout.input_layout);
                dialog.setTitle(getResources().getString(R.string.Rename) + mSelectedListItem);
                dialog.setCancelable(false);

                ImageView rename_icon = (ImageView)dialog.findViewById(R.id.input_icon);
                rename_icon.setImageResource(R.drawable.rename);

                TextView rename_label = (TextView)dialog.findViewById(R.id.input_label);
                rename_label.setText(mFileMag.getCurrentDir());
                final EditText rename_input = (EditText)dialog.findViewById(R.id.input_inputText);

                Button rename_cancel = (Button)dialog.findViewById(R.id.input_cancel_b);
                Button rename_create = (Button)dialog.findViewById(R.id.input_create_b);
                rename_create.setText(getResources().getString(R.string.Rename));

                rename_create.setOnClickListener(new OnClickListener() {
                    public void onClick (View v) {
                        if(rename_input.getText().length() < 1)
                            dialog.dismiss();
                        int ret = mFileMag.renameTarget(mFileMag.getCurrentDir() +"/"+ mSelectedListItem, rename_input.getText().toString());
                        switch(ret){
                            case -1:
                                Toast.makeText(UploadActivity.this, mSelectedListItem + getResources().getString(R.string.renamed_to_exist_file), Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                Toast.makeText(UploadActivity.this, mSelectedListItem + getResources().getString(R.string.be_renamed) +rename_input.getText().toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case -2:
                            default:
                                Toast.makeText(UploadActivity.this, mSelectedListItem + getResources().getString(R.string.not_renamed), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                        String temp = mFileMag.getCurrentDir();
                        mHandler.updateDirectory(mFileMag.getNextDir(temp));
                    }
                });
                rename_cancel.setOnClickListener(new OnClickListener() {
                    public void onClick (View v) {	dialog.dismiss(); }
                });
                break;

            case R.id.action_search:
                dialog.setContentView(R.layout.input_layout);
                dialog.setTitle(getResources().getString(R.string.Search));
                dialog.setCancelable(false);

                ImageView searchIcon = (ImageView)dialog.findViewById(R.id.input_icon);
                searchIcon.setImageResource(R.drawable.search);

                TextView search_label = (TextView)dialog.findViewById(R.id.input_label);
                search_label.setText(getResources().getString(R.string.Search_file));
                final EditText search_input = (EditText)dialog.findViewById(R.id.input_inputText);

                Button search_button = (Button)dialog.findViewById(R.id.input_create_b);
                Button cancel_button = (Button)dialog.findViewById(R.id.input_cancel_b);
                search_button.setText(getResources().getString(R.string.Search));

                search_button.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        String temp = search_input.getText().toString();

                        if (temp.length() > 0)
                            mHandler.searchForFile(temp);
                        dialog.dismiss();
                    }
                });

                cancel_button.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) { dialog.dismiss(); }
                });

                break;
        }
        return dialog;
    }

    /*
     * This will check if the user is at root directory. If so, if they press back
     * again, it will close the application. 
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        String current = mFileMag.getCurrentDir();

        if(keycode == KeyEvent.KEYCODE_SEARCH) {
            showDialog(R.id.action_search);

            return true;

        } else if(keycode == KeyEvent.KEYCODE_BACK &&
                mHandler.getMode() == EventHandler.CATALOG_MODE) {
            finish();

            return false;
        } else if(keycode == KeyEvent.KEYCODE_BACK && mUseBackKey &&
                !(mFileMag.isRoot()) ) {
            if(mHandler.isMultiSelected()) {
                mTable.killMultiSelect(true);
                Toast.makeText(UploadActivity.this, getResources().getString(R.string.Multi_select_off), Toast.LENGTH_SHORT).show();
            }

            mHandler.updateDirectory(mFileMag.getPreviousDir());
            mPathLabel.setText(mFileMag.getCurrentDir());
            if(mFileMag.isRoot()){
                mHandler.UpdateButtons(EventHandler.DISABLE_TOOLBTN);
            }else{
                mHandler.UpdateButtons(EventHandler.ENABLE_TOOLBTN);
            }
            return true;

        } else if(keycode == KeyEvent.KEYCODE_BACK && mUseBackKey &&
                mFileMag.isRoot() )
        {
            Toast.makeText(UploadActivity.this, getResources().getString(R.string.Press_back), Toast.LENGTH_SHORT).show();
            mUseBackKey = false;
            mPathLabel.setText(mFileMag.getCurrentDir());

            return false;

        } else if(keycode == KeyEvent.KEYCODE_BACK && !mUseBackKey &&
                mFileMag.isRoot() )
        {
            finish();

            return false;
        }
        return false;
    }

    @Override
    public void paste(String destination) {
        boolean multi_select = mHandler.hasMultiSelectData();

        if(multi_select) {
            mHandler.copyFileMultiSelect(destination);

        } else if(mHoldingFile && mCopiedTarget.length() > 1) {
            mHandler.copyFile(mCopiedTarget, destination);
            mDetailLabel.setText("");
        }

        mHoldingFile = false;
    }
}
