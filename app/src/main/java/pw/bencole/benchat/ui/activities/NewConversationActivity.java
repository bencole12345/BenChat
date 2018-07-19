package pw.bencole.benchat.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.network.ConversationCreationAttempt;
import pw.bencole.benchat.network.NetworkHelper;


/**
 * Displays a form for creating a new conversation.
 *
 * @author Ben Cole
 */
public class NewConversationActivity extends AppCompatActivity {

    private EditText mOtherUsernameField;
    private Button mCreateConversationButton;

    private LoggedInUser mLoggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        mLoggedInUser = (LoggedInUser) getIntent().getExtras().get("user");

        mOtherUsernameField = findViewById(R.id.otherUsernameField);
        mCreateConversationButton = findViewById(R.id.createConversationButton);
        mCreateConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConversation();
            }
        });
    }

    /**
     * Attempts to create a new conversation and forwards the user to a ConversationActivity if
     * successful.
     */
    private void createConversation() {
        new NewConversationTask().execute();
    }

    private void handleConverationCreationAttempt(ConversationCreationAttempt attempt) {
        if (attempt.getWasSuccessful()) {
            Intent conversationActivityIntent = new Intent(this, ConversationActivity.class);
            conversationActivityIntent.putExtra(ConversationActivity.CONVERSATION_THIS_USER, mLoggedInUser);
            conversationActivityIntent.putExtra(ConversationActivity.CONVERSATION_ID, attempt.getConversationId());
            startActivity(conversationActivityIntent);
            finish();
        } else {
            switch (attempt.getFailureReason()) {
                case NETWORK_ERROR:
                    // TODO: Show a dialog
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    return;
                case CONVERSATION_ALREADY_EXISTS:
                    // TODO: Show a dialog, include button to go to that conversation that launches a ConversationActivity (the id is included in the response)
                    Toast.makeText(this, "Conversation already exists!", Toast.LENGTH_SHORT).show();
                    return;
                case USER_NOT_FOUND:
                    // TODO: Show a dialog for this
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NewConversationTask extends AsyncTask<Void, Void, ConversationCreationAttempt> {

        @Override
        protected ConversationCreationAttempt doInBackground(Void... voids) {
            String otherUsername = mOtherUsernameField.getText().toString();
            return NetworkHelper.createConversation(mLoggedInUser, otherUsername, getApplicationContext());
        }

        @Override
        protected void onPostExecute(ConversationCreationAttempt attempt) {
            handleConverationCreationAttempt(attempt);
        }
    }
}
