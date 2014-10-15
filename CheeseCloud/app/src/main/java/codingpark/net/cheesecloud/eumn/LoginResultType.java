package codingpark.net.cheesecloud.eumn;

/**
 * Created by ethanshan on 14-10-15.
 * The class list UserLogin return values
 */
public final class LoginResultType {
    /**
     * Username and Password is right, login success
     */
    public static final int Success         = 0;
    /**
     * The Username is not found
     */
    public static final int UserIsNotFind   = 1;
    /**
     * The User's password is wrong
     */
    public static final int PasswordIsWrong = 2;
    /**
     * SSO error
     */
    public static final int SsoIsError      = 3;
}
