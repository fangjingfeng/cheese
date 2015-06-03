package codingpark.net.cheesecloud.view;

import codingpark.net.cheesecloud.view.listener.OnBackPressedObservable;
import android.app.Application;

public class CustomApplication extends Application{
	   public OnBackPressedObservable onBackPressedObservable;
	   public void onCreate() {
           // TODO Auto-generated method stub
           super.onCreate();
           onBackPressedObservable  = new OnBackPressedObservable();
   }

}
