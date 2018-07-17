package pw.bencole.benchat.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.models.Message;
import pw.bencole.benchat.models.User;

/**
 * Encapsulates all communication with the backend API.
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
