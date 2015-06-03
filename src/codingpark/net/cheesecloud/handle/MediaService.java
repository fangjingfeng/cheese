package codingpark.net.cheesecloud.handle;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import codingpark.net.cheesecloud.constances.MyConstances;
import codingpark.net.cheesecloud.view.MainActivity;
import codingpark.net.cheesecloud.view.OpenFiles;
import codingpark.net.cheesecloud.view.OpenMusic;

public class MediaService extends Service implements OnCompletionListener, OnErrorListener {

    private static final String TAG = MediaService.class.getSimpleName();

    private MediaPlayer player;

   private MediaEngine mEngine;

    private Timer timer;
    @Override
    public void onCreate() {
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        mEngine = MediaEngine.getInstance();
        System.out.println("初始化音乐播放器~~~");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        }
        String path = intent.getStringExtra("path");
        int option = intent.getIntExtra("option", -1);
        int seek = intent.getIntExtra("seek", -1);

        switch (option) {
            case MyConstances.OPTION_PLAY:
                play(path);
                mEngine.mCurrentState = MyConstances.OPTION_PLAY;
                break;
            case MyConstances.OPTION_PAUSE:
                pause();
                mEngine.mCurrentState = MyConstances.OPTION_PAUSE;
                break;
            case MyConstances.OPTION_CONTINUE:
                continuePlay();
                mEngine.mCurrentState = MyConstances.OPTION_CONTINUE;
                break;
            case MyConstances.MUSIC_SEEK:
                seekTo(seek);
                break;

            default:
                break;
        }
        return Service.START_STICKY;
    }

    private void seekTo(int seek) {
        if (player != null && seek > 0 && seek < player.getDuration()) {
            player.seekTo(seek);
        }
    }

    private void continuePlay() {
        if (player != null && !player.isPlaying()) {
            player.start();
           // startTimer();
        }
    }

    private void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void play(String path) {
        try {
            player.reset();
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startTimer();
        //loadLyric();
    }

   /* private void loadLyric() {
        String path = mEngine.getCurrentMusic().path;
        String sub = path.substring(0, path.lastIndexOf("."));

        File f = new File(sub + ".lrc");

        if (!f.exists()) {
            f = new File(sub + ".txt");
        }

        MainActivity.sLyricUtil.readLRC(f);
    }*/

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (player != null) {
                    int duration = player.getDuration();
                    int currentPos = player.getCurrentPosition();

                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPos", currentPos);

                    Message msg = Message.obtain();
                    msg.what = MyConstances.MUSIC_PROGRESS;
                    msg.setData(bundle);

                    OpenMusic.handlers.sendMessage(msg);
                }
            }
        }, 0, 100);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   @Override
   public void onCompletion(MediaPlayer mp) {
        OpenMusic.handlers.sendEmptyMessage(MyConstances.MUSIC_COMPLETE);
        switch (mEngine.mCurrentMode) {
            case MyConstances.MODE_CIRCLE:
                if (mEngine.mCurrentPos < mEngine.getMusicList().size() - 1) {
                    mEngine.mCurrentPos++;
                } else {
                    mEngine.mCurrentPos = 0;
                }
                play(mEngine.getCurrentMusic().path);
                break;
            case MyConstances.MODE_LOOP:
                play(mEngine.getCurrentMusic().path);
                break;
            case MyConstances.MODE_ORDER:
                if (mEngine.mCurrentPos < mEngine.getMusicList().size() - 1) {
                    mEngine.mCurrentPos++;
                    play(mEngine.getCurrentMusic().path);
                } else {
                	 OpenMusic.handlers.sendEmptyMessage(MyConstances.MUSIC_STOP);
                    stopSelf();
                }
                break;
            case MyConstances.MODE_RANDOM:
                Random random = new Random();
                mEngine.mCurrentPos = random.nextInt(mEngine.getMusicList().size());
                play(mEngine.getCurrentMusic().path);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "亲, 您的文件有问题!",Toast.LENGTH_LONG).show();
        // 同时会回调OnComplete方法
        return false;
    }


}