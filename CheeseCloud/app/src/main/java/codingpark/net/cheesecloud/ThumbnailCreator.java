package codingpark.net.cheesecloud;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;
import android.graphics.Matrix;

public class ThumbnailCreator {
    private int width;
    private int height;
    private Context context;
    private static final String TAG = ThumbnailCreator.class.getSimpleName();

    public ThumbnailCreator(Context context,int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
    }

    public Bitmap hasBitmapCached(String imageSrc) {
        File f = new File(imageSrc);
        Bitmap bmp = MessageCache.getInstance().loadThumbnailMessage(imageSrc);
        if(bmp != null){
            if(MessageCache.getInstance().loadModifiedTime(imageSrc) != f.lastModified()){
                Log.d(TAG," the file had been change since last time,I should request thumbnail again ");
                return null;
            }
        }
        return bmp;
    }


    public void clearBitmapCache() {
        MessageCache.getInstance().clearThumbnailCache();
    }

    public void setBitmapToImageView(final String imageSrc,
                                     final ImageView icon) {

        Thread thread = new Thread() {
            public void run() {
                synchronized (this) {
                    final Bitmap bmp = createThumbnail(imageSrc);
                    if(bmp != null){
                        MessageCache.getInstance().saveThumbnailMessage(imageSrc, bmp);
                        File f = new File(imageSrc);
                        MessageCache.getInstance().saveModifiedTime(imageSrc, f.lastModified());
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                icon.setImageBitmap(bmp);
                            }
                        });
                    }
                }
            }
        };

        thread.start();
    }

    public Bitmap createThumbnail(String imageSrc)
    {
        boolean isJPG = false;
        Bitmap thumbnail = null;
        try
        {
            String ext = imageSrc.substring(imageSrc.lastIndexOf(".") + 1);
            if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))
            {
                isJPG = true;
            }
        }catch(IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return null;
        }

        if(isJPG)
        {
            try {
                ExifInterface mExif = null;
                mExif = new ExifInterface(imageSrc);
                if(mExif != null)
                {
                    byte[] thumbData = mExif.getThumbnail();
                    if(thumbData == null)
                    {
                        thumbnail = createThumbnailByOptions(imageSrc);
                    }
                    else
                    {
                        int orient = mExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        thumbnail = BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length);
                        Matrix m = new Matrix();
                        float centerX = (float)thumbnail.getWidth()/2;
                        float centerY = (float)thumbnail.getHeight()/2;
                        switch(orient){
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                m.setRotate(90.0f, centerX, centerY);
                                thumbnail = Bitmap.createBitmap(thumbnail, 0, 0,
                                        thumbnail.getWidth(), thumbnail.getHeight(), m, false);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                m.setRotate(180.0f, centerX, centerY);
                                thumbnail = Bitmap.createBitmap(thumbnail, 0, 0,
                                        thumbnail.getWidth(), thumbnail.getHeight(), m, false);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                m.setRotate(270.0f, centerX, centerY);
                                thumbnail = Bitmap.createBitmap(thumbnail, 0, 0,
                                        thumbnail.getWidth(), thumbnail.getHeight(), m, false);
                                break;
                            default:

                        }

                        thumbnail = Bitmap.createScaledBitmap(thumbnail, width, height, false);
                    }
                }
                else
                {
                    thumbnail = createThumbnailByOptions(imageSrc);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            thumbnail = createThumbnailByOptions(imageSrc);
        }
        return thumbnail;
    }

    private Bitmap createThumbnailByOptions(String imageSrc)
    {
        try{
            Bitmap thumb = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            thumb = BitmapFactory.decodeFile(imageSrc, options);
            int be = (int) (Math.min(options.outWidth / width, options.outHeight / height));
            if(be <= 0)
                be = 1;
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            thumb = BitmapFactory.decodeFile(imageSrc, options);
            if(thumb == null)
            {
                return null;
            }
            thumb = Bitmap.createScaledBitmap(thumb, width, height, false);
            Log.d(TAG,"image:" + imageSrc + "  orignal size:" + options.outWidth + "*" + options.outHeight);
            Log.d(TAG,"image:" + imageSrc + "  thumb size:" + String.valueOf(width) + "*" + String.valueOf(height));
            return thumb;
        }catch(Exception e){
            return null;
        }
    }
}