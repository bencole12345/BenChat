package pw.bencole.benchat.models;

import pw.bencole.benchat.network.Session;

/**
 * Pairs a User object with a Session object.
 *
 * @author Ben Cole
 */
public class LoggedInUser extends User {

//    private Session mSession;
    private String mPassword;

//    public LoggedInUser(String username, Session session) {
//        super(username);
//        mSession = session;
//    }

    public LoggedInUser(String username, String password) {
        super(username);
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    /**
     * Returns the Session object associated with this instance.
     *
     * @return the Session object associated with this instance
     */
//    public Session getSession() {
//        return mSession;
//    }
}
