package pw.bencole.benchat.models;

import java.io.Serializable;


/**
 * Encapsulates a user of the app.
 *
 * @author Ben Cole
 */
public class User implements Serializable {

    private String mUsername;

    public User(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }
}
