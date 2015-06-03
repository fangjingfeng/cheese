package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.ListFragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import com.lidroid.xutils.BitmapUtils;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnKeyDownListener;
import codingpark.net.cheesecloud.handle.OnSelectUploadChangedListener;
import codingpark.net.cheesecloud.utils.CommonAdapter;
import codingpark.net.cheesecloud.utils.FlowConverter;
import codingpark.net.cheesecloud.utils.ImageUtis;
import codingpark.net.cheesecloud.utils.ImageUtis.Type;
import codingpark.net.cheesecloud.utils.ViewHolder;
import codingpark.net.cheesecloud.view.imegeutils.ImageLoader;
import codingpark.net.cheesecloud.wsi.ImageFloder;

/**
 * 显示手机中图片列表
 */
public class FragmentSelectUploadImage extends ListFragment implements  OnKeyDownListener {
	
	public static final String TAG = FragmentSelectUploadImage.class.getSimpleName();
    public static final int CATEGORY_LIST_MODE  = 1;
    public static final int ITEM_LIST_MODE  = 2;
	private Context mContext        = null;
	private LayoutInflater mInflater ;
	private HashSet<String> mDirPaths = new HashSet<String>();
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	private int mPicsSize;
    private boolean isAlive                             = false;
    private ImageCategoryAdapter mCategoryAdapter;
	
    private PathBarItemClickListener mPathBatItemListener;
    private LinearLayout mPathBar;
    private LinearLayout mListContainer;
    private ProgressBar mLoadingView;
    private int mListMode ;
    public  ArrayList<String> mSelectedImage;
    private String fordePathName;
    
    public static FragmentSelectUploadImage newInstance(String param1, String param2) {
        FragmentSelectUploadImage fragment = new FragmentSelectUploadImage();
        return fragment;
    }
    public FragmentSelectUploadImage() {}
    
    private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			data2View();
		}
	};
	private GridView mGirdView;
	private OnSelectUploadChangedListener mOnSelectUploadChanged;
	private ImageFloder imageFloder;
    
	//填充listView
	public void data2View(){
		setLoadingViewVisible(false);
		if(mImageFloders.size()>0){
			mCategoryAdapter = new  ImageCategoryAdapter(mContext,R.layout.list_dir_item,mImageFloders);
			setListAdapter(mCategoryAdapter);
		}else{
			Toast.makeText(mContext, "亲，你手机真干净啊！", Toast.LENGTH_SHORT).show();
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPathBatItemListener = new PathBarItemClickListener();
        mSelectedImage= new ArrayList<String>();
        setLoadingViewVisible(false);
        getImages();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	mOnSelectUploadChanged = (OnSelectUploadChangedListener)activity;
            mContext = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_select_upload_images, null);
        
        mListContainer = (LinearLayout)rootView.findViewById(R.id.listcontainer);
        mLoadingView = (ProgressBar)rootView.findViewById(R.id.loading);
        mGirdView = (GridView) rootView.findViewById(R.id.id_gridView); 
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mPathBar = (LinearLayout)getView().findViewById(R.id.pathBarContainer);
        setUpdatePathBar(mPathBar);
        refreshPathBar();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	  mListMode =ITEM_LIST_MODE; 
    	  System.out.println("mListMode --->"+mListMode);
		  getListView().setVisibility(View.GONE);
	      mGirdView.setVisibility(View.VISIBLE);
	   	  imageFloder = mImageFloders.get(position);
	   	  System.out.println("imageFloder.getDir()"+imageFloder.getDir().substring(imageFloder.getDir().lastIndexOf("/")));
	   	  refreshPathBar();
	   	  fordePathName=imageFloder.getDir();
	   	  mGirdView.setAdapter(new MyAdapter(mContext,Arrays.asList(new File(imageFloder.getDir()).list()),R.layout.grid_item,imageFloder.getDir()));
    }
    

    public class MyAdapter extends CommonAdapter<String>{
	private String mDirPath;
	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,String dirPath){
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(ViewHolder helper, final String item){
		//设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		//设置no_selected
				helper.setImageResource(R.id.id_item_select,
						R.drawable.picture_unselected);
		//设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener(){
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v){

				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item)){
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else
				// 未选择该图片
				{
					mSelectedImage.add(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}
				selecteImages(mSelectedImage);
			}
			
		});
			
			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedImage.contains(mDirPath + "/" + item)){
				mSelect.setImageResource(R.drawable.pictures_selected);
				mImageView.setColorFilter(Color.parseColor("#77000000"));
			}
	
		}
    }
  
    public void selecteImages(ArrayList<String> list){
    	mOnSelectUploadChanged.onSelectUploadChanged(list);
    }
    
    @Override
    public boolean onBackKeyDown() {
        if (mListMode == CATEGORY_LIST_MODE) {
            return false;
        } else {
            //clearMultiSelect();
            mListMode = CATEGORY_LIST_MODE;
            setListAdapter(mCategoryAdapter);
            mCategoryAdapter.notifyDataSetChanged();
            refreshPathBar();
            return true;
        }
    }

    private static final class CategoryViewHolder {
        public ImageView bucketThumbView    = null;
        public TextView bucketNameView      = null;
        public TextView countView           = null;
    }

    /**
     * The {@see CATEGORY_LIST_MODE} adapter
     */
    private class ImageCategoryAdapter extends ArrayAdapter<ImageFloder> {
    	
        public ImageCategoryAdapter(Context context, int resource, List<ImageFloder> listImageFloder) {
            super(context, resource, listImageFloder);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ImageFloder item = mImageFloders.get(position);
            CategoryViewHolder holder = null;
            if (convertView == null) {
                holder = new CategoryViewHolder();
                convertView = mInflater.inflate(R.layout.list_dir_item, null);
                holder.bucketThumbView = (ImageView)convertView.findViewById(R.id.id_dir_item_image);
                holder.bucketNameView = (TextView)convertView.findViewById(R.id.id_dir_item_name);
                holder.countView = (TextView)convertView.findViewById(R.id.id_dir_item_count);
                convertView.setTag(holder);
            } else {
                holder = (CategoryViewHolder)convertView.getTag();
            }
            
            if(item.getFirstImagePath()!=null){
            	ImageUtis.getInstance(3,Type.LIFO).loadImage(item.getFirstImagePath(), holder.bucketThumbView);
            }else{
            	holder.bucketThumbView.setImageResource(R.drawable.ic_launcher);
            }
            holder.bucketNameView.setText(item.getDir());
            holder.countView.setText(item.getCount() + "");
            return convertView;
        }
    }

    private void setUpdatePathBar(LinearLayout pathBar) {
        mPathBar = pathBar;
        // Initial path bar default item, Disk, this item is root.
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView)inflater.inflate(R.layout.path_bar_item_layout, null);
        textView.setTag(0);
        String path = "相册";
        textView.setText(path);
        textView.setOnClickListener(mPathBatItemListener);
        mPathBar.addView(textView);
    }
    
    private void refreshPathBar() {
        int pathBarCount = mPathBar.getChildCount();
        Log.d(TAG, "pathStackCount: " + pathBarCount);

        if (mListMode == CATEGORY_LIST_MODE){
            if (pathBarCount > 1)
                mPathBar.removeViewAt(pathBarCount - 1);
        } else if (mListMode == ITEM_LIST_MODE){
            if (pathBarCount == 1) {
                TextView textView = (TextView)mInflater.inflate(R.layout.path_bar_item_layout, null);
                textView.setTag(1);
                String path = "";
                path = imageFloder.getDir().substring(imageFloder.getDir().lastIndexOf("/"));
                Log.d(TAG, "path is " + path);
                textView.setText(path);
                textView.setOnClickListener(mPathBatItemListener);
                mPathBar.addView(textView);
            }
        }
    }

    private void setLoadingViewVisible(boolean visible){
        if(null != mLoadingView && null != mListContainer){
            mListContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
            mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
    
    private class PathBarItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int index = Integer.valueOf(v.getTag().toString());
            if (index == 0) {
                //clearMultiSelect();
            	mSelectedImage.clear();
            	if(mOnSelectUploadChanged !=null){
            		mOnSelectUploadChanged.onSelectUploadChanged(new ArrayList());
            	}
                mListMode = CATEGORY_LIST_MODE;
                setListAdapter(mCategoryAdapter);
                mCategoryAdapter.notifyDataSetChanged();
                refreshPathBar();
            }
        }
    }

    
	private void getImages(){
		//判断有没有外在存储
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(mContext, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		//mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		setLoadingViewVisible(true);

		new Thread(new Runnable(){
			@Override
			public void run(){
				String firstImage = null;
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = mContext.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext()){
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null){
						firstImage = path;
					}
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)){
						continue;
					} else{
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter(){
						@Override
						public boolean accept(File dir, String filename){
							if (filename.endsWith(".jpg")|| filename.endsWith(".png")|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);
				}
				mCursor.close();
				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
	}
}
