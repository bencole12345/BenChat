package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.ConversationPreview;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.network.NetworkHelper;
import pw.bencole.benchat.ui.activities.NewConversationActivity;
import pw.bencole.benchat.ui.adapters.ConversationPreviewAdapter;

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
    private ListView mConversationsList;
    private FloatingActionButton mFab;
    private ProgressBar mLoadingSpinner;

    /**
     * A custom adapter to produce previews from ConversationPreview objects
     */
    private ConversationPreviewAdapter mAdapter;
    private ArrayList<ConversationPreview> mConversations;

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
        LoggedInUser getUser();
        void onConversationSelected(ConversationPreview conversation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_overview, container, false);

        mConversations = new ArrayList<>();
        mConversationsList = view.findViewById(R.id.conversationsOverviewListView);
        mConversationsList.setOnItemClickListener(this);

        mFab = view.findViewById(R.id.floatingActionButtonNewConversation);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewConversation();
            }
        });

        mLoadingSpinner = view.findViewById(R.id.loadingSpinner);
        mLoadingSpinner.setVisibility(View.INVISIBLE);

        mAdapter = new ConversationPreviewAdapter(mListener.getUser(), getContext(), R.layout.listelement_conversation_overview, mConversations);
        mConversationsList.setAdapter(mAdapter);

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
        mAdapter.clear();
        new ConversationDownloadTask().execute();
    }

    /**
     * Updates the conversations being displayed once the download operation has completed.
     *
     * @param conversations The new list of conversations to display
     */
    private void finishRefresh(ArrayList<ConversationPreview> conversations) {
        mAdapter.addAll(conversations);
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
        intent.putExtra("user", mListener.getUser());
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mListener.onConversationSelected(mConversations.get(i));
    }

    /**
     * Asynchronously fetches a list of all conversations for this user. When the download is
     * complete, the finishRefresh() method will be called to handle the changes.
     */
    private class ConversationDownloadTask extends AsyncTask<Void, Void, ArrayList<ConversationPreview>> {

        /**
         * Fetch the conversations from the server.
         */
        @Override
        protected ArrayList<ConversationPreview> doInBackground(Void... voids) {
            return NetworkHelper.getAllConversations(mListener.getUser(), getContext());
        }

        /**
         * Once the download is complete, call the Fragment's handler function to update the
         * conversations that are displayed.
         */
        @Override
        protected void onPostExecute(ArrayList<ConversationPreview> conversations) {
            finishRefresh(conversations);
        }
    }
}
