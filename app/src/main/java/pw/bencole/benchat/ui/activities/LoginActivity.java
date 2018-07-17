package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.ui.fragments.LoginFragment;
import pw.bencole.benchat.ui.fragments.SignupFragment;
import pw.bencole.benchat.util.LoginManager;

// TODO: Stop using deprecated method calls!

/**
 * Handles logging in or signing up a user.
 *
 * The aim of this Activity is to produce a LoggedInUser object containing the username, id and
 * password of a valid user. The user can either log in if they already have an account, or sign up
 * otherwise. Once this is complete and the relevant fragment has received confirmation from the
 * server that it was successful, a MainActivity instance will be created and the newly constructed
 * LoggedInUser instance will be passed to it.
 *
 * The Activity contains a LoginFragment and a SignupFragment, using tabs and a ViewPager to
 * switch between them.
 */
public class LoginActivity extends AppCompatActivity
        implements LoginFragment.LoginFragmentListener, SignupFragment.SignupFragmentListener {

    /**
     * ViewPager and PagerAdapter to handle switching Fragments
     */
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPager = findViewById(R.id.loginViewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };
        actionBar.addTab(actionBar.newTab().setText("Log In").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Sign Up").setTabListener(tabListener));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        Fragment LOGIN_FRAGMENT = new LoginFragment();
        Fragment SIGNUP_FRAGMENT = new SignupFragment();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0) ? LOGIN_FRAGMENT : SIGNUP_FRAGMENT;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * Called by the LoginFragment when a user successfully logs in.
     * @param user The user that was just successfully logged in
     */
    public void onLoginComplete(LoggedInUser user) {
        login(user);
    }

    /**
     * Called by the SignupFragment when a new user is successfully signed up and logged in.
     * @param user The user that was just successfully signed up and logged in
     */
    public void onSignupCompletion(LoggedInUser user) {
        login(user);
    }

    /**
     * Completes the login by setting the logged in user persistently and starting a MainActivity
     * instance with this user.
     * @param user The user that was just logged in (and potentially signed up)
     */
    private void login(LoggedInUser user) {
        LoginManager.setLoggedInUser(user, this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}
