package codingpark.net.cheesecloud.utils;

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
}
