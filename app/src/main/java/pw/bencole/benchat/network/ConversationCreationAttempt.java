package pw.bencole.benchat.network;


/**
 * Contains the result of an attempt to create a new conversation.
 *
 * If the conversation was created successfully then its conversationId should be stored: if not,
 * then success should be false and a reason given using failureReason.
 *
 * @author Ben Cole
 */
public class ConversationCreationAttempt extends ResourceCreationAttempt<String> {

    public ConversationCreationAttempt(boolean success, String conversationId, FailureReason failureReason) {
        super(success, conversationId, failureReason);
    }

    /**
     * Returns the id of the conversation that was created.
     *
     * @return The id of the conversation that was created.
     */
    public String getConversationId() {
        return getCreatedObject();
    }
}
