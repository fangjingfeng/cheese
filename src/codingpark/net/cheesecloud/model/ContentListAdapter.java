package codingpark.net.cheesecloud.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.enumr.CloudFileType;
import codingpark.net.cheesecloud.view.dummy.utils.ThumbnailCreator;

/**
 * Created by ethanshan on 14-9-19.
 * ListAdapter for FragmentHome, convert CloudFile to list item
 */
public class ContentListAdapter extends ArrayAdapter<CloudFile>{
    private static final String TAG         = "ContentListAdapter";

    private LayoutInflater mInflater   = null;
    private final Context mContext;
    private List<CloudFile> mDiskList  = null;
    
    public ContentListAdapter(Context context, int resource, List<CloudFile> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDiskList = objects;
        
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.cloud_item_layout, parent, false);
            holder = new ViewHolder();
            holder.fileThumb = (ImageView)convertView.findViewById(R.id.file_thumb);
            holder.fileNameView = (TextView)convertView.findViewById(R.id.file_name_view);
            holder.fileSizeView = (TextView)convertView.findViewById(R.id.file_size_view);
            holder.fileDateView = (TextView)convertView.findViewById(R.id.file_date_view);
            
            //holder.multiSelectCheckBox = (CheckBox)convertView.findViewById(R.id.multiselect_checkbox);
            //holder.multiSelectCheckBox.setOnCheckedChangeListener(mCheckedListener);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        
        
       /* // Update holder content
        CloudFile file = mFileFolderList.get(position);
        // multiSelectCheckBox
        holder.multiSelectCheckBox.setTag(position);
        if (mSelectedPositions != null && mSelectedPositions.contains(position))
            holder.multiSelectCheckBox.setChecked(true);
        else
            holder.multiSelectCheckBox.setChecked(false);
        // fileThumb/fileSizeView
        if (file.getFileType() == CloudFileType.TYPE_FOLDER) {
            holder.fileThumb.setImageResource(R.drawable.folder);
            holder.fileSizeView.setVisibility(View.INVISIBLE);
        } else {
            holder.fileThumb.setImageResource(ThumbnailCreator.getDefThumbnailsByName(file.getFilePath()));
            holder.fileSizeView.setText(file.getFileSize() + "");
        }
        // fileNameView
        holder.fileNameView.setText(file.getFilePath());
        // fileDateView
        holder.fileDateView.setText(file.getCreateDate());*/

        return convertView;
    }

    /**
     * File/Directory list item view encapsulate
     */
    private static class ViewHolder {
        ImageView fileThumb;
        TextView fileNameView;
        TextView fileSizeView;
        TextView fileDateView;
        CheckBox multiSelectCheckBox;
    }

  
}
