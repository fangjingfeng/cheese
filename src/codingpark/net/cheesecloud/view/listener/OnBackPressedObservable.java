package codingpark.net.cheesecloud.view.listener;

import java.util.Observable;

public class OnBackPressedObservable extends Observable {
    public void onBackPressed() {
    		//通知返回按钮被点击
            setChanged();
            notifyObservers(this);
    }
}