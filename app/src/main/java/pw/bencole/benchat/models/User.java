package pw.bencole.benchat.models;

/**
 * Encapsulates a user of the app.
 *
 * @author Ben Cole
 */
public class User {

    private String mUsername;

    public User(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }
}
