package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;

public class MainActivity extends AppCompatActivity implements ConversationsOverviewFragment.OnConversationSelectedListener {

    static final int LOGIN_REQUEST = 1;

    private LoggedInUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = (LoggedInUser) getIntent().getExtras().get("user");

        setContentView(R.layout.activity_main);

//        ensureLoggedIn();
    }

    private void ensureLoggedIn() {
        if (mUser == null) {
            login();
        }
    }

    private void login() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, LOGIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_REQUEST:
                mUser = (LoggedInUser) data.getExtras().get("user");
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        Log.v("MainActivity", "onConversationSelected() called");
        Intent conversationIntent = new Intent(this, ConversationActivity.class);
        conversationIntent.putExtra(ConversationActivity.CONVERSATION_THIS_USER, mUser);
        conversationIntent.putExtra(ConversationActivity.CONVERSATION_OTHER_USER, conversation.getOtherPerson());
        startActivity(conversationIntent);
    }

    public LoggedInUser getUser() {
        return mUser;
    }
}
