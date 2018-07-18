package pw.bencole.benchat.ui.activities;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pw.bencole.benchat.R;


/**
 * Displays a form for creating a new conversation.
 *
 * @author Ben Cole
 */
public class NewConversationActivity extends AppCompatActivity {

    private Button mCreateConversationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

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

    }

    private class NewConversationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
