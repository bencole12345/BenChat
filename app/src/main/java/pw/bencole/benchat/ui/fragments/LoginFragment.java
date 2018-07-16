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

import static pw.bencole.benchat.network.NetworkHelper.login;


public class LoginFragment extends Fragment {

    private TextView mUsernameField;
    private TextView mPasswordField;
    private Button mLoginButton;

    private LoginFragmentListener mListener;

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

    private void attemptLogin() {
        // TODO: Actually log in

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        new LoginTask().execute(username, password);
    }

    private void handleLoginResponse(LoginAttempt loginAttempt) {
        // TODO: Populate this method with validation logic
        if (loginAttempt.getWasSuccessful()) {
            mListener.onLoginComplete(loginAttempt.getUser());
        } else {
            Toast.makeText(getContext(), "Failed to log in", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, LoginAttempt> {

        @Override
        protected LoginAttempt doInBackground(String... strings) {
            return login(strings[0], strings[1], getContext());
        }

        @Override
        protected void onPostExecute(LoginAttempt loginAttempt) {
            super.onPostExecute(loginAttempt);
            handleLoginResponse(loginAttempt);
        }

    }
}
