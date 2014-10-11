package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import codingpark.net.cheesecloud.DevicePathUtils;

public class FileManager {
    private static final String TAG = "FileManager";

    // TODO comment BUFFER
    private static final int BUFFER = 		2048;

    /**
     * No sort
     */
    private static final int SORT_NONE = 	0;
    /**
     * Sort by alpha of file name
     */
    private static final int SORT_ALPHA = 	1;
    /**
     * Sort by file type
     */
    private static final int SORT_TYPE = 	2;

    /**
     * ArrayList store all internal flash storage disk path of system
     */
    private ArrayList<String> flashPathList;
    /**
     * ArrayList store all sdcard storage disk path of system
     */
    private ArrayList<String> sdcardPathList;

    /**
     * Root paths of flash/sdcard
     */
    private DevicePathUtils mDevices;

    /**
     * Switch of weather show hidden files, exp: .profile/.git...
     */
    private boolean mShowHiddenFiles = false;
    /**
     * Setting of the sort type, which display files and directory
     * Default: sort by alpha
     */
    private int mSortType = SORT_ALPHA;
    /**
     * The directory contain files count
     */
    private long mDirSize = 0;
    /**
     * The stack data structure, which store current directory path
     */
    private Stack<String> mPathStack;
    /**
     * A list store current directory's files and dir name
     */
    private ArrayList<String> mDirContent;

    /**
     * The key stored in mPathStack stand for Disk(Internal+External Storage)
     */
    public String diskName = "/";


    private Context mContext ;

    /**
     * Constructs an object of the class
     * <br>
     * this class uses a stack to handle the navigation of directories.
     */
    public FileManager(Context context) {
        mDirContent = new ArrayList<String>();
        mPathStack = new Stack<String>();
        mContext = context;

        // Initial flash/sdcard storage disk mounted path
        mDevices = new DevicePathUtils(context);
        flashPathList = mDevices.getInterStoragePath();
        sdcardPathList = mDevices.getSdStoragePath();

        // Initial file path stack with root path
        mPathStack.push(diskName);
    }

    /**
     * This will return a string of the current directory path
     * @return the current directory
     */
    public String getCurrentDir() {
        return mPathStack.peek();
    }

    /**
     * Switch mPathStack to root and return root subdirectories list
     * Subdirectories include: all flash + all sdcard(absolute path)
     * @return	the flashes + sdcards path list
     */
    public ArrayList<String> switchToRoot() {
        mPathStack.clear();
        mPathStack.push(diskName);
        ArrayList<String> r_list = new ArrayList<String>();
        r_list.addAll(mDevices.getMountedPath(sdcardPathList));
        r_list.addAll(mDevices.getMountedPath(flashPathList));
        return r_list;
    }

    /**
     * Switch to previous directory and return the subdirectories' list
     * @return	returns the previous subdirectories' list
     */
    public ArrayList<String> switchToPreviousDir() {
        int size = mPathStack.size();

        if (size >= 2)
            mPathStack.pop();

        else if(size == 0)
            mPathStack.push(diskName);

        String st = mPathStack.peek();
        if (st.equals(diskName)) {
            ArrayList<String> r_list = new ArrayList<String>();
            r_list.addAll(flashPathList);
            r_list.addAll(sdcardPathList);
            return r_list;
        }
        else{
            return populate_list();
        }
    }
    /**
     * This will tell if current path is root
     * @return	is root?
     */
    public boolean isRoot() {
        //This will eventually be placed as a settings item
        String r_name = mPathStack.peek();

        if(r_name.equals(diskName)) {
            return true;
        }
        return false;
    }


    /**
     * This will determine if hidden files and folders will be visible to the
     * user.
     * @param
     *  choice	true if user is viewing hidden files, false otherwise
     */
    public void setShowHiddenFiles(boolean choice) {
        mShowHiddenFiles = choice;
    }

    /**
     * Set list file order
     * @param type
     */
    public void setSortType(int type) {
        mSortType = type;
    }


    /**
     * Update current directory to path, and
     * return the path dir content
     * @param path
     *  The directory
     * @return
     *  The directory contents
     */
    public ArrayList<String> switchToNextDir(String path) {
        if(!path.equals(mPathStack.peek())) {
            mPathStack.push(path);
        }
        if (diskName.equals(path)) {
            ArrayList<String> r_list = new ArrayList<String>();
            r_list.addAll(mDevices.getMountedPath(sdcardPathList));
            r_list.addAll(mDevices.getMountedPath(flashPathList));
            return r_list;
        }
        else {
            return populate_list();
        }
    }


    /**
     * Alph comparator, use to compare the filename
     */
    private static final Comparator alph = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            return arg0.toLowerCase().compareTo(arg1.toLowerCase());
        }
    };

    /**
     * Extension comparator, use to compare the file extension
     */
    private static final Comparator type = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            String ext = null;
            String ext2 = null;

            try {
                ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length());
                ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length());

            } catch (IndexOutOfBoundsException e) {
                return 0;
            }

            return ext.compareTo(ext2);
        }
    };

    /*
     * This function get the first item of mPathStack, and return the item subdirectories
     * and files of the item(Directory) list.
     * @return
     */
    private ArrayList<String> populate_list() {
        if(!mDirContent.isEmpty())
            mDirContent.clear();
        try {
            String path = mPathStack.peek();
            File file = new File(path);

            if(file.exists() && file.canRead() && file.isDirectory()) {
                File[] fList = file.listFiles();
                if(fList != null) {
                    int len = fList.length;
					
					/* add files/folder to arraylist depending on hidden status */
                    for (int i = 0; i < len; i++) {
                        String name = fList[i].getName();
                        if(!mShowHiddenFiles) {
                            if(name.charAt(0) != '.')
                                mDirContent.add(name);

                        } else {
                            mDirContent.add(name);
                        }
                    }
			
					/* sort the arraylist that was made from above for loop */
                    switch(mSortType) {
                        case SORT_NONE:
                            //no sorting needed
                            break;

                        case SORT_ALPHA:
                            Object[] tt = mDirContent.toArray();
                            mDirContent.clear();

                            Arrays.sort(tt, alph);

                            for (Object a : tt){
                                mDirContent.add((String)a);
                            }
                            break;

                        case SORT_TYPE:
                            Object[] t = mDirContent.toArray();
                            String dir = mPathStack.peek();

                            Arrays.sort(t, type);
                            mDirContent.clear();

                            for (Object a : t){
                                if(new File(dir + "/" + a).isDirectory())
                                    mDirContent.add(0, (String)a);
                                else
                                    mDirContent.add((String)a);
                            }
                            break;
                    }
                }
            }
        }catch(Exception e) {
			/* clear any operate made above */
            Log.e("FileManager", "unknow exception");
            mDirContent.clear();
        }
        return mDirContent;
    }

    /*
     * This function will be rewritten as there is a problem getting
     * the directory size in certain folders from root. ex /sys, /proc.
     * The app will continue until a stack overflow. get size is fine uder the
     * sdcard folder.
     *
     * @param path
     */
    /*
    private void get_dir_size(File path) {
        File[] list = path.listFiles();
        int len;

        if(list != null) {
            len = list.length;

            for (int i = 0; i < len; i++) {
                if(list[i].isFile() && list[i].canRead()) {
                    mDirSize += list[i].length();

                } else if(list[i].isDirectory() && list[i].canRead()) {
                    get_dir_size(list[i]);
                }
            }
        }
    }
    */

}
