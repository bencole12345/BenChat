package pw.bencole.benchat.models;

import java.util.HashSet;

/**
 * Contains a some information about a conversation: its ID, its name and its most recent message.
 *
 * This class is used by ConversationPreviewFragment to represent the preview for one conversation.
 */
public class ConversationPreview {

    private String mId;
    private Message mMostRecentMessage;
    private String mConversationName;

    public ConversationPreview(String conversationID, Message mostRecentMessage, String conversationName) {
        mId = conversationID;
        mMostRecentMessage = mostRecentMessage;
        mConversationName = conversationName;
    }

    /**
     * Returns the ID of this conversation.
     *
     * @return The ID of this conversation
     */
    public String getId() {
        return mId;
    }

    /**
     * Returns the name assigned to this conversation.
     *
     * @return The name of the conversation
     */
    public String getConversationName() {
        return mConversationName;
    }

    /**
     * Returns a preview of the most recent message, formatted into a String containing the
     * username of the person that send the message.
     *
     * @return A formatted String showing a preview of the most recent message
     */
    public String getMessagePreview() {
        if (mMostRecentMessage == null) {
            return "No messages sent!";
        } else {
            return mMostRecentMessage.getSender().getUsername()
                    + ": "
                    + mMostRecentMessage.getContent();
        }
    }

}
