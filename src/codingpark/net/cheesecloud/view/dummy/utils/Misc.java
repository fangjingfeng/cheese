package codingpark.net.cheesecloud.view.dummy.utils;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;

import codingpark.net.cheesecloud.CheeseConstants;

/**
 * Created by ethanshan on 14-11-18.
 * Some helper function. Such as merge path, convert date format....
 */
public class Misc {

    /**
     * Get current date format string
     * @return
     *  String: current date string, such as 2014/10/17 16:44:23
     */
    public static String getDateString() {
        return DateFormat.format("yyyy/MM/dd HH:mm:ss", new Date(System.currentTimeMillis())).toString();
    }

    /**
     * Merge prefix_path and post_path with specific
     * @param prefix_path Prefix path string(Normal is a absolutely path)
     * @param post_path Post path string(Normal is directory)
     * @return Merged path
     */
    public static String mergePath(String prefix_path, String post_path) {
        return prefix_path + CheeseConstants.SEPARATOR + post_path;
    }

    /**
     * Simulate the "mkdir -p" command, create path include all directory
     * @param path The target path string
     * @return If create all directory success, return true. Else return false;
     */
    public static boolean createFullDir(String path) {
        String[] dirs = path.split(CheeseConstants.SEPARATOR);
        String r_path = CheeseConstants.SEPARATOR;
        File file = null;
        for (int i =0; i < dirs.length; i++) {
            if (dirs[i] != null && !dirs[i].isEmpty()) {
                r_path = Misc.mergePath(r_path, dirs[i]);
                file = new File(r_path);
                if (!file.exists()) {
                    // If create directory failed, return false
                    if (!file.mkdir())
                        return false;
                }
            }
        }
        return true;
    }


    /**
     * 下载的文件/文件夹存储根文件夹。
     * Such as /sdcard/CheeseCloudDownload
     * @return The download root folder
     */
    public static String getDownloadRootDir() {
        String prefix_path = Environment.getExternalStorageDirectory().toString();
        String path = Misc.mergePath(prefix_path, CheeseConstants.DOWNLOAD_ROOT_DIR_NAME);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    public static String getExtesion(String path) {
        String ext = path.substring(path.lastIndexOf(".") + 1);
        return ext;
    }
}
