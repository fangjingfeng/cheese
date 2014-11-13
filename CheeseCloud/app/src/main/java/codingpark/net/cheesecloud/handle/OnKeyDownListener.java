package codingpark.net.cheesecloud.handle;

/**
 * Used by CloudFilesActivity's three fragments
 * When the fragment need to handle key down event, just need implement
 * this interface, override onBackKeyDown. Then CloudFilesActivity will
 * call onBackKeyDown when user click back key.
 * Created by ethanshan on 14-11-5.
 */
public interface OnKeyDownListener {
    public boolean onBackKeyDown();
}
