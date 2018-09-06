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
import java.util.List;
import java.util.Map;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.FriendRequest;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.NetworkHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestsFragment extends Fragment {

    /**
     * The currently logged in user
     */
    private LoggedInUser mUser;

    /**
     * Stores the list of people, as well as the labels to separate received from sent requests
     */
    private ArrayList<FriendRequestListElement> mElements;

    /**
     * Displays the list of sent and received requests
     */
    private RecyclerView mRecyclerView;
    private FriendRequestListAdapter mAdapter;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        mUser = (LoggedInUser) getArguments().get("user");
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mElements = new ArrayList<>();
        mAdapter = new FriendRequestListAdapter(mElements);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        refresh();

        return view;
    }

    public void refresh(){
        new FriendRequestDownloadTask().execute();
    }

    /**
     * Updates and repopulates the list of displayed friend requests, and handles setting labels
     * correctly.
     *
     * @param data The data fetched from the server
     */
    private void updateDisplayedItems(Map<String, List<FriendRequest>> data) {

        mElements.clear();

        if (data == null || !data.containsKey("sent") || !data.containsKey("received")) {
            mElements.add(new HeaderElement("Loading..."));
            mAdapter.notifyDataSetChanged();
            return;
        }

        List<FriendRequest> sentRequests = data.get("sent");
        List<FriendRequest> receivedRequests = data.get("received");

        if (receivedRequests.size() > 0) {
            mElements.add(new HeaderElement("Received requests"));
        }
        for (FriendRequest request : receivedRequests) {
            mElements.add(new ReceivedRequestElement(request));
        }
        if (sentRequests.size() > 0) {
            mElements.add(new HeaderElement("Sent requests"));
        }
        for (FriendRequest request : sentRequests) {
            mElements.add(new SentRequestElement(request));
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Represents a generic list element, be this a label (eg "received requests") or a row
     * representing a friend.
     */
    private abstract class FriendRequestListElement {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_RECEIVED_REQUEST = 1;
        public static final int TYPE_SENT_REQUEST = 2;

        abstract public int getType();

    }

    /**
     * Represents a header in the list, eg "Received Requests"
     */
    private class HeaderElement extends FriendRequestListElement {

        private String mText;

        public HeaderElement(String text) {
            mText = text;
        }

        public int getType() {
            return FriendRequestListElement.TYPE_HEADER;
        }

        public String getText() {
            return mText;
        }
    }

    /**
     * A generic class representing a person to be displayed in the list
     */
    private abstract class FriendElement extends FriendRequestListElement {

        private FriendRequest mFriendRequest;

        public FriendElement(FriendRequest friendRequest) {
            mFriendRequest = friendRequest;
        }

        public FriendRequest getFriendRequest() {
            return mFriendRequest;
        }
    }

    /**
     * A person that sent a friend request to the logged in user
     */
    private class ReceivedRequestElement extends FriendElement {

        public ReceivedRequestElement(FriendRequest friendRequest) {
            super(friendRequest);
        }

        public int getType() {
            return FriendRequestListElement.TYPE_RECEIVED_REQUEST;
        }
    }

    /**
     * A person to whom the logged in user sent a friend request
     */
    private class SentRequestElement extends FriendElement {

        public SentRequestElement(FriendRequest friendRequest) {
            super(friendRequest);
        }

        public int getType() {
            return FriendRequestListElement.TYPE_SENT_REQUEST;
        }
    }

    /**
     * ViewHolder for header objects
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return mTextView;
        }
    }

    /**
     * Alternate ViewHolder for friend objects
     */
    private class FriendViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameText;
        // TODO: reference ImageView for profile picture

        public FriendViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameTextView);
        }
    }

    private class FriendRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<FriendRequestListElement> mItems;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

        public FriendRequestListAdapter(ArrayList<FriendRequestListElement> items) {
            mItems = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView;
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case FriendRequestListElement.TYPE_HEADER:
                    itemView = inflater.inflate(R.layout.listelement_text_header, parent, false);
                    viewHolder = new HeaderViewHolder(itemView);
                    break;
                case FriendRequestListElement.TYPE_RECEIVED_REQUEST:
                    itemView = inflater.inflate(R.layout.listelement_friend_request_accept_or_decline, parent, false);
                    viewHolder = new FriendViewHolder(itemView);
                    break;
                case FriendRequestListElement.TYPE_SENT_REQUEST:
                    itemView = inflater.inflate(R.layout.listelement_friend, parent, false);
                    viewHolder = new FriendViewHolder(itemView);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            switch (type) {
                case FriendRequestListElement.TYPE_HEADER:
                    HeaderElement header = (HeaderElement) mItems.get(position);
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.mTextView.setText(header.getText());
                    break;
                case FriendRequestListElement.TYPE_RECEIVED_REQUEST:
                case FriendRequestListElement.TYPE_SENT_REQUEST:
                    FriendElement friendElement = (FriendElement) mItems.get(position);
                    FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
                    String username = friendElement.getFriendRequest().getUser().getUsername();
                    friendViewHolder.usernameText.setText(username);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).getType();
        }
    }

    /**
     * Downloads the list of sent and received friend requests.
     */
    private class FriendRequestDownloadTask extends AsyncTask<Void, Void, Map<String, List<FriendRequest>>> {

        @Override
        protected Map<String, List<FriendRequest>> doInBackground(Void... voids) {
            return NetworkHelper.getAllRequests(mUser, getContext());
        }

        @Override
        protected void onPostExecute(Map<String, List<FriendRequest>> stringListMap) {
            updateDisplayedItems(stringListMap);
        }
    }

}
