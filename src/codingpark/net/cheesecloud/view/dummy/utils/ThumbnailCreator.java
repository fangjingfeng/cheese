package codingpark.net.cheesecloud.view.dummy.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;
import android.graphics.Matrix;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.model.MessageCache;

/**
 * The helper to create thumbnail from local image file
 */
public class ThumbnailCreator {
    private int width;
    private int height;
    private Context context;
    private static final String TAG = ThumbnailCreator.class.getSimpleName();
    public static final int IMAGER_thumbnail=100;

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

    /**
     * Create default(Not generate thumbnails for image and video) thumbnails for
     * cloud files(Just file, not folder)
     * @param fileName the file name for cloud file
     * @return The default image resource id
     */
    public static int getDefThumbnailsByName(String fileName) {
        int res_id = -1;
        if (fileName.lastIndexOf(".") >= 0) {
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (TypeFilter.getInstance().isPdfFile(ext)) {

                return R.drawable.ft_pdf;

            } else if (TypeFilter.getInstance().isMusicFile(ext)) {

                return R.drawable.ft_mp3;

            } else if (TypeFilter.getInstance().isPictureFile(ext)) {
                return ThumbnailCreator.IMAGER_thumbnail;

            } else if (TypeFilter.getInstance().isZipFile(ext) ||
                    TypeFilter.getInstance().isGZipFile(ext)) {

                return R.drawable.ft_zip;

            } else if(TypeFilter.getInstance().isMovieFile(ext)) {

                return R.drawable.ft_avi;

            } else if(TypeFilter.getInstance().isWordFile(ext)) {

                return R.drawable.word;

            } else if(TypeFilter.getInstance().isExcelFile(ext)) {

                return R.drawable.excel;

            } else if(TypeFilter.getInstance().isPptFile(ext)) {

                return R.drawable.ft_ppt;

            } else if(TypeFilter.getInstance().isHtml32File(ext)) {

                return R.drawable.ft_html;

            } else if(TypeFilter.getInstance().isXml32File(ext)) {

                return R.drawable.ft_xml;

            } else if(TypeFilter.getInstance().isConfig32File(ext)) {
                return R.drawable.config32;

            } else if(TypeFilter.getInstance().isApkFile(ext)) {
                return R.drawable.ft_apk;

            } else if(TypeFilter.getInstance().isJarFile(ext)) {
                return R.drawable.jar32;

            } else {
                return R.drawable.ft_txt;
            }
        } else {
            return R.drawable.text;
        }

    }
    

    
}