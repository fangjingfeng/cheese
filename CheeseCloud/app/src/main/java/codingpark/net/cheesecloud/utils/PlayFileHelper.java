package codingpark.net.cheesecloud.utils;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import codingpark.net.cheesecloud.R;

/**
 * Created by ethanshan on 14-11-27.
 */
public class PlayFileHelper {
    public static final String TAG  = PlayFileHelper.class.getSimpleName();
    private Context mContext        = null;


    public PlayFileHelper(Context context) {
        mContext = context;
    }

    public void playFile(String path) {
        /*
        if (mCallbacks.returnFile(new File(path))) {
            return;
        }
        */
        String ext = Misc.getExtesion(path);

        if (TypeFilter.getInstance().isMovieFile(ext)) {
            playVideo(path);
        } else if (TypeFilter.getInstance().isMusicFile(ext)) {
            playMusic(path);
        } else if (TypeFilter.getInstance().isPictureFile(ext)) {
            playPicture(path);
        } else if (TypeFilter.getInstance().isApkFile(ext)) {
            playApk(path);
        } else if (TypeFilter.getInstance().isTxtFile(ext)) {
            playTxt(path);
        } else if (TypeFilter.getInstance().isHtml32File(ext)) {
            playHtml(path);
        } else if (TypeFilter.getInstance().isPdfFile(ext)) {
            playPdf(path);
        } else {
            selectFileType_dialog(new File(path));
        }
    }

    private void playMusic(String path) {
        File file = new File(path);
        Intent picIntent = new Intent();
        picIntent.setAction(android.content.Intent.ACTION_VIEW);
        picIntent.setDataAndType(Uri.fromFile(file), "audio/*");
        try {
            mContext.startActivity(picIntent);
        } catch (ActivityNotFoundException e) {
            //DisplayToast(mContext.getResources().getString(
                    //R.string.not_app_to_play_the_music));
            DisplayToast("无音乐播放器");
        }
    }

    private void playVideo(String path) {

        File file = new File(path);
        Intent movieIntent = new Intent();
        //movieIntent.putExtra(MediaStore.PLAYLIST_TYPE,
                //MediaStore.PLAYLIST_TYPE_CUR_FOLDER);
        movieIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
        //movieIntent.putExtra(MediaStore.EXTRA_BD_FOLDER_PLAY_MODE, false);
        movieIntent.setAction(android.content.Intent.ACTION_VIEW);
        movieIntent.setDataAndType(Uri.fromFile(file), "video/*");

        Log.d(TAG, "Start");
        try {
            mContext.startActivity(movieIntent);
        } catch (ActivityNotFoundException e) {
            /*
            DisplayToast(mContext.getResources().getString(
                    R.string.not_app_to_play_the_video));
                    */
            DisplayToast("无视频播放器");
        }
    }

    private void playPicture(String path) {
        File file = new File(path);
        Intent picIntent = new Intent();
        picIntent.setAction(android.content.Intent.ACTION_VIEW);
        picIntent.setDataAndType(Uri.fromFile(file), "image/*");
        try {
            mContext.startActivity(picIntent);
        } catch (ActivityNotFoundException e) {
            /*
            DisplayToast(mContext.getResources().getString(
                    R.string.not_app_to_oepn_the_pic));
                    */
            DisplayToast("无照片浏览器");
        }
    }

    private void playPdf(String path) {
        File file = new File(path);
        Intent pdfIntent = new Intent();
        pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        try {
            mContext.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            selectFileType_dialog(new File(path));
        }

    }

    private void playApk(String path) {
        File file = new File(path);
        Intent apkIntent = new Intent();
        apkIntent.setAction(android.content.Intent.ACTION_VIEW);
        apkIntent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        try {
            mContext.startActivity(apkIntent);
        } catch (ActivityNotFoundException e) {
            /*
            DisplayToast(mContext.getResources().getString(
                    R.string.not_app_to_open_the_file));
                    */
            DisplayToast("无应用安装器");
        }
    }

    private void playHtml(String path) {
        File file = new File(path);
        Intent htmlIntent = new Intent();
        htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
        htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");
        try {
            mContext.startActivity(htmlIntent);
        } catch (ActivityNotFoundException e) {
            selectFileType_dialog(new File(path));
        }
    }

    private void playTxt(String path) {
        File file = new File(path);
        Intent txtIntent = new Intent();
        txtIntent.setAction(android.content.Intent.ACTION_VIEW);
        txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");
        try {
            mContext.startActivity(txtIntent);
        } catch (ActivityNotFoundException e) {
            /*
            DisplayToast(mContext.getResources().getString(
                    R.string.not_app_to_open_the_file));
                    */
            DisplayToast("无文本编辑器");
        }
    }


    private String openType = null;

    private void selectFileType_dialog(final File openFile) {
        String mFile = mContext.getResources().getString(R.string.open_file);
        String mText = mContext.getResources().getString(R.string.text);
        String mAudio = mContext.getResources().getString(R.string.music);
        String mVideo = mContext.getResources().getString(R.string.video);
        String mImage = mContext.getResources().getString(R.string.picture);
        CharSequence[] FileType = { mText, mAudio, mVideo, mImage };
        AlertDialog.Builder builder;
        AlertDialog dialog;
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mFile);
        builder.setIcon(R.drawable.help);
        builder.setItems(FileType, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent();
                switch (which) {
                    case 0:
                        openType = "text/*";
                        break;
                    case 1:
                        openType = "audio/*";
                        break;
                    case 2:
                        openType = "video/*";
                        break;
                    case 3:
                        openType = "image/*";
                        break;
                }
                mIntent.setAction(android.content.Intent.ACTION_VIEW);
                mIntent.setDataAndType(Uri.fromFile(openFile), openType);
                try {
                    mContext.startActivity(mIntent);
                } catch (ActivityNotFoundException e) {
                    DisplayToast("无法打开文件");
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void DisplayToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}
