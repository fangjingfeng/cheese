package codingpark.net.cheesecloud.eumn;

/**
 * Created by ethanshan on 14-10-24.
 * Mark ClientWS.checkFileInfo() return result
 */
public class CheckedFileInfoResultType {
    /**
     * This file already exist on server
     */
    public static final int RESULT_QUICK_UPLOAD     = 1;
    /**
     * The file is created success on server
     */
    public static final int RESULT_CHECK_SUCCESS    = 2;
}
