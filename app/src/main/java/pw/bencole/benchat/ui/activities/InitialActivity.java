package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;

        if (getIsLoggedIn()) {
            activityIntent = new Intent(this, MainActivity.class);
            activityIntent.putExtra("user", getUser());
        } else {
            activityIntent = new Intent(this, LoginActivity.class);
        }

        startActivity(activityIntent);
        finish();
    }

    private boolean getIsLoggedIn() {
        // TODO: Use a local file to store login details
        return false;
    }

    private LoggedInUser getUser() {
        // TODO: Retrieve the previously logged in user from a local filestore
        LoggedInUser user = new LoggedInUser("ben", null);
        return user;
    }
}
