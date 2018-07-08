package pw.bencole.benchat.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.models.User;

public class NetworkHelper {

    public static ArrayList<Message> getAllMessagesBetween(LoggedInUser thisUser, User otherUser) {

        // TODO: Actually load messages from the server!

        ArrayList<Message> messages = new ArrayList<>();

        messages.add(new Message(
                "Hello there, " + thisUser.getUsername(),
                otherUser, thisUser, 0));
        messages.add(new Message(
                "How are you?", otherUser, thisUser, 1)
        );
        messages.add(new Message(
                "Good thanks!", thisUser, otherUser, 2)
        );
        messages.add(new Message(
                "We should add more messages so that the chat is a bit longer.", otherUser, thisUser, 3
        ));
        messages.add(new Message("That is a brilliant idea!", thisUser, otherUser, 4));
        messages.add(new Message("It sure is!", otherUser, thisUser, 5));

        return messages;
    }

}
