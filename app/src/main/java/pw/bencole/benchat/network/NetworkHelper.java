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
     * Attempts to log in to the API. The result, successful or otherwise, is encapsulated in a
     * LoginAttempt object.
     *
     * @param username The username to log in
     * @param password The password of that user
     * @return A LoginAttempt object containing the result of the attempted login, including a
     *         LoggedInUser object if the login was successful
     */
    public static LoginAttempt login(String username, String password, Context context) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
            data.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoginAttempt loginAttempt = null;
        try {
            String loginURL = context.getResources().getString(R.string.login_url);
            Response response = postJson(loginURL, data);
//            Log.d("NetworkHelper", response.body().string());
            ResponseBody body = response.body();
            JSONObject result = new JSONObject(body.string());
            String id = result.getString("_id");
            LoggedInUser user = new LoggedInUser(username, password, id);
            loginAttempt = new LoginAttempt(true, user, FailureReason.NONE);
            // TODO: Check if password was wrong (this code assumes it was correct);
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
            loginAttempt = new LoginAttempt(false, null, FailureReason.NETWORK_ERROR);
        }
        return loginAttempt;
    }

    public static LoginAttempt signup(String username, String password) {
        return null;
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
