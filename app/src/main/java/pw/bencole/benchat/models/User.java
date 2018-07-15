package pw.bencole.benchat.models;

import java.io.Serializable;


/**
 * Encapsulates a user of the app.
 *
 * @author Ben Cole
 */
public class User implements Serializable {

    private String mUsername;
    private String mUserId;

    public User(String username, String userId) {
        mUsername = username;
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getId() {
        return mUserId;
    }
}
