package pw.bencole.benchat.ui.fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.network.FailureReason;
import pw.bencole.benchat.network.NetworkHelper;


/**
 * Displays a list of friends.
 *
 * @author Ben Cole
 */
public class FriendsFragment extends Fragment {

    /**
     * UI elements for changing tabs
     */
    private TabLayout mTabs;
    private ViewPager mViewPager;

    /**
     * Reference to Floating Action Button
     */
    private FloatingActionButton mFab;

    /**
     * Fragments for the list of friends and the pending friend request screens
     */
    private ConfirmedFriendsFragment mConfirmedFriendsFragment;
    private FriendRequestsFragment mPendingFriendsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mConfirmedFriendsFragment = new ConfirmedFriendsFragment();
        mConfirmedFriendsFragment.setArguments(getArguments());
        mConfirmedFriendsFragment.registerContainingFriendsFragment(this);
        mPendingFriendsFragment = new FriendRequestsFragment();
        mPendingFriendsFragment.setArguments(getArguments());
        mPendingFriendsFragment.registerContainingFriendsFragment(this);

        mViewPager = view.findViewById(R.id.viewPager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);

        mTabs = view.findViewById(R.id.tabs);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        mTabs.setupWithViewPager(mViewPager);

        mFab = view.findViewById(R.id.addFriendFAB);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFriendDialog();
            }
        });

        return view;
    }

    /**
     * Refreshes the information displayed in both sub-fragments.
     */
    public void refresh() {
        mConfirmedFriendsFragment.refresh();
        mPendingFriendsFragment.refresh();
    }

    /**
     * Displays a dialog for adding a new friend.
     */
    private void showAddFriendDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.setTitle(R.string.enter_a_username);
        final EditText usernameField = dialog.findViewById(R.id.usernameField);
        Button addButton = dialog.findViewById(R.id.addButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameField.getText().toString();
                new AddFriendTask(username).execute();
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Handles the result from the server of attempting to add a friend, refreshing the list of
     * friends or displaying an error message as appropriate.
     *
     * @param failureReason The result of the operation from the server
     */
    private void handleAddFriendAttempt(FailureReason failureReason) {
        switch (failureReason) {
            case NONE:
                refresh();
                break;
            case FRIEND_REQUEST_ALREADY_EXISTS:
                Toast.makeText(getContext(), "There is already a friend request with this person.", Toast.LENGTH_SHORT).show();
                break;
            case ALREADY_FRIENDS:
                Toast.makeText(getContext(), "You are already friends with this person.", Toast.LENGTH_SHORT).show();
                break;
            case FRIENDS_WITH_SELF:
                Toast.makeText(getContext(), "You cannot add yourself as a friend!", Toast.LENGTH_SHORT).show();
                break;
            case USER_NOT_FOUND:
                Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapts the two fragments into the ViewPager.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0) ? mConfirmedFriendsFragment : mPendingFriendsFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position == 0) ? "Friends" : "Requests";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * Sends a friend request to the server.
     */
    private class AddFriendTask extends AsyncTask<Void, Void, FailureReason> {

        private String mUsername;

        public AddFriendTask(String username) {
            mUsername = username;
        }

        @Override
        protected FailureReason doInBackground(Void... voids) {
            return NetworkHelper.addFriend(mUsername, getContext());
        }

        @Override
        protected void onPostExecute(FailureReason failureReason) {
            handleAddFriendAttempt(failureReason);
        }
    }

}
