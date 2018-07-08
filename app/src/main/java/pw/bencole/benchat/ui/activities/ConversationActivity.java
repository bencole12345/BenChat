package pw.bencole.benchat.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.NetworkHelper;
import pw.bencole.benchat.ui.adapters.ConversationMessageAdapter;

public class ConversationActivity extends AppCompatActivity {

    public static String CONVERSATION_THIS_USER = "conversation_this_user";
    public static String CONVERSATION_OTHER_USER = "conversation_other_user";

    private ListView mConversationList;
    private TextView mMessageContent;
    private Button mSendMessageButton;

    private LoggedInUser mThisUser;
    private User mOtherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Unpack the users passed to this activity
        mThisUser = (LoggedInUser) getIntent().getExtras().get(CONVERSATION_THIS_USER);
        mOtherUser = (User) getIntent().getExtras().get(CONVERSATION_OTHER_USER);

        // Set the activity title to the username of the other person
        setTitle(mOtherUser.getUsername());

        // Find the view elements from the layout
        mConversationList = findViewById(R.id.conversationList);
        mMessageContent = findViewById(R.id.messageContent);
        mSendMessageButton = findViewById(R.id.sendButton);

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
        ConversationMessageAdapter adapter = new ConversationMessageAdapter(this, R.layout.listelement_conversation_message, getMessages());
        mConversationList.setAdapter(adapter);
    }

    /**
     * Loads and returns all messages in chronological order between the logged in user and the
     * other user.
     * @return All messages between this user and the other user
     */
    private List<Message> getMessages() {
        return NetworkHelper.getAllMessagesBetween(mThisUser, mOtherUser);
    }

    /**
     * Attempts to send the message contained in the text field. If there is an error (eg network
     * or empty text field) then an it will be handled and an error displayed.
     */
    private void sendMessage() {
        // TODO: Send the message
    }
}
