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
public class LoginAttempt extends ResourceCreationAttempt<LoggedInUser> {
    public LoginAttempt(boolean success, LoggedInUser loggedInUser, FailureReason failureReason) {
        super(success, loggedInUser, failureReason);
    }
}