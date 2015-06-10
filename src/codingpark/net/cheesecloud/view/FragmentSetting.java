package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnSettingListener;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class FragmentSetting extends Fragment implements OnClickListener{
    public static final String TAG              = FragmentSetting.class.getSimpleName();
    private boolean mHiddenChanged              = false;
    private boolean mThumbnailChanged           = false;
    private boolean mSortChanged                = false;

    private boolean hidden_state                = false;
    private boolean thumbnail_state             = false;
    // Default text color black
    private int color_state                     = 0xFF000000;
    private int sort_state                      = 0;
    private Intent is                           = new Intent();

    // UI element
   /* private CheckBox hidden_bx                  = null;
    private CheckBox thumbnail_bx               = null;
    private ImageButton sort_bt                 = null;*/
    
    private TextView logenName;
    private ProgressBar disk_space ;
    
    
    private Context context= null;

    private Context mContext                = null;
    private SharedPreferences mPrefs        = null;

    private OnSettingListener mListener     = null;
	private ProgressBar user_space_ratio_progressbar;
	private RelativeLayout iv_update_and_down;
	private ToggleButton toggleButton;
	private Button iv_logo;

    public static FragmentSetting newInstance(String param2) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentSetting(Context context) {
    	this.context=context;
    }

    public FragmentSetting() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init System object
        mPrefs = mContext.getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, null);
        
        logenName = (TextView) v.findViewById(R.id.account_info_textview);
        disk_space =(ProgressBar) v.findViewById(R.id.disk_space_state_textview);
        iv_update_and_down = (RelativeLayout) v.findViewById(R.id.iv_update_and_down);
        iv_update_and_down.setOnClickListener(this);
        toggleButton = (ToggleButton)v.findViewById(R.id.toggleButton);
        iv_logo = (Button) v.findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(this);
        

        initHandler();

        return v;
    }


    private void initHandler() {
    	disk_space.setMax(100);
    	disk_space.setProgress(10);
    	
    	toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
            	if(isChecked){
            		Toast.makeText(mContext, "关闭", 0).show();
            	}else{
            		Toast.makeText(mContext, "打开", 0).show();
            	}
                
            }

        });
    	
    	
    	
    	/*
        hidden_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hidden_state = isChecked;
                is.putExtra("HIDDEN", hidden_state);
                mHiddenChanged = true;
            }
        });

        thumbnail_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                thumbnail_state = isChecked;

                is.putExtra("THUMBNAIL", thumbnail_state);
                mThumbnailChanged = true;
            }
        });

        sort_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                CharSequence[] options = {getResources().getString(R.string.None),
                getResources().getString(R.string.Alphabetical),
                getResources().getString(R.string.Type)};

                builder.setTitle(getResources().getString(R.string.Sort_by));
                builder.setIcon(R.drawable.filter);
                builder.setSingleChoiceItems(options, sort_state, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch(index) {
                            case 0:
                                sort_state = 0;
                                mSortChanged = true;
                                is.putExtra("SORT", sort_state);
                                break;

                            case 1:
                                sort_state = 1;
                                mSortChanged = true;
                                is.putExtra("SORT", sort_state);
                                break;

                            case 2:
                                sort_state = 2;
                                mSortChanged = true;
                                is.putExtra("SORT", sort_state);
                                break;
                        }
                    }
                });

                builder.create().show();
            }
        });

        
    */}


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSettingListener) activity;
            mContext = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("FragmentSetting xiaohuile ");
        mListener = null;
        
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_photho_updat:
			//同步照片
			
			break;

		case R.id.iv_update_and_down:
			//查看当前进度
			Intent intent =new Intent(mContext, TransferStateActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.iv_logo:
			Toast.makeText(mContext, "退出等人",0).show();
            mPrefs.edit().putBoolean(AppConfigs.PREFS_LOGIN, false).apply();
            mListener.logout();
           break;
		}
	}

}
