package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class FragmentSetting extends Fragment {
    public static final String TAG              = "FragmentSetting";
    private boolean mHiddenChanged              = false;
    private boolean mThumbnailChanged           = false;
    private boolean mSortChanged                = false;

    private boolean hidden_state                = false;
    private boolean thumbnail_state             = false;
    // Default text color black
    private int color_state                     = 0xFF000000;
    private int sort_state                      = 0;
    private Intent is                           = new Intent();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1      = "param1";
    private static final String ARG_PARAM2      = "param2";

    private static Context mContext             = null;
    private String mParam2                      = null;

    private OnFragmentInteractionListener mListener;

    public static FragmentSetting newInstance(Context context, String param2) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        mContext = context;
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

        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, null);

        //SharedPreferences sp = m
        // TODO Get info from SharedPreference
        hidden_state        = true;//i.getExtras().getBoolean("HIDDEN");
        thumbnail_state     = true;//i.getExtras().getBoolean("THUMBNAIL");
        sort_state          = 0;//i.getExtras().getInt("SORT");

        final CheckBox hidden_bx        = (CheckBox)v.findViewById(R.id.setting_hidden_box);
        final CheckBox thumbnail_bx     = (CheckBox)v.findViewById(R.id.setting_thumbnail_box);
        final ImageButton sort_bt       = (ImageButton)v.findViewById(R.id.settings_sort_button);

        hidden_bx.setChecked(hidden_state);
        thumbnail_bx.setChecked(thumbnail_state);

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
        return v;

        // TODO Store setting to SharedPreferences
        //return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
