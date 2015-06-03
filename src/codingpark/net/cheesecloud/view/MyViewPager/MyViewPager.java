package codingpark.net.cheesecloud.view.MyViewPager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager {

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	// 自定义的viewpager不要去拦截相应的事件，传递给内部控件去消费
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}
	
	
}
