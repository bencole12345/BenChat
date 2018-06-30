package pw.bencole.benchat.models;

import java.util.Date;

/**
 * @author Ben Cole
 */

public class Message {

    private String mContent;
    private User mSender;
    private Date mTimestamp;

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

    public Date getTimestamp() {
        return mTimestamp;
    }
}
