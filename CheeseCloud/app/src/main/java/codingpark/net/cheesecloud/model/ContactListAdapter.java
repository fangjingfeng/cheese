package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import codingpark.net.cheesecloud.R;

/**
 * Created by ethanshan on 14-9-22.
 */
public class ContactListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "ContactListAdapter";

    private Context mContext        = null;
    private String[] values         = null;

    public ContactListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ContactListAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        mContext = context;
        this.values = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Get position: " + position);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.home_item_layout, parent, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.tab_home_item_icon_iv);
        // change the icon for Windows and iPhone
        String s = values[position];
        if (s.equals("old_friends")) {
            imageView.setImageResource(R.drawable.tab_contact_old_friends_item_normal);
        } else if (s.equals("new_friends")){
            imageView.setImageResource(R.drawable.tab_contact_new_friends_normal);
        } else if (s.equals("group_chat")) {
            imageView.setImageResource(R.drawable.tab_contact_group_chat_normal);
        } else if (s.equals("circle_of_friends")) {
            imageView.setImageResource(R.drawable.tab_contact_circle_of_friends_normal);
        }
        return v;
    }
}
