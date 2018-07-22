package pw.bencole.benchat.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pw.bencole.benchat.R;
import pw.bencole.benchat.models.ConversationPreview;
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
     * Creates a JSONObject containing the passed username and password.
     *
     * @param username The username attribute
     * @param password The password attribute
     * @return A JSONObject with the username and password attributes set
     */
    private static JSONObject getUserJson(String username, String password) {
        JSONObject user = new JSONObject();
        try {
            user.put("username", username);
            user.put("password", password);
            return user;
        } catch (JSONException e) {
            return null;
        }
    }

    private static JSONObject getUserJson(LoggedInUser user) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", user.getUsername());
            data.put("password", user.getPassword());
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
        JSONObject data = getUserJson(username, password);
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
        JSONObject data = getUserJson(username, password);
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
    public static ArrayList<ConversationPreview> getAllConversations(LoggedInUser user, Context context) {
        JSONObject data = getUserJson(user);
        ArrayList<ConversationPreview> conversations = new ArrayList<>();
        try {
            String getConversationsURL = context.getResources().getString(R.string.get_conversations_url);
            Response response = postJson(getConversationsURL, data);
            ResponseBody body = response.body();
            if (response.code() == 200) {
                JSONArray result = new JSONArray(body.string());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject json = result.getJSONObject(i);
                    JSONArray participants = json.getJSONArray("participants");
                    HashSet<User> participantsSet = new HashSet<>();
                    String conversationName = "unnamed conversation";
                    for (int j = 0; j < participants.length(); j++) {
                        JSONObject participant = participants.getJSONObject(j);
                        String username = participant.getString("username");
                        String userId = participant.getString("_id");
                        participantsSet.add(new User(username, userId));
                        // TODO: Modify API to actually store a conversation name
                        if (!userId.equals(user.getId())) conversationName = username;
                    }

                    // TODO: Modify the API so you don't have to do this
                    ArrayList<Message> messages = getAllMessagesInConversation(user, json.getString("_id"), context);
                    Message mostRecentMessage = null;
                    if (messages.size() > 0) {
                        mostRecentMessage = messages.get(0);
                    }

                    conversations.add(new ConversationPreview(json.getString("_id"), mostRecentMessage, conversationName));
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
     * Attempts to create a new conversation with the other user, and uses a
     * ConversationCreationAttempt object to convey the result.
     *
     * @param user The logged in user
     * @param otherUsername The username of the other participant in this conversation
     * @param context The context from which the method is called
     * @return A ConversationCreatingAttempt containing the result of the operation
     */
    public static ConversationCreationAttempt createConversation(LoggedInUser user, String otherUsername, Context context) {
        JSONObject data = getUserJson(user);
        try {
            data.put("otherUsername", otherUsername);
            String url = context.getResources().getString(R.string.create_conversation_url);
            Response response = postJson(url, data);
            ResponseBody body = response.body();
            if (response.code() == 201) {
                JSONObject bodyParsed = new JSONObject(body.string());
                return new ConversationCreationAttempt(true, bodyParsed.getString("_id"), FailureReason.NONE);
            } else if (response.code() == 400) {
                return new ConversationCreationAttempt(false, null, FailureReason.USER_NOT_FOUND);
            } else if (response.code() == 422) {
                return new ConversationCreationAttempt(false, null, FailureReason.CONVERSATION_ALREADY_EXISTS);
            } else {
                return new ConversationCreationAttempt(false, null, FailureReason.NETWORK_ERROR);
            }
        } catch (IOException | NullPointerException | JSONException e) {
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

}
