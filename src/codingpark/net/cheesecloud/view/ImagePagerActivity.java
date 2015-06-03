package codingpark.net.cheesecloud.view;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.view.photoview.CirclePageIndicator;
import codingpark.net.cheesecloud.view.photoview.HackyViewPager;
import codingpark.net.cheesecloud.view.photoview.PageIndicator;
import codingpark.net.cheesecloud.view.photoview.PhotoView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImagePagerActivity extends Activity {
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	private static final String STATE_POSITION = "STATE_POSITION";
	private static final String IMAGES = "images";
	private static final String IMAGE_POSITION = "image_index";

	HackyViewPager pager;
	PageIndicator mIndicator; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_image_pager);

		List<String> list =null;
		Intent intetn = getIntent();
		if(intetn.getStringArrayListExtra("getfirstPathList")!=null){
			list=intetn.getStringArrayListExtra("getfirstPathList");
			int number =intetn.getIntExtra("CLICKIMAGE", 0);
			initImageLoader(this);
			
			options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();
		
			pager = (HackyViewPager) findViewById(R.id.pager);
			pager.setAdapter(new ImagePagerAdapter(list,this));
			pager.setCurrentItem(number);
			
			mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		    mIndicator.setViewPager(pager);
		}else{
			Toast.makeText(this, "获取图片时失败！", Toast.LENGTH_SHORT).show();
		}
	}

	public  void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;
		private Context mContext;

		ImagePagerAdapter(List<String> images,Context context) {
			this.images = images;
			this.mContext=context;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageLoader.displayImage(images.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					//地址失败时显示失败提示
					//Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
}