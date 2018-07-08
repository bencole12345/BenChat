package pw.bencole.benchat.models;

import java.util.Date;

/**
 * Encapsulates a message either sent to or from the user.
 *
 * @author Ben Cole
 */
public class Message {

    private String mContent;
    private User mSender;
    private long mTimestamp;
    private User mRecipient;

    public Message(String content, User sender, User recipient, long timestamp) {
        mContent = content;
        mSender = sender;
        mRecipient = recipient;
        mTimestamp = timestamp;
    }

    public String getContent() {
        return mContent;
    }

    public User getSender() {
        return mSender;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
}
