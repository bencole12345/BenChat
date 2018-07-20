package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pw.bencole.benchat.R;


/**
 * Displays a list of friends.
 *
 * Any activity containing this fragment must implement FriendListInteractionListener.
 *
 * @author Ben Cole
 */
public class FriendsListFragment extends Fragment {

    private FriendListInteractionListener mListener;

    public interface FriendListInteractionListener {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FriendListInteractionListener) {
            mListener = (FriendListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FriendListInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
