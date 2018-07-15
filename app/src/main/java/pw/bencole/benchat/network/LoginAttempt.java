package pw.bencole.benchat.network;

import pw.bencole.benchat.models.LoggedInUser;

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