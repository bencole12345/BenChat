package pw.bencole.benchat.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
     * Fragments for the list of friends and the pending friend request screens
     */
    private ConfirmedFriendsFragment mConfirmedFriendsFragment;
    private FriendRequestsFragment mPendingFriendsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mConfirmedFriendsFragment = new ConfirmedFriendsFragment();
        mConfirmedFriendsFragment.setArguments(getArguments());
        mPendingFriendsFragment = new FriendRequestsFragment();
        mPendingFriendsFragment.setArguments(getArguments());

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

        return view;
    }

    /**
     * Refreshes the information displayed in both sub-fragments.
     */
    public void refresh() {
        mConfirmedFriendsFragment.refresh();
        mPendingFriendsFragment.refresh();
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

}
