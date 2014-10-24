package codingpark.net.cheesecloud.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import codingpark.net.cheesecloud.AppConfigs;
import codingpark.net.cheesecloud.R;
import codingpark.net.cheesecloud.eumn.LoginResultType;
import codingpark.net.cheesecloud.handle.ClientWS;
import codingpark.net.cheesecloud.model.User;
import codingpark.net.cheesecloud.model.UserDataSource;
import codingpark.net.cheesecloud.wsi.WsGuidOwner;

/**
 * A login screen that offers login via email/password
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private static final String TAG = LoginActivity.class.getSimpleName();


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView     = null;
    private EditText mPasswordView              = null;
    private EditText mWebUrlView                = null;
    private View mProgressView                  = null;
    private View mEmailLoginFormView            = null;
    private View mLoginFormView                 = null;


    private UserDataSource mDataSource          = null;
    private SharedPreferences mPrefs            = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mWebUrlView = (EditText)findViewById(R.id.web_url);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailLoginFormView = findViewById(R.id.email_login_form);

        // Initial data source
        mDataSource = new UserDataSource(this);
        mDataSource.open();
        mPrefs = getSharedPreferences(AppConfigs.PREFS_NAME, Context.MODE_PRIVATE);

        initInfo();
    }

    private void initInfo() {
        Log.d(TAG, "initInfo");
        String latestUser = mPrefs.getString(AppConfigs.USERNAME, "");
        if (!latestUser.isEmpty()) {
            Log.d(TAG, "latestUser: " + latestUser);
            User user = mDataSource.getUserByUsername(latestUser);
            if (user != null) {
                Log.d(TAG, "Database have the user");
                mEmailView.setText(user.getEmail());
                mPasswordView.setText(user.getPassword_md5());
                mWebUrlView.setText(user.getWs_address());
            }
        }
    }

    @Override
    protected void onResume() {
        mDataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDataSource.close();
        super.onPause();
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mWebUrlView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String web_url = mWebUrlView.getText().toString();

        boolean cancel = false;
        cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid server address.
        if (TextUtils.isEmpty(web_url)) {
            mWebUrlView.setError(getString(R.string.error_field_required));
            focusView = mWebUrlView;
            cancel = true;
        } else if (!isWebUrlValid(web_url)) {
            mWebUrlView.setError(getString(R.string.error_invalid_weburl));
            focusView = mWebUrlView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, web_url);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with email check logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with password check logic
        return password.length() > 4;
    }

    private boolean isWebUrlValid(String weburl) {
        //TODO: Replace this with password check logic
        return weburl.length() > 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;
        private final String mWebUrl;

        UserLoginTask(String email, String password, String weburl) {
            mEmail = email;
            mPassword = password;
            mWebUrl = weburl;
            // Store the username/password/weburl to SharedPreferences
            mPrefs.edit().putString(AppConfigs.USERNAME, mEmail).apply();
            mPrefs.edit().putString(AppConfigs.PASSWORD_MD5, mPassword).apply();
            mPrefs.edit().putString(AppConfigs.SERVER_ADDRESS, mWebUrl).apply();
            // Refresh ClientWS
            ClientWS.getInstance(LoginActivity.this).setEndPoint(mWebUrl);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            int result = -1;
            try {
                // 1. Call web service UserLogin
                WsGuidOwner owner = new WsGuidOwner();
                //owner.CreateDate = "2014-10-17 16:44:23";
                MessageDigest md = null;
                md = MessageDigest.getInstance("MD5");
                Log.d(TAG, "mEmail: " + mEmail + "##########" + "mPassword:" + mPassword + "#############" + mWebUrl);
                result = ClientWS.getInstance(LoginActivity.this).userLogin(mEmail, mPassword, owner);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            mAuthTask = null;
            showProgress(false);

            switch (result) {
                case LoginResultType.Success:
                    // 1. Login success: Store the mEmail and mPassword to database
                    Log.d(TAG, "Login Success!");
                    User u = new User();
                    u.setEmail(mEmail);
                    u.setPassword_md5(mPassword);
                    u.setWs_address(mWebUrl);
                    // 2. Save current user id to AppConfigs
                    AppConfigs.current_local_user_id = mDataSource.addUser(u);
                    // 3. Close LoginActivity and start MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                    finish();
                    return;
                case -1:
                    Toast.makeText(LoginActivity.this, "服务器无法访问", Toast.LENGTH_SHORT);
                    mWebUrlView.setError(getString(R.string.error_invalid_weburl));
                    mWebUrlView.requestFocus();
                    return;
                case LoginResultType.UserIsNotFind:
                    Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_SHORT);
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                    return;
                case LoginResultType.PasswordIsWrong:
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    return;
                case LoginResultType.SsoIsError:
                    Toast.makeText(LoginActivity.this, "服务器报错", Toast.LENGTH_SHORT);
                    mWebUrlView.setError(getString(R.string.error_invalid_weburl));
                    mWebUrlView.requestFocus();
                    return;

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



