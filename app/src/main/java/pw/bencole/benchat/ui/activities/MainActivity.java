package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;
import pw.bencole.benchat.util.LoginManager;

public class MainActivity extends AppCompatActivity implements ConversationsOverviewFragment.OnConversationSelectedListener {

    private LoggedInUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (LoggedInUser) getIntent().getExtras().get("user");
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversations_overview_menu, menu);
        return true;
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        Log.v("MainActivity", "onConversationSelected() called");
        Intent conversationIntent = new Intent(this, ConversationActivity.class);
        conversationIntent.putExtra(ConversationActivity.CONVERSATION_THIS_USER, mUser);
        conversationIntent.putExtra(ConversationActivity.CONVERSATION_OTHER_USER, conversation.getOtherPerson());
        startActivity(conversationIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOutMenuItem:
                signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Signs the user out, updating the persistent store and returning to LoginActivity.
     */
    private void signOut() {
        LoginManager.logout(this);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public LoggedInUser getUser() {
        return mUser;
    }
}
