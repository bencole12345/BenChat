package pw.bencole.benchat.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
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

    private void showAddFriendDialog() {
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle(R.string.add_friend)
////               .setMessage(R.string.enter_a_username)
//               .setView(inflater.inflate(R.layout.dialog_add_friend, null))
//               .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
//                   @Override
//                   public void onClick(DialogInterface dialogInterface, int i) {
//                       Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
//                   }
//               });
//
//        builder.show();

//        DialogFragment dialog = new AddFriendDialogFragment();
//        dialog.show(getFragmentManager(), "AddFriendDialog");


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
                // TODO: send to server
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

//    public static class AddFriendDialogFragment extends DialogFragment {
//
//        FriendsFragment mFriendsFragment;
//        EditText mUsernameField;
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            View view = super.onCreateView(inflater, container, savedInstanceState);
//            mUsernameField = view.findViewById(R.id.usernameField);
//            return view;
//        }
//
//        public void registerFriendsFragment(FriendsFragment friendsFragment) {
//            mFriendsFragment = friendsFragment;
//        }
//
//    }

}
