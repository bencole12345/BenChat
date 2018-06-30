package pw.bencole.benchat.models;

import java.util.ArrayList;

/**
 * Encapsulates a sequence of messages exchanged between two people. One of these people is assumed
 * to be the user of the app: the other is a User object passed to this class via its constructor.
 *
 * @author Ben Cole
 */
public class Conversation {

    private User mOtherPerson;
    private ArrayList<Message> mMessages;

    public Conversation(User otherPerson, ArrayList<Message> messages) {
        mOtherPerson = otherPerson;
        mMessages = messages;
    }
}
