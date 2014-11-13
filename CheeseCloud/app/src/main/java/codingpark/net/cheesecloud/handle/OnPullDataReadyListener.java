package codingpark.net.cheesecloud.handle;

/**
 * Created by ethanshan on 14-11-11.
 * When Pull data from server task completed, call this callback function.
 * Current will hide loading view and show list view
 */
public interface OnPullDataReadyListener {

    public void onPullDataReady(int result);

}
