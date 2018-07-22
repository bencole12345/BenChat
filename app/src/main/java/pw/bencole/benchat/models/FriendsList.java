package pw.bencole.benchat.models;


import java.util.ArrayList;

/**
 * Contains a list of pending and confirmed friends.
 */
public class FriendsList {

    /**
     * A list for each type of friend
     */
    private ArrayList<User> mConfirmedFriends;
    private ArrayList<User> mReceivedRequests;
    private ArrayList<User> mSentRequests;

    public FriendsList(ArrayList<User> confirmedFriends, ArrayList<User> receivedRequests,
                       ArrayList<User> sentRequests) {
        mConfirmedFriends = confirmedFriends;
        mReceivedRequests = receivedRequests;
        mSentRequests = sentRequests;
    }

}
