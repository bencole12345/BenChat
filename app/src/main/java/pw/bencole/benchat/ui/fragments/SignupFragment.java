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

public class SignupFragment extends Fragment {

    private TextView mUsernameField;
    private TextView mPasswordField;
    private TextView mPasswordReentryField;
    private Button mSignupButton;

    private SignupFragmentListener mListener;

    public interface SignupFragmentListener {
        void onSignupCompletion(LoggedInUser user);
    }

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        mUsernameField = view.findViewById(R.id.usernameTextView);
        mPasswordField = view.findViewById(R.id.passwordTextView);
        mPasswordReentryField = view.findViewById(R.id.repeatPasswordTextView);
        mSignupButton = view.findViewById(R.id.signupButton);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignupFragmentListener) {
            mListener = (SignupFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignupFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Shows to the user that the passwords they have entered do not match.
     */
    private void showPasswordMismatch() {
        mPasswordReentryField.setError("Passwords don't match!");
    }

    /**
     * Attempts to connect to the server and sign up.
     *
     * If this succeeds, then the Activity will be notified and subsequently terminated. If the user
     * has entered invalid values or the connection fails then they will be notified.
     */
    private void attemptSignup() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        String passwordRetyped = mPasswordReentryField.getText().toString();
        if (!password.equals(passwordRetyped)) {
            showPasswordMismatch();
            return;
        }
        new SignupTask().execute(username, password);
    }

    /**
     * Handles the result of an attempted signup.
     *
     * If the signup was successful, the created LoggedInUser will be passed to the callback
     * method of the containing activity. Otherwise, an appropriate error message will be displayed
     * to the user.
     *
     * @param loginAttempt The LoginAttempt describing the result of the attempted signup
     */
    private void handleSignupResponse(LoginAttempt loginAttempt) {
        if (loginAttempt.getWasSuccessful()) {
            mListener.onSignupCompletion(loginAttempt.getUser());
        } else {
            switch (loginAttempt.getFailureReason()) {
                case USERNAME_TAKEN:
                    showUsernameAlreadyTaken();
                    break;
                case NETWORK_ERROR:
                    showNetworkError();
                    break;
                default:
                    showGenericError();
                    break;
            }
        }
    }

    private void showUsernameAlreadyTaken() {
        // TODO: Show a dialog
        Toast.makeText(getContext(), "That username is already taken", Toast.LENGTH_SHORT).show();
    }

    private void showNetworkError() {
        // TODO: Show a dialog
        Toast.makeText(getContext(), "Connection failure", Toast.LENGTH_SHORT).show();
    }

    private void showGenericError() {
        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
    }

    private class SignupTask extends AsyncTask<String, Void, LoginAttempt> {

        @Override
        protected LoginAttempt doInBackground(String... strings) {
            return NetworkHelper.signup(strings[0], strings[1], getContext());
        }

        @Override
        protected void onPostExecute(LoginAttempt loginAttempt) {
            super.onPostExecute(loginAttempt);
            handleSignupResponse(loginAttempt);
        }
    }

}
