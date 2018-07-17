package pw.bencole.benchat.network;

import pw.bencole.benchat.models.LoggedInUser;


/**
 * Encapsulates the result of an attempted login.
 *
 * This is used to communicate whether a login was successful. If it was, the created LoggedInUser
 * object should be stored: if not, a FailureReason should be supplied to explain why the login
 * (or signup) was not successful.
 *
 * @author Ben Cole
 */
public class LoginAttempt {

    private boolean mSuccess;
    private LoggedInUser mUser;
    private FailureReason mFailureReason;

    public LoginAttempt(boolean success, LoggedInUser user, FailureReason failureReason) {
        mSuccess = success;
        mUser = user;
        mFailureReason = failureReason;
    }

    public boolean getWasSuccessful() {
        return mSuccess;
    }

    public LoggedInUser getUser() {
        return mUser;
    }

    public FailureReason getFailureReason() {
        return mFailureReason;
    }

}