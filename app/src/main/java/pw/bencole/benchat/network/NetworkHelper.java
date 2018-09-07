package pw.bencole.benchat.network;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Conversation;
import pw.bencole.benchat.models.FriendRequest;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.models.User;

/**
 * Handles all communication with the server.
 *
 * @author Ben Cole
 */
public class NetworkHelper {

    /**
     * JSON type to use in headers when sending JSON over HTTP
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Sends an HTTP POST request containing the passed JSON data to a URL.
     *
     * @param url The URL to which the data should be sent
     * @param json The JSON data to send
     * @return The response from the server
     */
    private static Response postJson(String url, JSONObject json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    /**
     * Creates a JSONObject containing the username, password and id of the passed LoggedInUser.
     *
     * @param user The currently logged in user
     * @return The user's username, password and id formatted as a JSONObject
     */
    private static JSONObject getUserJson(LoggedInUser user) {
        JSONObject data = new JSONObject();
        try {
            if (user.getUsername() != null)
                data.put("username", user.getUsername());
            if (user.getPassword() != null)
                data.put("password", user.getPassword());
            if (user.getId() != null)
                data.put("_id", user.getId());
        } catch (JSONException e) {
            data = null;
        }
        return data;
    }

    /**
     * Attempts to log in to the API. The result, successful or otherwise, is encapsulated in a
     * LoginAttempt object.
     *
     * @param username The username to log in
     * @param password The password of that user
     * @return A LoginAttempt object containing the result of the attempted login, including a
     *         LoggedInUser object if the login was successful
     */
    public static LoginAttempt login(String username, String password, Context context) {
        JSONObject data = getUserJson(new LoggedInUser(username, password, null));
        LoginAttempt loginAttempt = null;
        try {
            String loginURL = context.getResources().getString(R.string.login_url);
            Response response = postJson(loginURL, data);
            ResponseBody body = response.body();
            if (response.code() == 200) {
                JSONObject result = new JSONObject(body.string());
                String id = result.getString("_id");
                LoggedInUser user = new LoggedInUser(username, password, id);
                loginAttempt = new LoginAttempt(true, user, FailureReason.NONE);
            } else if (response.code() == 401) {
                loginAttempt = new LoginAttempt(false, null, FailureReason.INVALID_CREDENTIALS);
            } else {
                loginAttempt = new LoginAttempt(false, null, FailureReason.NETWORK_ERROR);
            }
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
            loginAttempt = new LoginAttempt(false, null, FailureReason.NETWORK_ERROR);
        }
        return loginAttempt;
    }

    /**
     * Attempts to register a new user with the backend API and log them in. The result, successful
     * or otherwise, is contained in a LoginAttempt object.
     *
     * @param username The username of the new user
     * @param password The password of the new user
     * @return A LoginAttempt containing a LoggedInUser if the signup was successful, or a
     *         FailureReason otherwise
     */
    public static LoginAttempt signup(String username, String password, Context context) {
        JSONObject data = getUserJson(new LoggedInUser(username, password, null));
        LoginAttempt loginAttempt = null;
        try {
            String signupURL = context.getResources().getString(R.string.signup_url);
            Response response = postJson(signupURL, data);
            ResponseBody body = response.body();
            if (response.code() == 201) {
                JSONObject result = new JSONObject(body.string());
                String id = result.getString("_id");
                LoggedInUser user = new LoggedInUser(username, password, id);
                loginAttempt = new LoginAttempt(true, user, FailureReason.NONE);
            } else if (response.code() == 422) {
                loginAttempt = new LoginAttempt(false, null, FailureReason.USERNAME_TAKEN);
            }
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return loginAttempt;
    }

    /**
     * Returns a list of all conversations involving the passed LoggedInUser. These conversations
     * will not be populated with messages.
     *
     * @param user The user whose conversations are to be loaded
     * @param context The Context from which the method is called
     * @return An ArrayList of all conversations in which the user is a participant
     */
    public static ArrayList<Conversation> getAllConversations(LoggedInUser user, Context context) {
        JSONObject data = getUserJson(user);
        ArrayList<Conversation> conversations = new ArrayList<>();
        try {
            String getConversationsURL = context.getResources().getString(R.string.get_conversations_url);
            Response response = postJson(getConversationsURL, data);
            ResponseBody body = response.body();
            if (response.code() == 200) {
                JSONArray result = new JSONArray(body.string());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject json = result.getJSONObject(i);
                    JSONArray participants = json.getJSONArray("participants");
                    List<User> participantsList = new LinkedList<>();
                    for (int j = 0; j < participants.length(); j++) {
                        JSONObject participant = participants.getJSONObject(j);
                        String username = participant.getString("username");
                        String userId = participant.getString("_id");
                        participantsList.add(new User(username, userId));
                    }

                    // TODO: Modify the API so you don't have to do this
                    ArrayList<Message> messages = getAllMessagesInConversation(user, json.getString("_id"), context);
                    Message mostRecentMessage = null;
                    if (messages.size() > 0) {
                        mostRecentMessage = messages.get(0);
                    }

                    conversations.add(new Conversation(json.getString("_id"), participantsList, mostRecentMessage));
                }
            }
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return conversations;
    }

    /**
     * Retrieves all messages in the requested conversation.
     *
     * @param user The LoggedInUser requesting the messages
     * @param conversationId The ID of the conversation
     * @param context The Context from which the method is called
     * @return A list of all messages in this conversation
     */
    public static ArrayList<Message> getAllMessagesInConversation(LoggedInUser user, String conversationId, Context context) {
        JSONObject data = getUserJson(user);
        ArrayList<Message> messages = new ArrayList<>();
        try {
            data.put("conversationId", conversationId);
            String url = context.getResources().getString(R.string.get_messages_url);
            Response response = postJson(url, data);
            ResponseBody body = response.body();
            JSONArray messagesJson = new JSONArray(body.string());
            for (int i = 0; i < messagesJson.length(); i++) {
                JSONObject message = messagesJson.getJSONObject(i);
                JSONObject author = message.getJSONObject("author");
                String authorId = author.getString("_id");
                String authorName = author.getString("username");
                String messageContent = message.getString("content");
                User sender = new User(authorName, authorId);
                // TODO: Load the timestamp from the data and replace "i" below
                String timestamp = message.getString("createdAt");
                messages.add(new Message(messageContent, sender, timestamp));
            }
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Attempts to create a new conversation featuring the logged in user, as well as all users
     * in otherUsers.
     *
     * @param otherUsers The Set of other users in this conversation
     * @param context The context from which the method is called
     * @return A ConversationCreationAttempt containing the result of the operation
     */
    public static ConversationCreationAttempt createConversation(LoggedInUser user, Set<User> otherUsers, Context context) {
        JSONObject data = getUserJson(user);
        try {
            LinkedList<String> otherUserIDs = new LinkedList<>();
            for (User otherUser : otherUsers) {
                otherUserIDs.add(otherUser.getId());
            }
            data.put("otherUsers", new JSONArray(otherUserIDs));
            String url = context.getResources().getString(R.string.create_conversation_url);
            Response response = postJson(url, data);
            ResponseBody body = response.body();
            if (response.code() == 201) {
                JSONObject bodyParsed = new JSONObject(body.string());
                return new ConversationCreationAttempt(true, bodyParsed.getString("_id"), FailureReason.NONE);
            } else if (response.code() == 422) {
                return new ConversationCreationAttempt(false, null, FailureReason.CONVERSATION_ALREADY_EXISTS);
            } else {
                return new ConversationCreationAttempt(false, null, FailureReason.NETWORK_ERROR);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return new ConversationCreationAttempt(false, null, FailureReason.NETWORK_ERROR);
        }
    }

    /**
     * Attempts to send a message to a conversation.
     *
     * @param user The LoggedInUser sending the message
     * @param message The Message to be the sent
     * @param conversationID The ID of the target conversation
     * @param context The Context from which the method is called
     * @return true if the operation was successful; false otherwise
     */
    public static boolean sendMessage(LoggedInUser user, Message message, String conversationID, Context context) {
        JSONObject data = getUserJson(user);
        try {
            data.put("messageContent", message.getContent());
            data.put("conversationId", conversationID);
            String url = context.getResources().getString(R.string.send_message_url);
            Response response = postJson(url, data);
            return (response.code() == 201);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds all users that are friends with the currently logged in user.
     *
     * @param user The currently logged in user
     * @param context The context from which the method is called
     * @return A list of all Users that are friends with the logged in user
     */
    public static ArrayList<User> getAllFriends(LoggedInUser user, Context context) {
        JSONObject data = getUserJson(user);
        try {
            String url = context.getResources().getString(R.string.get_all_friends_url);
            Response response = postJson(url, data);
            ResponseBody body = response.body();
            if (response.code() == 200) {
                JSONArray array = new JSONArray(body.string());
                ArrayList<User> users = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString("_id");
                    String username = object.getString("username");
                    users.add(new User(username, id));
                }
                return users;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds everyone that has either sent a friend request to the user, or received one from them,
     * that has not yet been accepted or declined.
     *
     * A map is used to encode the result. There will be two keys: "sent" and "received". Each of
     * these points to a list of FriendRequest objects.
     *
     * @param user The currently logged in user
     * @param context The context from which the method is called
     * @return A map to encode the list of sent friend requests and the list of received friend
     *         requests
     */
    public static Map<String, List<FriendRequest>> getAllRequests(LoggedInUser user, Context context) {
        JSONObject data = getUserJson(user);
        Map<String, List<FriendRequest>> result = new HashMap<>();
        result.put("sent", new ArrayList<FriendRequest>());
        result.put("received", new ArrayList<FriendRequest>());
        try {
            String url = context.getResources().getString(R.string.get_all_friend_requests_url);
            Response response = postJson(url, data);
            ResponseBody body = response.body();
            if (response.code() == 200) {

                JSONObject json = new JSONObject(body.string());
                JSONArray sentArray = json.getJSONArray("sent");
                JSONArray receivedArray = json.getJSONArray("received");

                // Traverse sent requests
                for (int i = 0; i < sentArray.length(); i++) {
                    JSONObject sentRequest = sentArray.getJSONObject(i);
                    String requestId = sentRequest.getString("_id");
                    JSONObject personSentTo = sentRequest.getJSONObject("sentTo");
                    User userSentTo = new User(
                            personSentTo.getString("username"),
                            personSentTo.getString("_id"));
                    result.get("sent").add(new FriendRequest(userSentTo, false, requestId));
                }

                // Traverse received requests
                for (int i = 0; i < receivedArray.length(); i++) {
                    JSONObject receivedRequest = receivedArray.getJSONObject(i);
                    String requestId = receivedRequest.getString("_id");
                    JSONObject personSentFrom = receivedRequest.getJSONObject("sentFrom");
                    User userSentFrom = new User(
                            personSentFrom.getString("username"),
                            personSentFrom.getString("_id")
                    );
                    result.get("received").add(new FriendRequest(userSentFrom, true, requestId));
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    /**
     * Confirms a friend request.
     *
     * @param user The logged in user
     * @param request The request to respond to
     * @param context The Context from which the method is called
     * @return The User that was created
     */
    public static User confirmFriendRequest(LoggedInUser user, FriendRequest request, Context context) {
        JSONObject data = getUserJson(user);
        String url = context.getResources().getString(R.string.respond_to_friend_request_url);
        try {
            data.put("friendRequestId", request.getRequestId());
            data.put("response", "accept");
            Response response = postJson(url, data);
            if (response.code() == 200) {
                ResponseBody body = response.body();
                JSONObject bodyJson = new JSONObject(body.string());
                JSONObject otherUserJson = bodyJson.getJSONObject("otherUser");
                return new User(otherUserJson.getString("username"), otherUserJson.getString("_id"));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Declines a friend request.
     *
     * @param user The logged in user
     * @param request The request to respond to
     * @param context The Context from which the method is called
     * @return true if the response was successful; false otherwise
     */
    public static boolean declineFriendRequest(LoggedInUser user, FriendRequest request, Context context) {
        JSONObject data = getUserJson(user);
        String url = context.getResources().getString(R.string.respond_to_friend_request_url);
        try {
            data.put("friendRequestId", request.getRequestId());
            data.put("response", "decline");
            Response response = postJson(url, data);
            return (response.code() == 200);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cancels a sent friend request.
     *
     * This will fail if the user did not send the request.
     *
     * @param user The logged in user
     * @param request The sent request to be cancelled
     * @param context The context from which the method is called
     * @return true if the operation was successful; false otherwise
     */
    public static boolean cancelSentFriendRequest(LoggedInUser user, FriendRequest request, Context context) {
        JSONObject data = getUserJson(user);
        String url = context.getResources().getString(R.string.cancel_friend_request_url);
        try {
            data.put("friendRequestId", request.getRequestId());
            Response response = postJson(url, data);
            return (response.code() == 200);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Adds another user as a friend.
     *
     * The method uses a FriendCreationAttempt to encode the result.
     *
     * NONE:                           The friend request was sent successfully.
     * ALREADY_FRIENDS:                The logged in user is already friends with the user of that
     *                                 username.
     * FRIEND_REQUEST_ALREADY_EXISTS:  A friend request has already been sent between the logged
     *                                 in user and the target user.
     * FRIENDS_WITH_SELF:              The logged in user attempted to send themself a friend
     *                                 request.
     * USER_NOT_FOUND                  The requested user could not be found.
     *
     * @param user The logged in user
     * @param username The username of the target user
     * @param context The context from which the method is called
     * @return A FailureReason encoding the result of the operation
     */
    public static FailureReason addFriend(LoggedInUser user, String username, Context context) {
        if (user.getUsername().equals(username)) {
            return FailureReason.FRIENDS_WITH_SELF;
        }
        JSONObject data = getUserJson(user);
        String url = context.getResources().getString(R.string.add_friend_url);
        try {
            data.put("otherUsername", username);
            Response response = postJson(url, data);
            if (response.code() == 201) {
                return FailureReason.NONE;
            }
            ResponseBody body = response.body();
            JSONObject responseJson = new JSONObject(body.string());
            if (response.code() == 422) {
                if (responseJson.getBoolean("alreadyFriends")) {
                    return FailureReason.ALREADY_FRIENDS;
                } else {
                    return FailureReason.FRIEND_REQUEST_ALREADY_EXISTS;
                }
            }
            if (response.code() == 404) {
                return FailureReason.USER_NOT_FOUND;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return FailureReason.NETWORK_ERROR;
    }

}
