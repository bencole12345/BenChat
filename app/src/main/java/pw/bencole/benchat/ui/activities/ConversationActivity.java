package pw.bencole.benchat.ui.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.network.NetworkHelper;
import pw.bencole.benchat.ui.adapters.ConversationMessageAdapter;
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
    private ListView mConversationList;
    private TextView mMessageContent;
    private Button mSendMessageButton;
    private ProgressBar mLoadingMessagesProgressSpinner;

    /**
     * Adapter for displaying messages in a list
     */
    private ConversationMessageAdapter mAdapter;

    /**
     * The ID of the conversation this Activity is displaying
     */
    private String mConversationId;

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
        mConversationList = findViewById(R.id.conversationList);
        mMessageContent = findViewById(R.id.messageContent);
        mSendMessageButton = findViewById(R.id.sendButton);
        mLoadingMessagesProgressSpinner = findViewById(R.id.loadingMessagesProgressSpinner);

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

        // Set the adaptor to display the list of messages
        mAdapter = new ConversationMessageAdapter(this, R.layout.listelement_conversation_message, new ArrayList<Message>());
        mConversationList.setAdapter(mAdapter);

//        mConversationList.setDivider(null);

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
        mAdapter.clear();
        mLoadingMessagesProgressSpinner.setVisibility(View.VISIBLE);
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
     * Downloads the messages for this conversation and updates the UI with the result.
     */
    private class MessagesDownloadTask extends AsyncTask<Void, Void, ArrayList<Message>> {

        @Override
        protected ArrayList<Message> doInBackground(Void... voids) {
            return NetworkHelper.getAllMessagesInConversation(mConversationId, getApplicationContext());
        }

        @Override
        protected void onPostExecute(ArrayList<Message> messages) {
            mAdapter.clear();
            mAdapter.addAll(messages);
            mLoadingMessagesProgressSpinner.setVisibility(View.INVISIBLE);
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
                mAdapter.add(mMessage);
            } else {
                showErrorSendingMessage();
            }
        }
    }
}
