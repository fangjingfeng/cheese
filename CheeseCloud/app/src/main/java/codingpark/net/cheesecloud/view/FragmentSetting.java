package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.handle.OnSettingListener;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class FragmentSetting extends Fragment {
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
    private CheckBox hidden_bx                  = null;
    private CheckBox thumbnail_bx               = null;
    private ImageButton sort_bt                 = null;
    private Button logout_bt                    = null;

    private Context mContext                = null;
    private SharedPreferences mPrefs        = null;

    private OnSettingListener mListener     = null;

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
    public FragmentSetting() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init System object
        mPrefs = mContext.getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, null);

        //SharedPreferences sp = m
        // TODO Get info from SharedPreference
        hidden_state        = true;//i.getExtras().getBoolean("HIDDEN");
        thumbnail_state     = true;//i.getExtras().getBoolean("THUMBNAIL");
        sort_state          = 0;//i.getExtras().getInt("SORT");

        // Init UI elements
        hidden_bx = (CheckBox)v.findViewById(R.id.setting_hidden_box);
        thumbnail_bx = (CheckBox)v.findViewById(R.id.setting_thumbnail_box);
        sort_bt = (ImageButton)v.findViewById(R.id.settings_sort_button);
        logout_bt = (Button)v.findViewById(R.id.logout_bt);
        hidden_bx.setChecked(hidden_state);
        thumbnail_bx.setChecked(thumbnail_state);

        // Init UI elements handler
        initHandler();

        return v;
    }


    private void initHandler() {
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

        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "User logout manually!");
                // Set PREFS_LOGIN false
                mPrefs.edit().putBoolean(AppConfigs.PREFS_LOGIN, false).apply();
                // Call MainActivity logout,, start WelcomeActivity, then finish MainActivity
                mListener.logout();
            }
        });

        // TODO Store setting to SharedPreferences
        //return super.onCreateView(inflater, container, savedInstanceState);
    }


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
        mListener = null;
    }

}
