package pw.bencole.benchat.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.User;
import pw.bencole.benchat.network.ConversationCreationAttempt;
import pw.bencole.benchat.network.NetworkHelper;


/**
 * Displays a form for creating a new conversation.
 *
 * @author Ben Cole
 */
public class NewConversationActivity extends AppCompatActivity {

    private ListView mFriendsList;
    private Button mCreateConversationButton;

    private ArrayList<User> mFriends;
    private FriendListAdapter mAdapter;

    private HashSet<Integer> mSelectedParticipants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        // Display a back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSelectedParticipants = new HashSet<>();

        mFriendsList = findViewById(R.id.friendsList);

        mFriends = new ArrayList<>();
        mAdapter = new FriendListAdapter(this, R.layout.listelement_checkable_friend, mFriends);
        mFriendsList.setAdapter(mAdapter);
        mFriendsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mCreateConversationButton = findViewById(R.id.createConversationButton);
        mCreateConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateConversation();
            }
        });

        new FriendListDownloadTask(this).execute();

        mFriendsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mSelectedParticipants.contains(position)) {
                    mSelectedParticipants.remove(position);
                } else {
                    mSelectedParticipants.add(position);
                }
            }
        });
    }

    /**
     * Ends the Activity when the back button in the action bar is clicked.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Attempts to create a new conversation and forwards the user to a ConversationActivity if
     * successful.
     */
    private void attemptCreateConversation() {

        // Check that at least one other user has been selected
        if (mSelectedParticipants.size() == 0) {
            Toast.makeText(this, "You must select at least one other user.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Check if conversation with this set of people already exists (can be handled server-side)

        // All validation passed, so send the details to the server.
        new NewConversationTask().execute();
    }

    /**
     * Returns a set of all users selected to be in this conversation.
     *
     * @return a HashSet of Users that will be members of this conversation
     */
    private HashSet<User> getConversationParticipants() {
        HashSet<User> users = new HashSet<>();
        for (int i : mSelectedParticipants) {
            users.add(mFriends.get(i));
        }
        return users;
    }

    /**
     * Handles the result from the server of an attempt to create a conversation.
     *
     * If the creation was successful then the user will be transferred to a ConversationActivity
     * for the new conversation. If there was an error, then an appropriate error message will
     * be displayed.
     *
     * @param attempt The ConversationCreationAttempt containing the result
     */
    private void handleConversationCreationAttempt(ConversationCreationAttempt attempt) {
        if (attempt.getWasSuccessful()) {
            displayConversation(attempt.getConversationId());
        } else {
            switch (attempt.getFailureReason()) {
                case NETWORK_ERROR:
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    return;
                case CONVERSATION_ALREADY_EXISTS:
                    final String existingConversationId = attempt.getConversationId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Conversation already exists")
                           .setMessage("View the existing conversation with these users?")
                           .setPositiveButton("View", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   displayConversation(existingConversationId);
                               }
                           })
                           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                               }
                           })
                           .show();
            }
        }
    }

    /**
     * Initialises a ConversationActivity to display the conversation with the passed ID.
     *
     * @param conversationId The ID of the conversation to view
     */
    private void displayConversation(String conversationId) {
        Intent conversationActivityIntent = new Intent(this, ConversationActivity.class);
        conversationActivityIntent.putExtra(ConversationActivity.CONVERSATION_ID, conversationId);
        startActivity(conversationActivityIntent);
        finish();
    }

    /**
     * Adapts an array of User objects into the ListView for the list of friends.
     */
    private static class FriendListAdapter extends ArrayAdapter<User> {

        private static class ViewHolder {
            public CheckedTextView usernameCheckedText;
        }

        public FriendListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            User friend = getItem(position);
            ViewHolder viewHolder;

            final View result;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                result = inflater.inflate(R.layout.listelement_checkable_friend, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.usernameCheckedText = result.findViewById(R.id.usernameCheckedText);
                result.setTag(viewHolder);
            } else {
                result = convertView;
                viewHolder = (ViewHolder) result.getTag();
            }

            viewHolder.usernameCheckedText.setText(friend.getUsername());
            return result;
        }
    }

    /**
     * Handles downloading the friends list to be displayed.
     */
    private class FriendListDownloadTask extends AsyncTask<Void, Void, ArrayList<User>> {

        private Context mContext;

        public FriendListDownloadTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            return NetworkHelper.getAllFriends(mContext);
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            mFriends.clear();
            mFriends.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handles sending the required information about the new conversation to the server.
     */
    private class NewConversationTask extends AsyncTask<Void, Void, ConversationCreationAttempt> {

        @Override
        protected ConversationCreationAttempt doInBackground(Void... voids) {
            return NetworkHelper.createConversation(getConversationParticipants(), getApplicationContext());
        }

        @Override
        protected void onPostExecute(ConversationCreationAttempt attempt) {
            handleConversationCreationAttempt(attempt);
        }
    }
}
