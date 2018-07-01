package pw.bencole.benchat.models;

import pw.bencole.benchat.network.Session;

/**
 * Pairs a User object with a Session object.
 *
 * @author Ben Cole
 */
public class LoggedInUser extends User {

    private Session mSession;

    public LoggedInUser(String username, Session session) {
        super(username);
        mSession = session;
    }

    /**
     * Returns the Session object associated with this instance.
     *
     * @return the Session object associated with this instance
     */
    public Session getSession() {
        return mSession;
    }
}
