package pw.bencole.benchat.models;


import java.util.LinkedList;
import java.util.List;

import pw.bencole.benchat.util.LoginManager;

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
        return getConversationName(mParticipants);
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

    public static String getConversationName(List<User> participants) {
        LoggedInUser user = LoginManager.getInstance().getLoggedInUser();
        StringBuilder builder = new StringBuilder("");
        LinkedList<User> usersToInclude = new LinkedList<>();
        for (User participant : participants) {
            if (!participant.getUsername().equals(user.getUsername())) {
                usersToInclude.add(participant);
            }
        }
        for (int i = 0; i < usersToInclude.size(); i++) {
            builder.append(usersToInclude.get(i).getUsername());
            if (i != usersToInclude.size() - 1 ) builder.append(", ");
        }
        return builder.toString();
    }

}
