package codingpark.net.cheesecloud.utils;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by ethanshan on 14-11-18.
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
}
