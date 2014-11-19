package codingpark.net.cheesecloud.view;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import codingpark.net.cheesecloud.handle.OnTransFragmentInteractionListener;
import codingpark.net.cheesecloud.view.dummy.DummyContent;

/**
 * A fragment representing a list of download/downloading/downloaded record.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTransFragmentInteractionListener}
 * interface.
 */
public class FragmentDownloadList extends ListFragment {
    public static final String TAG          = FragmentDownloadList.class.getSimpleName();

    private static final String SECTION_NUMBER = "section_number";

    // The fragment index in TransferStateActivity
    private int section_number;

    private OnTransFragmentInteractionListener mListener = null;
    private Context mContext                        = null;

    public static FragmentDownloadList newInstance(int number) {
        FragmentDownloadList fragment = new FragmentDownloadList();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentDownloadList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            section_number = getArguments().getInt(SECTION_NUMBER);
        }

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTransFragmentInteractionListener) activity;
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }
}
