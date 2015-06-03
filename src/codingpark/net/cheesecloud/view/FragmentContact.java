package codingpark.net.cheesecloud.view;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.handle.OnFragmentInteractionListener;
import codingpark.net.cheesecloud.model.ContactListAdapter;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link codingpark.net.cheesecloud.handle.OnFragmentInteractionListener}
 * interface.
 */
public class FragmentContact extends ListFragment {

    private static Context mContext             = null;

    public static final String TAB_CONTACT_ITEM_OLD_FRIENDS                 = "old_friends";
    public static final String TAB_CONTACT_ITEM_NEW_FRIENDS                 = "new_friends";
    public static final String TAB_CONTACT_ITEM_GROUP_CHAT                  = "group_chat";
    public static final String TAB_CONTACT_ITEM_CIRCLE_OF_FRIENDS           = "circle_of_friends";

    private static final String[] values = new String[] {
            TAB_CONTACT_ITEM_OLD_FRIENDS,
            TAB_CONTACT_ITEM_NEW_FRIENDS,
            TAB_CONTACT_ITEM_GROUP_CHAT,
            TAB_CONTACT_ITEM_CIRCLE_OF_FRIENDS
    };

    private OnFragmentInteractionListener mListener;

    public static FragmentContact newInstance(Context context, String param2) {
        FragmentContact fragment = new FragmentContact();

        mContext = context;
        return fragment;
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentContact() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ContactListAdapter(mContext, R.layout.home_item_layout, values));
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
            mListener.onFragmentInteraction(values[position]);
        }
    }
}
