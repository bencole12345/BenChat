package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.NetworkHelper;


/**
 * Displays a list of friends.
 *
 * Any activity containing this fragment must implement FriendListInteractionListener.
 *
 * @author Ben Cole
 */
public class FriendsListFragment extends Fragment {

    /**
     * Reference to the RecyclerView used to display the list of friends.
     */
    private RecyclerView mRecyclerView;

    /**
     * References to UI elements
     */
    private FriendListAdapter mAdapter;

    /**
     * List of currently known friends
     */
    private ArrayList<User> mFriends;

    /**
     * Interface to ensure that the containing activity has a getUser() method
     */
    private FriendListInteractionListener mListener;
    public interface FriendListInteractionListener {
        LoggedInUser getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mFriends = new ArrayList<>();
        mAdapter = new FriendListAdapter(mFriends);
        mRecyclerView.setAdapter(mAdapter);
        refresh();
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

    public void refresh() {
        new FriendListDownloadTask().execute();
    }

    private class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView usernameText;

            public ViewHolder(View view) {
                super(view);
                usernameText = view.findViewById(R.id.usernameTextView);
            }

        }

        private ArrayList<User> mDataset;

        public FriendListAdapter(ArrayList<User> dataset) {
            mDataset = dataset;
        }

        @Override
        public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listelement_friend, parent, false);
            return new ViewHolder(view);
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

    /**
     * Handles downloading the friends list.
     */
    private class FriendListDownloadTask extends AsyncTask<Void, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            return NetworkHelper.getAllFriends(mListener.getUser(), getContext());
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            mFriends.clear();
            mFriends.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }

}
