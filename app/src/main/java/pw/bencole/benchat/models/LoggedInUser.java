package pw.bencole.benchat.models;


/**
 * Adds a password to a User.
 *
 * This class should be used to represent the user that is actually using the app. The password
 * needs to be known in order to send requests to the API.
 *
 * @author Ben Cole
 */
public class LoggedInUser extends User {

    private String mPassword;

    public LoggedInUser(String username, String password, String userId) {
        super(username, userId);
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

}
