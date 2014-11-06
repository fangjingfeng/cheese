package codingpark.net.cheesecloud.enumr;


/**
 * Mark the file type. Because upload file and folder use the same table --
 * uploadfile, use filetype column to mark the record stand for a file or a folder.
 *
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:26:22
 */
public class UploadFileType {

    /**
     * Mark the file is file type
     */
    public static final int TYPE_FILE = 0;
    /**
     * Mark the file is folder type
     */
    public static final int TYPE_FOLDER = 1;

    public UploadFileType(){

    }

    public void finalize() throws Throwable {

    }

}
