package pw.bencole.benchat.util;

import android.content.Context;
import android.content.SharedPreferences;

import pw.bencole.benchat.models.LoggedInUser;


/**
 * A Singleton used to access the currently logged in user, as well as save and load this to disk
 * for consistency between sessions.
 *
 * @author Ben Cole
 */
public class LoginManager {

    /**
     * Keys to be used to store relevant properties
     */
    public static final String LOGIN_PREFERENCES = "pw.bencole.benchat.LOGIN_PREFERENCES";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "userId";
    public static final String IS_LOGGED_IN = "is_logged_in";

    /**
     * The single instance of this class.
     */
    private static LoginManager mInstance;

    /**
     * The currently logged in user
     */
    private LoggedInUser mLoggedInUser;

    /**
     * The SharedPreferences file used to store login details persistently
     */
    private SharedPreferences mSharedPreferences;


    /**
     * Private constructor, as per the singleton design pattern.
     */
    private LoginManager() {}

    /**
     * Used to access the instance of this class.
     *
     * @return The single instance of this LoginManager.
     */
    public static LoginManager getInstance() {
        if (mInstance == null) {
            mInstance = new LoginManager();
        }
        return mInstance;
    }

    /**
     * Sets up the singleton instance.
     *
     * @param context A Context object, used to access the SharedPreferences file
     */
    public void initialise(Context context) {
        mSharedPreferences = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        if (savedLoginExists()) {
            mLoggedInUser = getSavedLoggedInUser();
        }
    }

    /**
     * Checks whether the user has previously logged in when they last used the app, in which case
     * they should not be taken to the login screen again.
     *
     * @return true if there are already username and password details saved from last time; false
     *         otherwise
     */
    public boolean savedLoginExists() {
        return mSharedPreferences.getBoolean(IS_LOGGED_IN, false)
                && mSharedPreferences.contains(USERNAME)
                && mSharedPreferences.contains(PASSWORD)
                && mSharedPreferences.contains(USER_ID);
    }

    /**
     * Returns the user that is currently logged in.
     *
     * If they have already been retrieved from the persistent store, then the previously
     * constructed LoggedInUser object will be used. If not, the user will first be loaded from
     * the store.
     *
     * If no user has been logged in since the last logout, then null will be returned.
     *
     * @return The LoggedInUser that signed in when the app was last used, or null if they don't
     *         exist
     */
    public LoggedInUser getLoggedInUser() {
        if (mLoggedInUser == null) {
            mLoggedInUser = getSavedLoggedInUser();
        }
        return mLoggedInUser;
    }

    /**
     * Returns the LoggedInUser that was last written to the persistent store, or null if they
     * don't exist.
     */
    private LoggedInUser getSavedLoggedInUser() {
        if (savedLoginExists()) {
            String username = mSharedPreferences.getString(USERNAME, null);
            String password = mSharedPreferences.getString(PASSWORD, null);
            String userId = mSharedPreferences.getString(USER_ID, null);
            return new LoggedInUser(username, password, userId);
        } else {
            return null;
        }
    }

    /**
     * Updates the persistently stored user.
     *
     * @param user The new user to log in
     */
    public void setLoggedInUser(LoggedInUser user) {
        mLoggedInUser = user;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(USERNAME, user.getUsername());
        editor.putString(PASSWORD, user.getPassword());
        editor.putString(USER_ID, user.getId());
        editor.apply();
    }

    /**
     * Deletes the logged in user from the permanent store.
     */
    public void logout() {
        mLoggedInUser = null;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(USERNAME, null);
        editor.putString(PASSWORD, null);
        editor.putString(USER_ID, null);
        editor.apply();
    }
}
