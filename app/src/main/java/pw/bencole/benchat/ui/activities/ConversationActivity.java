package pw.bencole.benchat.ui.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.network.NetworkHelper;
import pw.bencole.benchat.ui.adapters.ConversationMessageAdapter;

public class ConversationActivity extends AppCompatActivity {

    public static String CONVERSATION_THIS_USER = "conversation_this_user";
    public static String CONVERSATION_ID = "conversation_id";

    private ListView mConversationList;
    private TextView mMessageContent;
    private Button mSendMessageButton;
    private ProgressBar mLoadingMessagesProgressSpinner;

    private ConversationMessageAdapter mAdapter;

    private LoggedInUser mLoggedInUser;
    private String mConversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Unpack the users passed to this activity
        mLoggedInUser = (LoggedInUser) getIntent().getExtras().get(CONVERSATION_THIS_USER);
        mConversationId = getIntent().getExtras().getString(CONVERSATION_ID);

        // TODO: set the activity title

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

        refreshMessages();
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
        Message message = new Message(content, mLoggedInUser);
        new SendMessageTask().execute(message);
        // Show a dialog or spinner to show that the message is sending
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
            return NetworkHelper.getAllMessagesInConversation(mLoggedInUser, mConversationId, getApplicationContext());
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
            return NetworkHelper.sendMessage(mLoggedInUser, mMessage, mConversationId, getApplicationContext());
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
