package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;
import pw.bencole.benchat.network.LoginAttempt;
import pw.bencole.benchat.network.NetworkHelper;


/**
 * A Fragment containing all widgets and logic required to log the user in, or handle displaying
 * error messages if the login fails.
 *
 * @author Ben Cole
 */
public class LoginFragment extends Fragment {

    private TextView mUsernameField;
    private TextView mPasswordField;
    private Button mLoginButton;

    private LoginFragmentListener mListener;

    /**
     * Ensures that the Activity containing this Fragment includes a necessary callback method
     * to be used once a successful login has been made.
     */
    public interface LoginFragmentListener {
        void onLoginComplete(LoggedInUser user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUsernameField = view.findViewById(R.id.usernameTextView);
        mPasswordField = view.findViewById(R.id.passwordTextView);
        mLoginButton = view.findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            mListener = (LoginFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement LoginFragmentListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Attempts to log the user in.
     *
     * This method loads the username and password that the user entered and begins an asynchronous
     * LoginTask to send them to the server. It is called when the login button is clicked.
     */
    private void attemptLogin() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        new LoginTask().execute(username, password);
    }

    /**
     * Handles the result of an attempted login.
     *
     * If the login was successful, the LoggedInUser is passed to the callback method of the
     * containing activity. If the login details are incorrect, or if the connection fails, the
     * user is informed via a relevant error message.
     *
     * @param loginAttempt A LoginAttempt object containing the result from an attempted login.
     */
    private void handleLoginResponse(LoginAttempt loginAttempt) {
        if (loginAttempt.getWasSuccessful()) {
            mListener.onLoginComplete(loginAttempt.getUser());
        } else {
            switch (loginAttempt.getFailureReason()) {
                case NETWORK_ERROR:
                    showNetworkError();
                    break;
                case INVALID_CREDENTIALS:
                    showInvalidCredentials();
                    break;
                default:
                    break;

            }
        }
    }

    /**
     * Displays an error message explaining that an error occurred while connecting to the backend
     * API.
     */
    private void showNetworkError() {
        // TODO: Show a dialog instead
        Toast.makeText(getContext(), "Connection failure", Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays an error message explaining that the user has entered invalid login details.
     */
    private void showInvalidCredentials() {
        // TODO: Show a dialog instead
        Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
    }

    private class LoginTask extends AsyncTask<String, Void, LoginAttempt> {

        @Override
        protected LoginAttempt doInBackground(String... strings) {
            return NetworkHelper.login(strings[0], strings[1], getContext());
        }

        @Override
        protected void onPostExecute(LoginAttempt loginAttempt) {
            super.onPostExecute(loginAttempt);
            handleLoginResponse(loginAttempt);
        }

    }
}
