package pw.bencole.benchat.ui.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.network.NetworkHelper;
import pw.bencole.benchat.util.LoginManager;


/**
 * Displays the messages in a conversation, and contains a text box for the user to send their
 * own messages to this conversation.
 *
 * @author Ben Cole
 */
public class ConversationActivity extends AppCompatActivity {

    /**
     * Tags for passing information about the conversation in question to this activity
     */
    public static String CONVERSATION_ID = "conversation_id";

    /**
     * References to UI elements
     */
    private RecyclerView mMessagesRecyclerView;
    private TextView mMessageContent;
    private Button mSendMessageButton;
    private ProgressBar mProgressSpinner;

    /**
     * Adapter for the RecyclerView
     */
    private MessagesAdapter mAdapter;

    /**
     * List of conversations
     */
    private List<Message> mMessages;

    /**
     * The ID of the conversation this Activity is displaying
     */
    private String mConversationId;

    /**
     * Variables used to determine whether to show the progress spinner
     */
    private boolean mDownloadingMessages = false;
    private boolean mSendingMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Display a back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load the conversation ID passed to this activity
        mConversationId = getIntent().getExtras().getString(CONVERSATION_ID);

        // TODO: Set activity name

        // Find the view elements from the layout
        mMessagesRecyclerView = findViewById(R.id.conversationList);
        mMessageContent = findViewById(R.id.messageContent);
        mSendMessageButton = findViewById(R.id.sendButton);
        mProgressSpinner = findViewById(R.id.loadingMessagesProgressSpinner);

        // Disable the send message button if the text field is empty to prevent sending empty
        // messages
        mSendMessageButton.setEnabled(false);
        mMessageContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mMessageContent.getText().length() == 0) {
                    mSendMessageButton.setEnabled(false);
                } else {
                    mSendMessageButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up the send message button callback function
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // Load whether to show timestamps from the settings preferences file
        boolean showTimestamps = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_timestamps", false);

        // Set up an array of messages and pass it to an adapter for the RecyclerView
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages, showTimestamps, this);
        mMessagesRecyclerView.setAdapter(mAdapter);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMessagesRecyclerView.setLayoutManager(layoutManager);

        refreshMessages();
    }

    /**
     * Ends the Activity when the back button in the action bar is clicked.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Set the Menu for this Activity.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation_activity_menu, menu);
        return true;
    }

    /**
     * Set up a handler for each menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshMenuItem:
                refreshMessages();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Begins an asynchronous download task to refresh the messages that are displayed.
     */
    private void refreshMessages() {
        mMessages.clear();
        // TODO: mAdapter.notifyDataSetChanged() ?
        mDownloadingMessages = true;
        updateSpinner();
        new MessagesDownloadTask().execute();
    }

    /**
     * Attempts to send the message contained in the text field. If there is an error (eg network
     * or empty text field) then an it will be handled and an error displayed.
     */
    private void sendMessage() {
        String content = mMessageContent.getText().toString();
        mMessageContent.setText("");
        Message message = new Message(content, LoginManager.getInstance().getLoggedInUser());
        mSendingMessage = true;
        updateSpinner();
        new SendMessageTask().execute(message);
        // TODO: Show a dialog or spinner to show that the message is sending
    }

    /**
     * Displays an error message explaining that there was a problem sending the message.
     */
    private void showErrorSendingMessage() {
        // TODO: Show a dialog
        Toast.makeText(this, "There was a problem sending the message.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Decides whether the progress spinner should be hidden or shown, and hides/shows it
     * accordingly.
     */
    private void updateSpinner() {
        int visibility = (mDownloadingMessages || mSendingMessage) ? View.VISIBLE : View.INVISIBLE;
        mProgressSpinner.setVisibility(visibility);
    }

    /**
     * Adapts an array of messages to be displayed in the recycler view.
     */
    private static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

        private List<Message> mMessages;
        private boolean mDisplayTimestamps;

        /**
         * An alternate colour used to highlight that the message was sent by the user
         */
        private int mThisUserColour;

        public static class MessageViewHolder extends RecyclerView.ViewHolder {

            TextView username;
            TextView timestamp;
            TextView content;

            public MessageViewHolder(View itemView) {
                super(itemView);
                username = itemView.findViewById(R.id.usernameText);
                timestamp = itemView.findViewById(R.id.timestampText);
                content = itemView.findViewById(R.id.contentText);
            }
        }

        public MessagesAdapter(List<Message> messages, boolean displayTimestamps, Context context) {
            mMessages = messages;
            mDisplayTimestamps = displayTimestamps;
            mThisUserColour = ContextCompat.getColor(context, R.color.colorSecondary);
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listelement_conversation_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = mMessages.get(position);
            holder.username.setText(message.getSender().getUsername());
            holder.content.setText(message.getContent());
            holder.timestamp.setText(message.getTimestamp());
            if (mDisplayTimestamps) {
                holder.timestamp.setVisibility(View.VISIBLE);
            } else {
                holder.timestamp.setVisibility(View.INVISIBLE);
            }
            if (message.getSender().getId().equals(LoginManager.getInstance().getLoggedInUser().getId())) {
                holder.username.setTextColor(mThisUserColour);
            }
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

    }

    /**
     * Downloads the messages for this conversation and updates the UI with the result.
     */
    private class MessagesDownloadTask extends AsyncTask<Void, Void, ArrayList<Message>> {

        @Override
        protected ArrayList<Message> doInBackground(Void... voids) {
            return NetworkHelper.getAllMessagesInConversation(mConversationId, getApplicationContext());
        }

        @Override
        protected void onPostExecute(ArrayList<Message> messages) {
            mMessages.clear();
            mMessages.addAll(messages);
            mAdapter.notifyDataSetChanged();
            mDownloadingMessages = false;
            updateSpinner();
        }
    }

    /**
     * Sends the user's message to the server.
     */
    private class SendMessageTask extends AsyncTask<Message, Void, Boolean> {

        private Message mMessage;

        @Override
        protected Boolean doInBackground(Message... messages) {
            mMessage = messages[0];
            return NetworkHelper.sendMessage(mMessage, mConversationId, getApplicationContext());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                mMessages.add(mMessage);
                mAdapter.notifyDataSetChanged();
            } else {
                showErrorSendingMessage();
            }
            mSendingMessage = false;
            updateSpinner();
        }
    }
}
