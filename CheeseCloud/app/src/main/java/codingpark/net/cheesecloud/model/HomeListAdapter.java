package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;

/**
 * Created by ethanshan on 14-9-19.
 * ListAdapter for FragmentHome, convert CloudFile to list item
 */
public class HomeListAdapter extends ArrayAdapter<CloudFile>{
    private static final String TAG         = "HomeListAdapter";

    private LayoutInflater mInflater   = null;
    private final Context mContext;
    private List<CloudFile> mDiskList  = null;

    /*
    public HomeListAdapter(Context context, int resource) {
        super(context, R.layout.home_item_layout, resource);
        this.mContext = context;
        values = mContext.getResources().getStringArray(resource);
    }
    */

    public HomeListAdapter(Context context, int resource, List<CloudFile> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDiskList = objects;
    }

    /*
    public HomeListAdapter(Context context, String[] values) {
        super(context, R.layout.home_item_layout, values);
        this.mContext = context;
        this.values = values;
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Get position: " + position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_item_layout, parent, false);
            holder = new ViewHolder();
            holder.tabHomeItemIcon = (ImageView)convertView.findViewById(R.id.tab_home_item_icon_iv);
            holder.tabHomeItemTitle = (TextView)convertView.findViewById(R.id.tab_home_item_title_tv);
            holder.tabHomeItemArrow = (ImageView)convertView.findViewById(R.id.tab_home_item_new_arraw_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        CloudFile disk = mDiskList.get(position);
        // Rendering the item view by the disk object information
        // Icon
        if (disk.getRemote_id().equals(AppConfigs.current_remote_user_id)) {
            holder.tabHomeItemIcon.setImageResource(R.drawable.tab_home_cloud_disk_item_icon_normal_img);
        } else {
            holder.tabHomeItemIcon.setImageResource(R.drawable.tab_home_res_lib_item_icon_normal);
        }
        // Name
        holder.tabHomeItemTitle.setText(disk.getFilePath());
        // Arrow needn't change
        return convertView;
    }

    private static class ViewHolder {
        public ImageView tabHomeItemIcon    = null;
        public TextView tabHomeItemTitle    = null;
        public ImageView tabHomeItemArrow   = null;
    }
}
