package codingpark.net.cheesecloud.handle;

/**
 * Created by ethanshan on 14-11-7.
 * The web service call task finish callback interface
 */
public interface OnWSTaskFinishListener<T> {
    /**
     * Callback when web service query finish
     */
    public void onWSTaskDataFinish(T data);

    //public void onWSTaskListFinish(List<T> list);
}
