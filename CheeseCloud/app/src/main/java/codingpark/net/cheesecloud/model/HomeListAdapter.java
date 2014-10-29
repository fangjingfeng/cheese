package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.view.FragmentHome;

/**
 * Created by ethanshan on 14-9-19.
 */
public class HomeListAdapter extends ArrayAdapter<String>{

    private final Context mContext;
    private final String[] values;
    private static final String TAG     = "HomeListAdapter";

    /*
    public HomeListAdapter(Context context, int resource) {
        super(context, R.layout.home_item_layout, resource);
        this.mContext = context;
        values = mContext.getResources().getStringArray(resource);
    }
    */

    public HomeListAdapter(Context context, String[] values) {
        super(context, R.layout.home_item_layout, values);
        this.mContext = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Get position: " + position);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.home_item_layout, parent, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.tab_home_item_icon_iv);
        TextView textView = (TextView) v.findViewById(R.id.tab_home_item_title_tv);
        // change the icon for Windows and iPhone
        String s = values[position];
        if (s.equals(FragmentHome.TAB_HOME_ITEM_NEWS)) {
            imageView.setImageResource(R.drawable.tab_home_news_item_icon_normal_img);
            textView.setText(R.string.tab_home_item_news_title);
            v.setTag(FragmentHome.TAB_HOME_ITEM_NEWS);
        } else if (s.equals(FragmentHome.TAB_HOME_ITEM_CLOUD_DISK)){
            imageView.setImageResource(R.drawable.tab_home_cloud_disk_item_icon_normal_img);
            textView.setText(R.string.tab_home_item_cloud_disk_title);
            v.setTag(FragmentHome.TAB_HOME_ITEM_CLOUD_DISK);
        } else if (s.equals(FragmentHome.TAB_HOME_ITEM_RESOURCE_LIBRARY)) {
            imageView.setImageResource(R.drawable.tab_home_res_lib_item_icon_normal);
            textView.setText(R.string.tab_home_item_res_lib_title);
            v.setTag(FragmentHome.TAB_HOME_ITEM_RESOURCE_LIBRARY);
        } else if (s.equals(FragmentHome.TAB_HOME_ITEM_SMALL_CLASS)) {
            imageView.setImageResource(R.drawable.tab_home_small_class_item_icon_normal);
            textView.setText(R.string.tab_home_item_small_class_title);
            v.setTag(FragmentHome.TAB_HOME_ITEM_SMALL_CLASS);
        } else if (s.equals(FragmentHome.TAB_HOME_ITEM_TEMP_SCREEN)) {
            imageView.setImageResource(R.drawable.tab_home_temp_screen_item_icon_normal);
            textView.setText(R.string.tab_home_item_temp_screen_title);
            v.setTag(FragmentHome.TAB_HOME_ITEM_TEMP_SCREEN);
        }
        return v;
    }
}
