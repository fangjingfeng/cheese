package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import codingpark.net.cheesecloud.Main;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.model.HomeListAdapter;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class FragmentHome extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM2 = "param2";

    private static Context mContext                 = null;
    private String mParam2;

    public static final String TAB_HOME_ITEM_NEWS               = "news";
    public static final String TAB_HOME_ITEM_CLOUD_DISK         = "cloud_disk";
    public static final String TAB_HOME_ITEM_RESOURCE_LIBRARY   = "resource_library";
    public static final String TAB_HOME_ITEM_SMALL_CLASS        = "small_class";
    public static final String TAB_HOME_ITEM_TEMP_SCREEN        = "temp_screen";

    private static final String[] values = new String[] {
            TAB_HOME_ITEM_NEWS,
            TAB_HOME_ITEM_CLOUD_DISK,
            TAB_HOME_ITEM_RESOURCE_LIBRARY,
            TAB_HOME_ITEM_SMALL_CLASS,
            TAB_HOME_ITEM_TEMP_SCREEN
    };

    private OnFragmentInteractionListener mListener;

    public static FragmentHome newInstance(Context context, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        mContext = context;
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentHome() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setListAdapter(new HomeListAdapter(mContext, values));
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            String action_item = values[position];
            mListener.onFragmentInteraction(action_item);
            // TODO Current just resolve tab_home_item_resource_library
            if (action_item.equals(TAB_HOME_ITEM_RESOURCE_LIBRARY)) {
                Intent i = new Intent(mContext, Main.class);
                mContext.startActivity(i);

            }
        }
    }

}
