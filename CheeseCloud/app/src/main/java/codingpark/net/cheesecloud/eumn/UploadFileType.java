package codingpark.net.cheesecloud.eumn;

/**
 * Created by ethanshan on 14-10-23.
 * Mark the file type. Because upload file and folder use the
 * same table --uploadfile, use filetype column to mark the
 * record stand for a file or a folder.
 */
public class UploadFileType {
    /**
     * Mark the file is file type
     */
    public static final int TYPE_FILE       = 0;
    /**
     * Mark the file is folder type
     */
    public static final int TYPE_FOLDER     = 1;
}
