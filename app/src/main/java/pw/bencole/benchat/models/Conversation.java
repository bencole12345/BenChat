package pw.bencole.benchat.models;


import java.util.List;

/**
 * Contains a some information about a conversation: its ID, its name and its most recent message.
 *
 * This class is used by ConversationPreviewFragment to represent the preview for one conversation.
 *
 * @author Ben Cole
 */
public class Conversation {

    private String mId;
    private List<User> mParticipants;
    private Message mMostRecentMessage;

    public Conversation(String conversationID, List<User> participants, Message mostRecentMessage) {
        mId = conversationID;
        mParticipants = participants;
        mMostRecentMessage = mostRecentMessage;
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
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < mParticipants.size(); i++) {
            buffer.append(mParticipants.get(i).getUsername());
            if (i != mParticipants.size() - 1) buffer.append(", ");
        }
        return buffer.toString();
    }

    /**
     * Returns the list of participants in this conversation.
     *
     * @return the list of participants in this conversation
     */
    public List<User> getParticipants() {
        return mParticipants;
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
