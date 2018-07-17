package pw.bencole.benchat.models;


/**
 * Encapsulates a message either sent to or from the user.
 *
 * @author Ben Cole
 */
public class Message {

    private String mContent;
    private User mSender;
    private long mTimestamp;

    public Message(String content, User sender, long timestamp) {
        mContent = content;
        mSender = sender;
        mTimestamp = timestamp;
    }

    public Message(String content, User sender) {
        mContent = content;
        mSender = sender;
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
