package pw.bencole.benchat.models;

public class FriendRequest {

    private User mUser;
    private boolean mWasReceived;  // as opposed to sent
    private String mRequestId;

    public FriendRequest(User user, boolean wasReceived, String requestId) {
        mUser = user;
        mWasReceived = wasReceived;
        mRequestId = requestId;
    }

    public User getUser() {
        return mUser;
    }

    public boolean getWasReceived() {
        return mWasReceived;
    }

    public String getRequestId() {
        return mRequestId;
    }
}
