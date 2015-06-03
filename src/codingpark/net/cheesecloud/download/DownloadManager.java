package codingpark.net.cheesecloud.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Intent;
import android.net.Uri;

import com.lidroid.xutils.util.LogUtils;

/**
 * Created by lipan on 2014/12/13.
 */
public class DownloadManager {
	public static final int STATE_NORMAL = 1;
	public static final int STATE_WAITING = 2;
	public static final int STATE_DOWANLODING = 3;
	public static final int STATE_PAUSE = 4;
	public static final int STATE_ERROR = 5;
	public static final int STATE_COMPULE = 6;

	private DownloadManager(){}
	private static DownloadManager mInstance;

	private Map<Long,DownloadInfo> downloadInfoMap = new ConcurrentHashMap<Long, DownloadInfo>();
	private Map<Long,DownloadTask> downloadTaskMap = new ConcurrentHashMap<Long, DownloadTask>();

	private List<DownloadObser> obsers = new ArrayList<DownloadObser>();

	public static DownloadManager getInstance(){
		synchronized (DownloadManager.class){
			if (mInstance == null){
				mInstance = new DownloadManager();
			}
		}
		return mInstance;
	}

	public DownloadInfo getDownloadInfo(AppInfo info){
		return downloadInfoMap.get(info.getId());
	}
	public void download(AppInfo info){
		DownloadInfo downloadInfo = downloadInfoMap.get(info.getId());
		if (downloadInfo == null){
			downloadInfo = DownloadInfo.clone(info);
			downloadInfoMap.put(info.getId(),downloadInfo);
		}
		if (downloadInfo.getDownloadState() == STATE_NORMAL || downloadInfo.getDownloadState() == STATE_ERROR || downloadInfo.getDownloadState() == STATE_PAUSE){
			downloadInfo.setDownloadState(STATE_WAITING);
			// 通知UI
			notifyStateChanged(downloadInfo);
			DownloadTask task = new DownloadTask(downloadInfo);
			downloadTaskMap.put(info.getId(),task);
			ThreadPoolManager.getDownloadPool().execute(task);
		}
	}

	public void pause(AppInfo info){
		DownloadInfo downloadInfo = downloadInfoMap.get(info.getId());
		if (downloadInfo != null) {
			downloadInfo.setDownloadState(STATE_PAUSE);
			// 通知UI
			notifyStateChanged(downloadInfo);
			cancelTask(info);
		}
	}

	public void cancel(AppInfo info){
		DownloadInfo downloadInfo = downloadInfoMap.get(info.getId());
		if (downloadInfo != null) {
			downloadInfo.setDownloadState(STATE_NORMAL);
			// 通知UI
			notifyStateChanged(downloadInfo);
			cancelTask(info);
			File file = new File(downloadInfo.getPath());
			file.delete();
		}
	}

	public void install(AppInfo info){
		DownloadInfo downloadInfo = downloadInfoMap.get(info.getId());//找出下载信息
		if (info != null) {//发送安装的意图
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			installIntent.setDataAndType(Uri.parse("file://" + downloadInfo.getPath()), "application/vnd.android.package-archive");
			UIUtils.getContext().startActivity(installIntent);
		}
	}


	private void cancelTask (AppInfo info){
		DownloadTask task = downloadTaskMap.get(info.getId());
		if (task != null) {
			ThreadPoolManager.getDownloadPool().cancel(task);
		}
		ThreadPoolManager.getDownloadPool().cancel(task);
	}


	public void registDownloadObser(DownloadObser obser){
		synchronized (obsers) {
			if (!obsers.contains(obser)) {
				obsers.add(obser);
			}
		}
	}

	public void unregistDownloadObser(DownloadObser obser){
		synchronized (obsers) {
			obsers.remove(obser);
		}
	}

	private void notifyStateChanged(DownloadInfo info){
		synchronized (obsers) {
			for (DownloadObser obser : obsers) {
				obser.onDownloadStateChanged(info);
			}
		}
	}

	private void notifyProgressChanged(DownloadInfo info){
		synchronized (obsers) {
			for (DownloadObser obser : obsers) {
				obser.onDownloadProgressChanged(info);
			}
		}
	}

	class DownloadTask implements Runnable {
		private DownloadInfo mInfo;
		public DownloadTask(DownloadInfo info){
			mInfo = info;
		}
		@Override
		public void run() {/*
			mInfo.setDownloadState(STATE_DOWANLODING);
			// 通知UI去刷新
			notifyStateChanged(mInfo);
			File file = new File(mInfo.getPath());
			HttpHelper.HttpResult httpResult = null;
			if (mInfo.getCurrentSize() == 0  || !file.exists() || file.length() != mInfo.getCurrentSize()){
				// 从头开始下
				LogUtils.e("download```````````");
				httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + mInfo.getUrl());
				file.delete();
				mInfo.setCurrentSize(0);

			} else {
				// 断点续传
				httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + mInfo.getUrl() + "&range=" + mInfo.getCurrentSize());
			}

			if (httpResult == null || httpResult.getInputStream() == null){
				mInfo.setDownloadState(STATE_ERROR);
				// 通知UI去刷新
				notifyStateChanged(mInfo);
			} else {
				try {
					InputStream in = httpResult.getInputStream();
					FileOutputStream stream = new FileOutputStream(file,true);
					byte[] buff = new byte[1024];
					int len = -1;
					while ((len = in.read(buff)) != -1 && mInfo.getDownloadState() == STATE_DOWANLODING){
						stream.write(buff,0,len);
						stream.flush();
						mInfo.setCurrentSize(mInfo.getCurrentSize() + len);
						LogUtils.e("download2```````````");
						notifyProgressChanged(mInfo);
						LogUtils.e("download2```````````");
					}
				} catch (Exception e) {
					mInfo.setDownloadState(STATE_ERROR);
					// 通知UI去刷新
				}
				if (mInfo.getDownloadState() == STATE_DOWANLODING && mInfo.getCurrentSize() == mInfo.getAppSize() && file.exists() && file.length() ==  mInfo.getCurrentSize()){
					mInfo.setDownloadState(STATE_COMPULE);
				} else if(mInfo.getDownloadState() == STATE_ERROR){
					file.delete();
					mInfo.setCurrentSize(0);
				}
				notifyStateChanged(mInfo);
			}
		*/}
	}


	public interface DownloadObser{
		public void onDownloadStateChanged(DownloadInfo info);
		public void onDownloadProgressChanged(DownloadInfo info);
	}
	//httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + info.getUrl());
	//httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + info.getUrl() + "&range=" + info.getCurrentSize());
}
