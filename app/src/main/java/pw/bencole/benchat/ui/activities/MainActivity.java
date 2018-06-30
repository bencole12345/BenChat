package pw.bencole.benchat.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.ui.fragments.ConversationsOverviewFragment;

public class MainActivity extends AppCompatActivity implements ConversationsOverviewFragment.OnConversationSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onConversationSelected(Conversation conversation) {

    }
}
