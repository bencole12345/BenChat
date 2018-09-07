package pw.bencole.benchat.ui.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.FriendRequest;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.NetworkHelper;

/**
 * A Fragment that displays a list of received and sent friend requests.
 *
 * Any Activity or Fragment containing this must implement Refreshable.
 *
 * @author Ben Cole
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

    /**
     * Used to ensure that the parent fragment can be refreshed when a change is made
     */
    private FriendsFragment mParent;

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

    /**
     * Saves a reference to the containing FriendsFragment so that it can be notified if it needs
     * to be updated.
     */
    public void registerContainingFriendsFragment(FriendsFragment parent) {
        mParent = parent;
    }

    public void refresh() {
        // TODO: display progress bar
        new FriendRequestDownloadTask().execute();
    }

    /**
     * Handles the result of the attempted accept, decline or cancel of a friend request.
     *
     * If the operation was successful then the friend list will be refreshed. Otherwise, a
     * message indicating that there was a network error will be displayed.
     *
     * @param success Whether the operation was successful
     */
    private void handleNetworkOperationResult(Boolean success) {
        if (success) {
            if (mParent != null) mParent.refresh();
        } else {
            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
        }
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
            mElements.add(new ReceivedFriendRequestElement(request));
        }
        if (sentRequests.size() > 0) {
            mElements.add(new HeaderElement("Sent requests"));
        }
        for (FriendRequest request : sentRequests) {
            mElements.add(new SentFriendRequestElement(request));
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
    private class ReceivedFriendRequestElement extends FriendElement {

        public ReceivedFriendRequestElement(FriendRequest friendRequest) {
            super(friendRequest);
        }

        public int getType() {
            return FriendRequestListElement.TYPE_RECEIVED_REQUEST;
        }
    }

    /**
     * A person to whom the logged in user sent a friend request
     */
    private class SentFriendRequestElement extends FriendElement {

        public SentFriendRequestElement(FriendRequest friendRequest) {
            super(friendRequest);
        }

        public int getType() {
            return FriendRequestListElement.TYPE_SENT_REQUEST;
        }
    }

    /**
     * ViewHolder for header objects
     */
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

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
     * ViewHolder for received friend requests
     */
    private static class ReceivedFriendRequestViewHolder extends RecyclerView.ViewHolder
                                                  implements View.OnClickListener {

        ImageView profilePictureImageView;
        TextView usernameText;
        Button confirmButton;
        Button declineButton;

        ReceivedFriendRequestClickListener listener;

        public ReceivedFriendRequestViewHolder(View itemView, ReceivedFriendRequestClickListener listener) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameTextView);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            profilePictureImageView = itemView.findViewById(R.id.profilePicture);

            this.listener = listener;
            confirmButton.setOnClickListener(this);
            declineButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.confirmButton:
                    listener.confirm(this.getLayoutPosition());
                    break;
                case R.id.declineButton:
                    listener.decline(this.getLayoutPosition());
            }
        }

        public interface ReceivedFriendRequestClickListener {
            void confirm(int position);
            void decline(int position);
        }
    }

    /**
     * ViewHolder for sent friend requests
     */
    private static class SentFriendRequestViewHolder extends RecyclerView.ViewHolder
                                                     implements View.OnClickListener {

        ImageView profilePictureImageView;
        TextView usernameText;
        Button cancelButton;

        SentFriendRequestClickListener listener;

        public SentFriendRequestViewHolder(View itemView, SentFriendRequestClickListener listener) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            profilePictureImageView = itemView.findViewById(R.id.profilePicture);

            this.listener = listener;
            cancelButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.cancelButton) {
                listener.cancel(this.getLayoutPosition());
            }
        }

        public interface SentFriendRequestClickListener {
            void cancel(int position);
        }
    }

    private class FriendRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<FriendRequestListElement> mItems;

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
                    viewHolder = new ReceivedFriendRequestViewHolder(itemView, new ReceivedFriendRequestViewHolder.ReceivedFriendRequestClickListener() {
                        @Override
                        public void confirm(int position) {
                            Toast.makeText(getContext(), "confirming request in position " + position, Toast.LENGTH_SHORT).show();
                            ReceivedFriendRequestElement element = (ReceivedFriendRequestElement) mItems.get(position);
                            new AcceptOrDeclineRequestTask(element.getFriendRequest(), true).execute();
                        }
                        @Override
                        public void decline(int position) {
                            Toast.makeText(getContext(), "declining request in position " + position, Toast.LENGTH_SHORT).show();
                            ReceivedFriendRequestElement element = (ReceivedFriendRequestElement) mItems.get(position);
                            new AcceptOrDeclineRequestTask(element.getFriendRequest(), false).execute();
                        }
                    });
                    break;
                case FriendRequestListElement.TYPE_SENT_REQUEST:
                    itemView = inflater.inflate(R.layout.listelement_friend_request_awaiting, parent, false);
                    viewHolder = new SentFriendRequestViewHolder(itemView, new SentFriendRequestViewHolder.SentFriendRequestClickListener() {
                        @Override
                        public void cancel(int position) {
                            Toast.makeText(getContext(), "cancelling request in position " + position, Toast.LENGTH_SHORT).show();
                            SentFriendRequestElement element = (SentFriendRequestElement) mItems.get(position);
                            new CancelRequestTask(element.getFriendRequest()).execute();
                        }
                    });
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String username;
            int type = getItemViewType(position);
            switch (type) {

                case FriendRequestListElement.TYPE_HEADER:
                    HeaderElement header = (HeaderElement) mItems.get(position);
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.mTextView.setText(header.getText());
                    break;

                case FriendRequestListElement.TYPE_RECEIVED_REQUEST:
                    ReceivedFriendRequestElement receivedFriendRequestElement = (ReceivedFriendRequestElement) mItems.get(position);
                    ReceivedFriendRequestViewHolder receivedFriendRequestViewHolder = (ReceivedFriendRequestViewHolder) holder;
                    username = receivedFriendRequestElement.getFriendRequest().getUser().getUsername();
                    receivedFriendRequestViewHolder.usernameText.setText(username);
                    // TODO: set profile picture?
                    break;

                case FriendRequestListElement.TYPE_SENT_REQUEST:
                    SentFriendRequestElement sentFriendRequestElement = (SentFriendRequestElement) mItems.get(position);
                    SentFriendRequestViewHolder sentFriendRequestViewHolder = (SentFriendRequestViewHolder) holder;
                    username = sentFriendRequestElement.getFriendRequest().getUser().getUsername();
                    sentFriendRequestViewHolder.usernameText.setText(username);
                    // TODO: set profile picture?
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

    /**
     * Accepts or declines a received friend request.
     */
    private class AcceptOrDeclineRequestTask extends AsyncTask<Void, Void, Boolean> {

        private FriendRequest mRequest;
        private boolean mAccept;
        private User mCreatedFriend;

        public AcceptOrDeclineRequestTask(FriendRequest request, boolean accept) {
            super();
            mRequest = request;
            mAccept = accept;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (mAccept) {
                mCreatedFriend = NetworkHelper.confirmFriendRequest(mUser, mRequest, getContext());
                return (mCreatedFriend != null);
            } else {
                return NetworkHelper.declineFriendRequest(mUser, mRequest, getContext());
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            handleNetworkOperationResult(success);
            if (mAccept) {
                new CreateInitialConversationTask(mCreatedFriend).execute();
            }
        }
    }

    /**
     * Cancels a sent friend request.
     */
    private class CancelRequestTask extends AsyncTask<Void, Void, Boolean> {

        private FriendRequest mRequest;

        public CancelRequestTask(FriendRequest request) {
            super();
            mRequest = request;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return NetworkHelper.cancelSentFriendRequest(mUser, mRequest, getContext());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            handleNetworkOperationResult(success);
        }
    }

    private class CreateInitialConversationTask extends AsyncTask<Void, Void, Void> {

        private User mOtherUser;

        public CreateInitialConversationTask(User otherUser) {
            mOtherUser = otherUser;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HashSet<User> otherUsers = new HashSet<>();
            otherUsers.add(mOtherUser);
            NetworkHelper.createConversation(mUser, otherUsers, getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // TODO: update the Activity so that the new conversation shows up
        }
    }

}
