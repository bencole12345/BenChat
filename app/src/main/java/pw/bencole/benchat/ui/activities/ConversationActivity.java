package pw.bencole.benchat.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

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

    private LoggedInUser mThisUser;
    private User mOtherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mThisUser = (LoggedInUser) getIntent().getExtras().get(CONVERSATION_THIS_USER);
        mOtherUser = (User) getIntent().getExtras().get(CONVERSATION_OTHER_USER);
        setTitle(mOtherUser.getUsername());

        mConversationList = findViewById(R.id.conversationList);

        ConversationMessageAdapter adapter = new ConversationMessageAdapter(this, R.layout.listelement_conversation_message, getMessages());
        mConversationList.setAdapter(adapter);

    }

    private List<Message> getMessages() {
        return NetworkHelper.getAllMessagesBetween(mThisUser, mOtherUser);
    }
}
