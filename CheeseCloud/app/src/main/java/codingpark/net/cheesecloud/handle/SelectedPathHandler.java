package codingpark.net.cheesecloud.handle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import codingpark.net.cheesecloud.R;

/**
 * Created by ethanshan on 14-10-20.
 */
public class SelectedPathHandler {

    private Context mContext                        = null;
    private ArrayList<String> mFolderList           = null;
    private UploadListAdapter mAdapter              = null;

    public SelectedPathHandler(Context context, UploadListAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        ImageView icon;
        TextView rightView;
    }

    public class UploadListAdapter extends ArrayAdapter<String> {

        public UploadListAdapter() {
            super(mContext, R.layout.upload_item_layout, mFolderList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.select_path_item_layout, parent, false);

                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
                // Update icon src
                holder.icon.setImageResource(R.drawable.folder);
                holder.rightView = (TextView)convertView.findViewById(R.id.fileNameView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.rightView.setText(mFolderList.get(position));

            return convertView;
        }
    }
}
