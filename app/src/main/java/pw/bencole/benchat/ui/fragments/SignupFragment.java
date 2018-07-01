package pw.bencole.benchat.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.LoggedInUser;

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
        // TODO: Somehow show the user that the passwords don't match
        mPasswordReentryField.setError("Passwords don't match!");
    }

    /**
     * Attempts to connect to the server and sign up.
     *
     * If this succeeds, then the Activity will be notified and subsequently terminated. If the user
     * has entered invalid values or the connection fails then they will be notified.
     */
    private void attemptSignup() {
        // TODO: Actually sign up

        String username = (String) mUsernameField.getText();
        String password = (String) mPasswordField.getText();
        String passwordRetyped = (String) mPasswordReentryField.getText();

        if (!password.equals(passwordRetyped)) {
            showPasswordMismatch();
            return;
        }

        mListener.onSignupCompletion(null);
    }

}
