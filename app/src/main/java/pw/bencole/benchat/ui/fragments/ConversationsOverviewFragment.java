package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.NetworkHelper;
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

    /**
     * A custom adapter to produce previews of Conversation objects
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
        LoggedInUser getUser();
        void onConversationSelected(Conversation conversation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_overview, container, false);

        mConversations = new ArrayList<>();
        mConversationsList = view.findViewById(R.id.conversationsOverviewListView);
        mConversationsList.setOnItemClickListener(this);

        mAdapter = new ConversationPreviewAdapter(getContext(), R.layout.listelement_conversation_overview, mConversations);
        mConversationsList.setAdapter(mAdapter);

        new ConversationDownloadTask().execute();

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
     * Returns dummy conversations to be used in development while the network functionality
     * has not yet been implemented.
     */
    private ArrayList<Conversation> getConversations() {
        // TODO: Actually get conversations!
        ArrayList<Conversation> conversations = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            User user = new User("User " + i, null);
            ArrayList<Message> messages = NetworkHelper.getAllMessagesBetween(mListener.getUser(), user);
//            messages.add(new Message("First message of conversation " + i, user, user));
            Conversation conversation = new Conversation(user, messages);
            conversations.add(conversation);
        }
        return conversations;
    }

    /**
     * Updates the conversations being displayed once the download operation has completed.
     *
     * @param conversations The new list of conversations to display
     */
    private void updateConversations(ArrayList<Conversation> conversations) {
        mAdapter.clear();
        mAdapter.addAll(conversations);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mListener.onConversationSelected(mConversations.get(i));
    }

    /**
     * Asynchronously fetches a list of all conversations for this user. When the download is
     * complete, the updateConversations() method will be called to handle the changes.
     */
    private class ConversationDownloadTask extends AsyncTask<Void, Void, ArrayList<Conversation>> {

        /**
         * Fetch the conversations from the server.
         */
        @Override
        protected ArrayList<Conversation> doInBackground(Void... voids) {
            return NetworkHelper.getAlLConversations(mListener.getUser(), getContext());
        }

        /**
         * Once the download is complete, call the Fragment's handler function to update the
         * conversations that are displayed.
         */
        @Override
        protected void onPostExecute(ArrayList<Conversation> conversations) {
            updateConversations(conversations);
        }
    }
}
