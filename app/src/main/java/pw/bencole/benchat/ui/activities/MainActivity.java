package pw.bencole.benchat.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.ConversationPreview;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;
import pw.bencole.benchat.ui.fragments.FriendsListFragment;
import pw.bencole.benchat.ui.fragments.SettingsFragment;
import pw.bencole.benchat.util.LoginManager;


/**
 * The main screen of the app.
 *
 * This Activity shows a list of all conversations in which the user is a member. Selecting one of
 * these conversations initiates a ConversationActivity for that conversation.
 *
 * @author Ben Cole
 */
public class MainActivity extends AppCompatActivity
        implements ConversationsOverviewFragment.OnConversationSelectedListener,
                   FriendsListFragment.FriendListInteractionListener,
                   BottomNavigationView.OnNavigationItemSelectedListener {

    /**
     * Tracks the logged in user so that API requests can be made
     */
    private LoggedInUser mUser;

    private BottomNavigationView mBottomNavigationView;
    private FriendsListFragment mFriendsListFragment;
    private ConversationsOverviewFragment mConversationsOverviewFragment;
    private SettingsFragment mSettingsFragment;
    private FrameLayout mPlaceholderFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (LoggedInUser) getIntent().getExtras().get("user");
        setContentView(R.layout.activity_main);

        mFriendsListFragment = new FriendsListFragment();
        mConversationsOverviewFragment = new ConversationsOverviewFragment();
        // TODO: Create a SettingsFragment
        mSettingsFragment = new SettingsFragment();
        mPlaceholderFrame = findViewById(R.id.activeFragment);

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mBottomNavigationView.setSelectedItemId(R.id.action_conversations);

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
                showSignoutDialog();
                return true;
            case R.id.refreshMenuItem:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friends:
                switchFragment(mFriendsListFragment);
                setTitle("Friends");
                break;
            case R.id.action_conversations:
                switchFragment(mConversationsOverviewFragment);
                setTitle("Conversations");
                break;
            case R.id.action_settings:
                // TODO: Show settings fragment
                switchFragment(mSettingsFragment);
                setTitle("Settings");
                break;
        }
        return true;
    }

    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activeFragment, targetFragment);
        transaction.commit();
    }

    /**
     * Shows a dialog box asking the user whether they would like to sign out. If they accept,
     * then the user will be signed out and the activity replaced with a LoginActivity.
     */
    private void showSignoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOut();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
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
     * Refreshes the information displayed.
     */
    private void refresh() {
        // TODO: Only refresh the active fragment
        mConversationsOverviewFragment.refresh();
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
