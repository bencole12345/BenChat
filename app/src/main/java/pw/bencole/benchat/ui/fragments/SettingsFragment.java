package pw.bencole.benchat.ui.fragments;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import pw.bencole.benchat.R;


/**
 * Displays the app's settings screen.
 *
 * @author Ben Cole
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_layout);
    }

}
