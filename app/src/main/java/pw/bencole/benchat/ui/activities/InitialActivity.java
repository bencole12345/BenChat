package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.bencole.benchat.util.LoginManager;

/**
 * Acts as an initial activity to be used when the app is first loaded. It will never be seen by
 * the user. It determines whether the user has already logged in previously. If they have not,
 * a LoginActivity will be shown. If they have, then the details used previously will be used again
 * and the login screen bypassed, with a MainActivity instance launched instead.
 *
 * @author Ben Cole
 */
public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;

        if (LoginManager.getIsLoggedIn(this)) {
            activityIntent = new Intent(this, MainActivity.class);
            activityIntent.putExtra("user", LoginManager.getLoggedInUser(this));
        } else {
            activityIntent = new Intent(this, LoginActivity.class);
        }

        startActivity(activityIntent);
        finish();
    }


}
