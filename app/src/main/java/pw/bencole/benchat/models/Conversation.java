package pw.bencole.benchat.models;

import java.util.ArrayList;

/**
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
