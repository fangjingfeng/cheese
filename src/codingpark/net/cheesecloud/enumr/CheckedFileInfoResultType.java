package codingpark.net.cheesecloud.enumr;


/**
 * Mark ClientWS.checkFileInfo() return result
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:09:05
 */
public class CheckedFileInfoResultType {

    /**
     * The file is created success on server
     */
    public static final int RESULT_CHECK_SUCCESS = 2;
    /**
     * This file already exist on server
     */
    public static final int RESULT_QUICK_UPLOAD = 1;

    public CheckedFileInfoResultType(){

    }

    public void finalize() throws Throwable {

    }

}
