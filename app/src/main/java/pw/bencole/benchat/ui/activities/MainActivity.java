package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;
import pw.bencole.benchat.ui.fragments.LoginFragment;
import pw.bencole.benchat.ui.fragments.SignupFragment;

public class MainActivity extends AppCompatActivity implements ConversationsOverviewFragment.OnConversationSelectedListener {

    static final int LOGIN_REQUEST = 1;

    private LoggedInUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ensureLoggedIn();
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
                // TODO: Unpack data from the activity result and set mUser
        }
    }





    @Override
    public void onConversationSelected(Conversation conversation) {

    }
}
