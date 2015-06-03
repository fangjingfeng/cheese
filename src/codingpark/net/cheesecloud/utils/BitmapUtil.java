package codingpark.net.cheesecloud.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.googlecode.mp4parser.h264.BTree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class BitmapUtil {
	private static Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	
	public static void downloadBitmap(String path,ImageView rowImage,int windowHeight,int windowWidth){
		 new MyDownLaodImag(path,rowImage,windowHeight,windowWidth).execute();
	}
	
	static class MyDownLaodImag extends AsyncTask{
		private String imagePath; 
		private ImageView rowImage; 
		private int windowHeight ;
		private int windowWidth ;
		private Bitmap bitmap;
		public MyDownLaodImag(String path,ImageView rowImage,int windowHeight,int windowWidth){
			this.imagePath =path;
			this.rowImage =rowImage;
			this.windowHeight =windowHeight;
			this.windowWidth= windowWidth;
		}
		@Override
		protected void onPreExecute() {
			imageViews.put(rowImage,imagePath);
			super.onPreExecute();
		}
		@Override
		protected Object doInBackground(Object... params) {
			 BitmapFactory.Options opts = new Options();
	         opts.inJustDecodeBounds = true;
	         BitmapFactory.decodeFile(imagePath, opts);
	         int imageHeight = opts.outHeight;
	         int imageWidth = opts.outWidth;

	         int scaleX = imageWidth / windowWidth;
	         int scaleY = imageHeight / windowHeight;
	         int scale = 1;
	         if (scaleX > scaleY && scaleY >= 1) {
	             scale = scaleX;
	         }
	         if (scaleX < scaleY && scaleX >= 1) {
	             scale = scaleY;
	         }
	         opts.inJustDecodeBounds = false;
	         opts.inSampleSize = scale;
	         bitmap = BitmapFactory.decodeFile(imagePath, opts);
	         System.out.println("加载玩图片  开始显示");
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			System.out.println("显示 Imager 图片！");
			String tag=imageViews.get(rowImage);
	         if(tag!=null&&tag==imagePath){
	        	 rowImage.setImageBitmap(bitmap);
	         }
			super.onPostExecute(result);
		}
	}
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

}  
