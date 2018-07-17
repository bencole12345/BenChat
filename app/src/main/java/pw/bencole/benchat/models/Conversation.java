package pw.bencole.benchat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a sequence of messages exchanged between two people. One of these people is assumed
 * to be the user of the app: the other is a User object passed to this class via its constructor.
 *
 * @author Ben Cole
 */
public class Conversation {

    private User mOtherPerson;
    private ArrayList<Message> mMessages;

    /**
     * Thrown to indicate that no messages have been sent yet in this conversation.
     */
    public static class NoMessagesException extends Exception {}

    /**
     * Creates a Conversation, using a User to represent the other person and an ArrayList of
     * Message objects to contain all messages exchanged.
     *
     * @param otherPerson a User showing the other person with whom this conversation is
     * @param messages an ArrayList of all messages in this conversation
     */
    public Conversation(User otherPerson, ArrayList<Message> messages) {
        mOtherPerson = otherPerson;
        mMessages = messages;
    }

    /**
     * Creates a conversation without any messages supplied. Instead, an empty array will be
     * created.
     *
     * @param otherPerson The other User involved in this conversation
     */
    public Conversation(User otherPerson) {
        mOtherPerson = otherPerson;
        mMessages = new ArrayList<>();
    }

    /**
     * Returns the other User with whom this conversation is.
     * @return the other User with whom this conversation is
     */
    public User getOtherPerson() {
        return mOtherPerson;
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    /**
     * Returns the most recent message in this conversation, or throws a NoMessagesException if no
     * messages have been exchanged yet.
     *
     * @return The most recent message in this conversation
     * @throws NoMessagesException if no messages have been exchanged yet
     */
    public Message getMostRecentMessage() throws NoMessagesException {
        if (mMessages.size() > 0) {
            return mMessages.get(0);
        } else {
            throw new NoMessagesException();
        }
    }

    /**
     * Returns a String preview of the most recent message, or the string "No messages sent!" if
     * there is no such message.
     *
     * @return A preview of the most recent message
     */
    public String getMostRecentMessagePreview() {
        String preview;
        try {
            Message message = getMostRecentMessage();
            preview = message.getSender().getUsername()
                    + ": "
                    + message.getContent();
        } catch (NoMessagesException e) {
            preview = "No messages sent!";
        }
        return preview;
    }
}
