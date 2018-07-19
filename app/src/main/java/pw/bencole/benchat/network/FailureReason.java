package pw.bencole.benchat.network;


/**
 * Describes why a network operation failed. The code can be read by a handler method and an
 * appropriate error message be displayed.
 *
 * @author Ben Cole
 */
public enum FailureReason {
    NONE,
    USERNAME_TAKEN,
    INVALID_CREDENTIALS,
    NETWORK_ERROR,
    CONVERSATION_ALREADY_EXISTS,
    USER_NOT_FOUND
}