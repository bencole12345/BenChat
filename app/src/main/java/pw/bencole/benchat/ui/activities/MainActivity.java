package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.ConversationPreview;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;
import pw.bencole.benchat.util.LoginManager;


/**
 * The main screen of the app.
 *
 * This Activity shows a list of all conversations in which the user is a member. Selecting one of
 * these conversations initiates a ConversationActivity for that conversation.
 *
 * @author Ben Cole
 */
public class MainActivity extends AppCompatActivity implements ConversationsOverviewFragment.OnConversationSelectedListener {

    /**
     * Tracks the logged in user so that API requests can be made
     */
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
    public void onConversationSelected(ConversationPreview conversation) {
        Intent conversationIntent = new Intent(this, ConversationActivity.class);
        conversationIntent.putExtra(ConversationActivity.CONVERSATION_THIS_USER, mUser);
        conversationIntent.putExtra(ConversationActivity.CONVERSATION_ID, conversation.getId());
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

    /**
     * Returns the user that is logged in to the app.
     *
     * @return the user that is logged in to the app
     */
    public LoggedInUser getUser() {
        return mUser;
    }
}
