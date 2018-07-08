package pw.bencole.benchat.ui.fragments;

import android.content.Context;
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
import pw.bencole.benchat.ui.adapters.ConversationArrayAdapter;

/**
 * @author Ben Cole
 */
public class ConversationsOverviewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mConversationsList;
    private ArrayList<Conversation> mConversations;

    private OnConversationSelectedListener mListener;

    public interface OnConversationSelectedListener {
        void onConversationSelected(Conversation conversation);
        LoggedInUser getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_overview, container, false);

        mConversations = getConversations();
        mConversationsList = view.findViewById(R.id.conversationsOverviewListView);
        mConversationsList.setOnItemClickListener(this);

        ConversationArrayAdapter adapter = new ConversationArrayAdapter(getContext(), R.layout.listelement_conversation_overview, getConversations());
        mConversationsList.setAdapter(adapter);

        return view;
    }
    // Handles binding this Fragment to some Activity

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
    // Similarly, handles unbinding this Fragment from an Activity

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private ArrayList<Conversation> getConversations() {
        ArrayList<Conversation> conversations = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            User user = new User("User " + i);
            ArrayList<Message> messages = NetworkHelper.getAllMessagesBetween(mListener.getUser(), user);
//            messages.add(new Message("First message of conversation " + i, user, user));
            Conversation conversation = new Conversation(user, messages);
            conversations.add(conversation);
        }
        return conversations;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mListener.onConversationSelected(mConversations.get(i));
    }
}
