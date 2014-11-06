package codingpark.net.cheesecloud.enumr;


/**
 * The class list UserLogin return values
 * @author Ethan Shan
 * @version 1.0
 * @created 06-十一月-2014 16:15:46
 */
public class LoginResultType {

    /**
     * The User's password is wrong
     */
    public static final int PasswordIsWrong = 2;
    /**
     * SSO error
     */
    public static final int SsoIsError = 3;
    /**
     * Username and Password is right, login success
     */
    public static final int Success = 0;
    /**
     * The Username is not found
     */
    public static final int UserIsNotFind = 1;

    public LoginResultType(){

    }

    public void finalize() throws Throwable {

    }

}
