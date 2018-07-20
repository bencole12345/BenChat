package pw.bencole.benchat.models;


/**
 * Encapsulates a message either sent to or from the user.
 *
 * @author Ben Cole
 */
public class Message {

    private String mContent;
    private User mSender;
    private String mTimestamp;

    public Message(String content, User sender, String timestamp) {
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

    public String getTimestamp() {
        return mTimestamp;
    }
}
