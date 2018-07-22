package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.User;


/**
 * Displays a list of friends.
 *
 * Any activity containing this fragment must implement FriendListInteractionListener.
 *
 * @author Ben Cole
 */
public class FriendsListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private FriendListInteractionListener mListener;

    public interface FriendListInteractionListener {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        return view;
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

    /**
     * Serves rows to the RecyclerView
     */
    private static class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView usernameText;

            public ViewHolder(TextView textView) {
                super(textView);
                usernameText = textView;
            }

        }

        private ArrayList<User> mDataset;

        public FriendListAdapter(ArrayList<User> dataset) {
            mDataset = dataset;
        }

        @Override
        public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView usernameText = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listelement_friend, parent, false);
            return new ViewHolder(usernameText);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.usernameText.setText(mDataset.get(position).getUsername());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
