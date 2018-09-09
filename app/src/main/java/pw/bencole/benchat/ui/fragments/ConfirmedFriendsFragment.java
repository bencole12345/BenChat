package pw.bencole.benchat.ui.fragments;


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
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.NetworkHelper;


/**
 * A Fragment that displays a scrollable list of all (confirmed) friends.
 *
 * Any Activity or Fragment that contains this must implement Refreshable.
 *
 * @author Ben Cole
 */
public class ConfirmedFriendsFragment extends Fragment {

    private ArrayList<User> mFriends;

    private RecyclerView mRecyclerView;
    private FriendListAdapter mAdapter;

    public ConfirmedFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmed_friends, container, false);

        mRecyclerView = view.findViewById(R.id.confirmedFriendsList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mFriends = new ArrayList<>();
        mAdapter = new FriendListAdapter(mFriends);
        mRecyclerView.setAdapter(mAdapter);
        refresh();

        return view;
    }

    /**
     * Saves a reference to the containing FriendsFragment so that it can be notified if it needs
     * to be updated.
     */
    public void registerContainingFriendsFragment(FriendsFragment parent) {
    }

    /**
     * Adapts a list of User objects to be displayed as the friends list.
     */
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


    public void refresh() {
        new FriendListDownloadTask().execute();
    }

    /**
     * Handles downloading the friends list.
     */
    private class FriendListDownloadTask extends AsyncTask<Void, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            return NetworkHelper.getAllFriends(getContext());
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            mFriends.clear();
            mFriends.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }


}
