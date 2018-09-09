package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.network.NetworkHelper;
import pw.bencole.benchat.ui.activities.NewConversationActivity;

/**
 * Downloads from the server and displays a list of all conversations in which the logged in user
 * is a participant. Selecting a conversation from the list triggers a ConversationActivity to be
 * launched to view that conversation.
 *
 * @author Ben Cole
 */
public class ConversationsOverviewFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * References to UI elements
     */
    private RecyclerView mConversationsList;
    private FloatingActionButton mFab;
    private ProgressBar mLoadingSpinner;

    /**
     * A custom adapter to produce previews from Conversation objects
     */
    private ConversationPreviewAdapter mAdapter;
    private ArrayList<Conversation> mConversations;

    /**
     * Reference to the parent activity so that the logged in user can be retrieved
     */
    private OnConversationSelectedListener mListener;

    /**
     * Ensures that the containing activity is able to provide a LoggedInUser instance so that
     * the relevant conversations can be downloaded, and has a handler method to be used when a
     * conversation is selected.
     */
    public interface OnConversationSelectedListener {
        void onConversationSelected(Conversation conversation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_overview, container, false);

        mConversations = new ArrayList<>();
        mConversationsList = view.findViewById(R.id.conversationsOverviewListView);

        mAdapter = new ConversationPreviewAdapter(mConversations, mListener);
        mConversationsList.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mConversationsList.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        mConversationsList.addItemDecoration(divider);

        mFab = view.findViewById(R.id.floatingActionButtonNewConversation);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewConversation();
            }
        });

        mLoadingSpinner = view.findViewById(R.id.loadingSpinner);
        mLoadingSpinner.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConversationSelectedListener) {
            mListener = (OnConversationSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnConversationSelectionListener.");
        }
        // Now we can assume that mListener has an onConversationSelected() method.
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Refreshes the list of conversations.
     */
    public void refresh() {
        mLoadingSpinner.setVisibility(View.VISIBLE);
        mConversations.clear();
        mAdapter.notifyDataSetChanged();
        new ConversationDownloadTask().execute();
    }

    /**
     * Updates the conversations being displayed once the download operation has completed.
     *
     * @param conversations The new list of conversations to display
     */
    private void finishRefresh(ArrayList<Conversation> conversations) {
        mConversations.clear();
        mConversations.addAll(conversations);
        mAdapter.notifyDataSetChanged();
        mLoadingSpinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * Launches a NewConversationActivity instance.
     */
    private void createNewConversation() {
        Intent intent = new Intent(getContext(), NewConversationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mListener.onConversationSelected(mConversations.get(i));
    }

    private static class ConversationPreviewAdapter extends RecyclerView.Adapter<ConversationPreviewAdapter.ViewHolder> {

        private List<Conversation> mConversations;
        private OnConversationSelectedListener mListener;

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView conversationName;
            TextView messagePreview;

            public ViewHolder(View itemView) {
                super(itemView);
                conversationName = itemView.findViewById(R.id.conversationNameTextView);
                messagePreview = itemView.findViewById(R.id.messagePreviewTextView);
            }

        }

        public ConversationPreviewAdapter(List<Conversation> conversations,
                                          OnConversationSelectedListener listener) {
            mConversations = conversations;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.listelement_conversation_overview,
                            parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Conversation conversation = mConversations.get(position);  // final so that it can be used in the click listener
            holder.conversationName.setText(conversation.getConversationName());
            holder.messagePreview.setText(conversation.getMessagePreview());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onConversationSelected(conversation);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mConversations.size();
        }
    }

    /**
     * Asynchronously fetches a list of all conversations for this user. When the download is
     * complete, the finishRefresh() method will be called to handle the changes.
     */
    private class ConversationDownloadTask extends AsyncTask<Void, Void, ArrayList<Conversation>> {

        /**
         * Fetch the conversations from the server.
         */
        @Override
        protected ArrayList<Conversation> doInBackground(Void... voids) {
            return NetworkHelper.getAllConversations(getContext());
        }

        /**
         * Once the download is complete, call the Fragment's handler function to update the
         * conversations that are displayed.
         */
        @Override
        protected void onPostExecute(ArrayList<Conversation> conversations) {
            finishRefresh(conversations);
        }
    }
}
