package codingpark.net.cheesecloud.handle;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.Music;

public class MediaEngine {

    private static MediaEngine sInstance = new MediaEngine();
    private ArrayList<Music> mMusicList = new ArrayList<Music>();

    public int mCurrentState = MyConstances.OPTION_STOP;// 当前播放状态
    public int mCurrentPos = 0;// 当前第几首音乐
    public int mCurrentMode = MyConstances.MODE_CIRCLE;// 当前播放模式

    private MediaEngine() {
    }

    public static MediaEngine getInstance() {
        return sInstance;
    }

    public ArrayList<Music> getMusicList() {
        return mMusicList;
    }

    public Music getCurrentMusic() {
        if (!mMusicList.isEmpty()) {
            return mMusicList.get(mCurrentPos);
        } else {
            return null;
        }
    }

    // 高版本sdk 需要权限才能调用该方法, 否则崩溃
    // <uses-permission
    // android:name="android.permission.READ_EXTERNAL_STORAGE"/>
   /* public void loadMusicList(Context ctx) {
        mMusicList.clear();
        ContentResolver contentResolver = ctx.getContentResolver();
        // 歌曲名字, 时长, 路径, 作者, id
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                }, "='张三'",
                null, null);

        while (cursor.moveToNext()) {
            Music music = new Music();
            music.id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            music.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            music.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            music.duration = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            music.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

            if (music.duration != null) {
                int duration = Integer.parseInt(music.duration);
                if (duration < 10 * 1000) {
                    continue;
                }
            }

            if (music.path != null) {
                File file = new File(music.path);
                if (!file.exists()) {
                    continue;
                }
            }
            mMusicList.add(music);
        }
    }*/

    // 00:00
    public static String timeFormat(int time) {
        int t = time / 1000;
        int min = t / 60;
        int sec = t % 60;
        String format = "%02d:%02d";
        return String.format(format, min, sec);
    }
    
}