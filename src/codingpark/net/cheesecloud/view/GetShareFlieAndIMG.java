package codingpark.net.cheesecloud.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.entity.CloudFile;
import codingpark.net.cheesecloud.entity.UploadFile;
import codingpark.net.cheesecloud.enumr.CheckedFileInfoResultType;
import codingpark.net.cheesecloud.enumr.UploadFileState;
import codingpark.net.cheesecloud.enumr.WsResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.handle.UploadService;
import codingpark.net.cheesecloud.model.UploadFileDataSource;
import codingpark.net.cheesecloud.utils.BitmapUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GetShareFlieAndIMG extends Activity implements
		View.OnClickListener {

	private ImageView image;
	private TextView down_file_path;
	private Bitmap bitmap;
	private ArrayList<String> pathList=null;
	private  UploadFileDataSource mDataSource;
	private String remote_parent_id ="b8361427-1919-47d5-871e-129fe7c5a5ac";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_imager);
		init();
		Intent intent = getIntent();
		// 获得Intent的Action
		String action = intent.getAction();
		// 获得Intent的MIME type
		String type = intent.getType();
		pathList=new ArrayList<String>();
		
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if (type.startsWith("text/")) {
				// 处理获取到的文本，这里我们用TextView显示
				handleSendText(intent);
			}
			else if (type.startsWith("image/")) {
				// 处理获取到图片，我们用ImageView显示
				handleSendImage(intent);
			}
		} else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
			if (type.startsWith("image/")) {
				// 处理多张图片，我们用一个GridView来显示
				handleSendMultipleImages(intent);
			}
		}
		 mDataSource = new UploadFileDataSource(GetShareFlieAndIMG.this);
		 mDataSource.open();
	}

	public void init() {
		TextView ib_playback = (TextView) findViewById(R.id.ib_playback);
		ib_playback.setOnClickListener(this);
		image = (ImageView) findViewById(R.id.image);
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText("上传到校园云盘");
		down_file_path = (TextView) findViewById(R.id.down_file_path);
		down_file_path.setOnClickListener(this);
		
		Button upload =(Button)findViewById(R.id.upload);
		upload.setOnClickListener(this);
	}

	/**
	 * 用TextView显示文本 可以打开一般的文本文件
	 * @param intent
	 */
	private void handleSendText(Intent intent) {
		TextView textView = new TextView(this);

		// 一般的文本处理，我们直接显示字符串
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			textView.setText(sharedText);
		}

		// 文本文件处理，从Uri中获取输入流，然后将输入流转换成字符串
		Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (textUri != null) {
			try {
				InputStream inputStream = this.getContentResolver()
						.openInputStream(textUri);
				textView.setText(inputStream2Byte(inputStream));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 设置给Activity
		setContentView(textView);
	}

	/**
	 * 将输入流转换成字符串
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private String inputStream2Byte(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		// 指定编码格式为UIT-8
		return new String(bos.toByteArray(), "UTF-8");
	}

	/**
	 * 用ImageView显示单张图片
	 * @param intent
	 */
	private void handleSendImage(Intent intent) {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		int windowHeight = wm.getDefaultDisplay().getHeight();
		int windowWidth = wm.getDefaultDisplay().getWidth();
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			//System.out.println("uri2Path(imageUri)---->"+uri2Path(imageUri));
			BitmapUtil.downloadBitmap(uri2Path(imageUri),image, windowHeight, windowWidth);
			pathList.add(uri2Path(imageUri));
		}
	}
	 public String uri2Path(Uri uri) {
	       int actual_image_column_index;
	       String img_path;
	       String[] proj = { MediaStore.Images.Media.DATA };
	       Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
	       actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	       cursor.moveToFirst();
	       img_path = cursor.getString(actual_image_column_index);
	       return img_path;

	    }

	/**
	 * 用GridView显示多张图片
	 * @param intent
	 */
	private void handleSendMultipleImages(Intent intent) {
		final ArrayList<Uri> imageUris = intent
				.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		for(Uri uri:imageUris){
			pathList.add(uri2Path(uri));
		}
		if (imageUris != null) {
			GridView gridView = new GridView(this);
			// 设置item的宽度
			gridView.setColumnWidth(130);
			// 设置列为自动适应
			gridView.setNumColumns(GridView.AUTO_FIT);
			gridView.setAdapter(new GridAdapter(this, imageUris));
			setContentView(gridView);

			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						final int position, long id) {
					// 点击GridView的item 可以分享图片给其他应用
					// 这里可以参考http://blog.csdn.net/xiaanming/article/details/9395991
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_STREAM,
							imageUris.get(position));
					intent.setType("image/*");
					startActivity(Intent.createChooser(intent, "共享图片"));
				}
			});
		}
	}

	/**
	 * 重写BaseAdapter
	 * @author xiaanming
	 */
	public class GridAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<Uri> list;

		public GridAdapter(Context mContext, ArrayList<Uri> list) {
			this.list = list;
			this.mContext = mContext;
		}
		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageURI(list.get(position));
			return imageView;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_playback:
			finish();
			break;
		case R.id.down_file_path:
			// 更换上传地址
			Intent r_intent = new Intent(GetShareFlieAndIMG.this,SelectPathActivity.class);
			GetShareFlieAndIMG.this.startActivityForResult(r_intent, 0, null);
			break;
		case R.id.upload:
			//开始上传文件图片
			if(pathList.size()>0){
				System.out.println("保存图片到文件中！");
				new ScanUploadFilesTask(pathList).execute();
				System.out.println("开启下载服务！");
				UploadService.startActionUploadAll(GetShareFlieAndIMG.this);
			}else{
				Toast.makeText(this, "没有图片可以下载", 0).show();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		mDataSource.close();
		super.onDestroy();
	}
	
	 /**
     * Scan the selected files and folders, and then insert the record to
     * 扫描选定的文件和文件夹，然后插入记录
     * upload_files table recursively.
     */
    private class ScanUploadFilesTask extends AsyncTask<Void, Integer, Integer> {

        public static final int SCAN_SUCCESS    = 0;
        public static final int SCAN_FAILED     = 1;

        private ArrayList<String> mFileList         = null;
        private UploadFileDataSource mDataSource    = null;

        public ScanUploadFilesTask(ArrayList<String> fileList) {
            mFileList = fileList;
            mDataSource = new UploadFileDataSource(GetShareFlieAndIMG.this);
        }

        @Override
        protected void onPreExecute() {
            mDataSource.open();
        }

        protected Integer doInBackground(Void... params) {
        	int i=0;
        	for (String path: mFileList) {
                scan(new File(path), -1, remote_parent_id);
                i++;
                publishProgress(i); 
            }
            return SCAN_SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Integer result) {
             UploadService.startActionUploadAll(GetShareFlieAndIMG.this);
            switch (result) {
                case SCAN_SUCCESS:
                    break;
                case SCAN_FAILED:
                    break;
                default:
                    break;
            }
            mDataSource.close();
        }

        private void scan(File file, long l_parent_id, String r_parent_id) {
            UploadFile uFile = UploadFileDataSource.createUploadFile(file, l_parent_id, r_parent_id);
            int result = WsResultType.Success; 
            long l_id = -1;    
            if (file.isDirectory()) {
                result = ClientWS.getInstance(GetShareFlieAndIMG.this).createFolderUpload_wrapper(uFile);
                if (result != WsResultType.Success)
                    return;     
                else {
                    uFile.setState(UploadFileState.UPLOADED);
                    l_id = mDataSource.addUploadFile(uFile);
                    File[] fileArray = file.listFiles();
                    File subFile = null;
                    for (int i = 0; i < fileArray.length; i++) {
                        subFile = fileArray[i];
                        scan(subFile, l_id, uFile.getRemote_id());
                    }
                }
            }
            else if (file.isFile()){
                if (file.exists() && file.length() > 0) {
                    result = ClientWS.getInstance(GetShareFlieAndIMG.this).checkedFileInfo_wrapper(uFile);
                    if (result == CheckedFileInfoResultType.RESULT_CHECK_SUCCESS ||
                            result == CheckedFileInfoResultType.RESULT_QUICK_UPLOAD) {
                        mDataSource.addUploadFile(uFile);
                    }
                }
            }
            return;
        }
    }
	
}