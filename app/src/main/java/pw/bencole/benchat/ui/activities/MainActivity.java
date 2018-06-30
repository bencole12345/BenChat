package pw.bencole.benchat.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.bencole.benchat.R;
import pw.bencole.benchat.ui.fragments.AllChats;
import pw.bencole.benchat.ui.fragments.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements AllChats.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
