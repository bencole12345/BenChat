package pw.bencole.benchat.network;


/**
 * Simplifies producing a data structure to contain the result of some attempted resource creation
 * over the internet with the API. To use, create a subclass and specify the type `T`. For example,
 * the `LoginAttempt` class extends `ResourceCreationAttempt<LoggedInUser>`.
 *
 * @param <T> The type of the resource being created
 *
 * @author Ben Cole
 */
public abstract class ResourceCreationAttempt<T> {

    private boolean mSuccess;
    private FailureReason mReason;
    private T mCreatedObject;

    public ResourceCreationAttempt(boolean success, T createdObject, FailureReason failureReason) {
        mSuccess = success;
        mReason = failureReason;
        mCreatedObject = createdObject;
    }

    public boolean getWasSuccessful() {
        return mSuccess;
    }

    public FailureReason getFailureReason() {
        return mReason;
    }

    public T getCreatedObject() {
        return mCreatedObject;
    }

}
