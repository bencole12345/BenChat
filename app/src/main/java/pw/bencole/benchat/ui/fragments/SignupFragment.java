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

    private void attemptSignup() {
        // TODO: Actually sign up
        mListener.onSignupCompletion(null);
    }

}
