package pw.bencole.benchat.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.ConversationPreview;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;
import pw.bencole.benchat.ui.fragments.FriendsListFragment;
import pw.bencole.benchat.ui.fragments.SettingsFragment;
import pw.bencole.benchat.ui.view.ToggleableSwipeViewPager;
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

    /**
     * References to UI elements
     */
    private BottomNavigationView mBottomNavigationView;
    private ToggleableSwipeViewPager mPager;

    private FriendsListFragment mFriendsListFragment;
    private ConversationsOverviewFragment mConversationsOverviewFragment;
    private SettingsFragment mSettingsFragment;

    private BottomNavigationPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (LoggedInUser) getIntent().getExtras().get("user");
        setContentView(R.layout.activity_main);

        // Create exactly one instance of each fragment.
        mFriendsListFragment = new FriendsListFragment();
        mConversationsOverviewFragment = new ConversationsOverviewFragment();
        mSettingsFragment = new SettingsFragment();

        // Set up the view pager and adapter, passing references to the fragments to its
        // constructor.
        mPager = findViewById(R.id.pager);
        mAdapter = new BottomNavigationPagerAdapter(getSupportFragmentManager(),
                mFriendsListFragment,
                mConversationsOverviewFragment,
                mSettingsFragment
        );
        mPager.setAdapter(mAdapter);
        mPager.setAllowSwiping(false);

        // Hook up the BottomNavigationView's handler.
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
                mPager.setCurrentItem(0, false);
                setTitle("Friends");
                break;
            case R.id.action_conversations:
                mPager.setCurrentItem(1, false);
                setTitle("Conversations");
                break;
            case R.id.action_settings:
                mPager.setCurrentItem(2, false);
                setTitle("Settings");
                break;
        }
        return true;
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
        switch (mBottomNavigationView.getSelectedItemId()) {
            case R.id.action_conversations:
                mConversationsOverviewFragment.refresh();
                return;
            case R.id.action_friends:
                mFriendsListFragment.refresh();
        }
    }

    /**
     * Returns the user that is logged in to the app.
     *
     * @return the user that is logged in to the app
     */
    public LoggedInUser getUser() {
        return mUser;
    }

    /**
     * Serves the correct Fragment to the ViewPager.
     *
     * No fragments are instantiated here: instead, they must be instantiated externally and the
     * references be passed in. This way, this class can reference them, but at the same time,
     * they can be accessed (eg refresh() called) externally.
     */
    private static class BottomNavigationPagerAdapter extends FragmentPagerAdapter {

        /**
         * References to external fragments
         */
        private FriendsListFragment mFriendsListFragment;
        private ConversationsOverviewFragment mConversationsOverviewFragment;
        private SettingsFragment mSettingsFragment;

        public BottomNavigationPagerAdapter(FragmentManager fm, FriendsListFragment friendsListFragment,
                                            ConversationsOverviewFragment conversationsOverviewFragment,
                                            SettingsFragment settingsFragment) {
            super(fm);
            mFriendsListFragment = friendsListFragment;
            mConversationsOverviewFragment = conversationsOverviewFragment;
            mSettingsFragment = settingsFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mFriendsListFragment;
                case 1:
                    return mConversationsOverviewFragment;
                case 2:
                    return mSettingsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
