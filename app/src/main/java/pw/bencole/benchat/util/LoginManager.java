package pw.bencole.benchat.util;

import android.content.Context;
import android.content.SharedPreferences;

import pw.bencole.benchat.models.LoggedInUser;


/**
 * Offers static methods for dealing with logging out and in, and storing user login details
 * persistently.
 *
 * @author Ben Cole
 */
public class LoginManager {

    public static final String LOGIN_PREFERENCES = "pw.bencole.benchat.LOGIN_PREFERENCES";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "userId";
    public static final String IS_LOGGED_IN = "is_logged_in";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Checks whether the user has previously logged in when they last used the app, in which case
     * they should not be taken to the login screen again.
     * @return true if there are already username and password details saved from last time; false
     *         otherwise
     */
    public static boolean getIsLoggedIn(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(IS_LOGGED_IN, false)
                && preferences.contains(USERNAME)
                && preferences.contains(PASSWORD)
                && preferences.contains(USER_ID);
    }

    /**
     * Retrieves the user that logged in when the app was last used. In the event that there are
     * no such details stored, null will be returned.
     * @param context The context from which this method is called
     * @return The LoggedInUser that signed in when the app was last used, or null if they don't
     *         exist
     */
    public static LoggedInUser getLoggedInUser(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        String username = preferences.getString(USERNAME, null);
        String password = preferences.getString(PASSWORD, null);
        String userId = preferences.getString(USER_ID, null);
        if (!getIsLoggedIn(context) || username == null || password == null || userId == null) {
            return null;
        } else {
            return new LoggedInUser(username, password, userId);
        }
    }

    /**
     * Updates the persistently stored user.
     * @param user The new user to log in
     * @param context The context from which the method is called
     */
    public static void setLoggedInUser(LoggedInUser user, Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(USERNAME, user.getUsername());
        editor.putString(PASSWORD, user.getPassword());
        editor.putString(USER_ID, user.getId());
        editor.apply();
    }

    /**
     * Deletes the logged in user from the permanent store.
     * @param context The context from which the method was called
     */
    public static void logout(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(USERNAME, null);
        editor.putString(PASSWORD, null);
        editor.putString(USER_ID, null);
        editor.apply();
    }
}
