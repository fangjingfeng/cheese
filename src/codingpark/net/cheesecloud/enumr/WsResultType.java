package codingpark.net.cheesecloud.enumr;


/**
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:33:41
 */
public class WsResultType {
    public static final int NetworkError = -1;

    /**
     * Operation permission denied
     */
    public static final int AuthFaild = 1;
    /**
     * Operation failed
     */
    public static final int Faild = 3;
    /**
     * Operation success
     */
    public static final int Success = 0;
    /**
     * Operate target not exist
     */
    public static final int TargetNotFind = 2;

    public WsResultType(){

    }

    public void finalize() throws Throwable {

    }

}
