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

/**
 * Created by ethanshan on 14-9-19.
 */
public class HomeListAdapter extends ArrayAdapter<String>{

    private final Context mContext;
    private final String[] values;
    private static final String TAG     = "HomeListAdapter";

    public HomeListAdapter(Context context, int resource) {
        super(context, R.layout.home_item_layout, resource);
        this.mContext = context;
        values = mContext.getResources().getStringArray(resource);
    }
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
        ImageView imageView = (ImageView) v.findViewById(R.id.tab_home_item_new_iv);
        // change the icon for Windows and iPhone
        String s = values[position];
        if (s.equals("news")) {
            imageView.setImageResource(R.drawable.tab_home_news_items_normal);
        } else if (s.equals("cloud_disk")){
            imageView.setImageResource(R.drawable.tab_home_cloud_disk_itmes_normal);
        } else if (s.equals("resource_library")) {
            imageView.setImageResource(R.drawable.tab_home_resource_library_item_normal);
        } else if (s.equals("small_class")) {
            imageView.setImageResource(R.drawable.tab_home_small_class_item_normal);
        } else if (s.equals("temp_screen")) {
            imageView.setImageResource(R.drawable.tab_home_temp_screen_item_normal);
        }
        return v;
    }
}
