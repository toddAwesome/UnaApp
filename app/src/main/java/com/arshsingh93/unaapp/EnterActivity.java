package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;


public class EnterActivity extends AppCompatActivity {
    private static final String TAG = EnterActivity.class.getSimpleName();

    private int currentType = 0; //0 is login, 1 is sign up, 2 is reset

    protected EditText myUsername;
    protected EditText myPassword;
    protected EditText myEmail;

    protected Button myButton;

    protected ProgressBar myProgressBar;

    protected TextView mySignUpMessage;
    protected TextView myLoginText;
    protected TextView mySignUpTextView;
    protected TextView myForgottenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);


        myUsername = (EditText) findViewById(R.id.usernameField);
        myPassword = (EditText) findViewById(R.id.passwordField);

        myProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        myProgressBar.setVisibility(View.INVISIBLE); //originally not seen

        myEmail = (EditText) findViewById(R.id.forgottenEmailField);
        myEmail.setVisibility(View.INVISIBLE);

        myLoginText = (TextView) findViewById(R.id.loginText);
        myLoginText.setVisibility(View.INVISIBLE);

        mySignUpMessage = (TextView) findViewById(R.id.signUpMess);
        mySignUpMessage.setVisibility(View.INVISIBLE);

        myButton = (Button) findViewById(R.id.mainButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType == 0) {
                    //log in
                    logIn(currentType);
                }
                else if (currentType == 1) {
                    //sign up
                    signUp(currentType);
                } else if (currentType == 2) {
                    //reset password
                    reset(currentType);
                }
            }
        });

        myForgottenView = (TextView) findViewById(R.id.forgotLabel);

        //create an account
        mySignUpTextView = (TextView) findViewById(R.id.signupText);
        mySignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 1;
                changeType(currentType);
            }
        });

        //forgot your account
        myForgottenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 2;
                changeType(currentType);
            }
        });

        //back to main page
        myLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 0;
                changeType(currentType);
            }
        });
    }

    /**
     * This method logs the user in.
     * @param type a value to determine if this option should be taken.
     */
    public void logIn(int type) {
        String username = myUsername.getText().toString();
        String password = myPassword.getText().toString();

        username = username.trim().toLowerCase();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            //login
            //toggleRefresh();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                //    toggleRefresh();
                    if (e == null) {
                        //success
                        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.login_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }

    }

    /**
     *This is the method for logging in.
     *
     *@param type a value to determine if this option should be taken.
     **/
    public void signUp(int type){
        String originalUsername = myUsername.getText().toString().trim();
        String password = myPassword.getText().toString();
        String email = myEmail.getText().toString();

        String lowerUsername = originalUsername.toLowerCase(); //this is the username is all lowercase letters.
        password = password.trim();
        email = email.trim().toLowerCase();
        if (originalUsername.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
            builder.setMessage(R.string.signup_error_message)
                    .setTitle(R.string.signup_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            toggleRefresh();
            ParseUser newUser = new ParseUser();
            newUser.setUsername(lowerUsername); //sign up with lowercase username for uniqueness
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.put("origName", originalUsername); //keep the original username intact for display


            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    toggleRefresh();
                    if (e == null) {
                        //success
                        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }
    /*
    more stuff
     */
    public void reset(int type) {
        String email = myEmail.getText().toString();
        email = email.trim().toLowerCase();

        if (email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
            builder.setMessage("Please enter an email address")
                    .setTitle("Oops")
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            toggleRefresh();

            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                public void done(ParseException e) {
                    toggleRefresh();
                    if (e == null) {
                        // An email was successfully sent with reset instructions.

                        AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                        builder.setMessage("A reset link will be sent to your email shortly.")
                                .setTitle("Reset Link")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //navigate to login screen
                                        currentType = 0;
                                        changeType(currentType);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
                        // Something went wrong. Look at the ParseException to see what's up.

                        AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle("Oops, something went wrong!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }


    public void changeType(int toType){
        if (toType == 0) {
            //user wants to go to log in
            //change page to look like login screen
            myButton.setText("LOGIN");
            myUsername.setVisibility(View.VISIBLE);
            myPassword.setVisibility(View.VISIBLE);
            mySignUpTextView.setVisibility(View.VISIBLE);
            myForgottenView.setVisibility(View.VISIBLE);
            myEmail.setVisibility(View.INVISIBLE);
            myLoginText.setVisibility(View.INVISIBLE);
            mySignUpMessage.setVisibility(View.INVISIBLE);
        } else if (toType == 1) {
            //user wants to go to sign up
            //change page to look like sign up screen
            myButton.setText("SIGN UP");
            myEmail.setVisibility(View.VISIBLE);
            mySignUpTextView.setVisibility(View.INVISIBLE);
            myForgottenView.setVisibility(View.INVISIBLE);
            myLoginText.setVisibility(View.VISIBLE);
         //   SignUp();
        } else if (toType == 2) {
            //user wants to reset password
            //change screen to look like reset screen
            myButton.setText("RESET MY PASSWORD");
            myEmail.setVisibility(View.VISIBLE);
            myUsername.setVisibility(View.INVISIBLE);
            myPassword.setVisibility(View.INVISIBLE);
            mySignUpTextView.setVisibility(View.INVISIBLE);
            myForgottenView.setVisibility(View.INVISIBLE);
            myLoginText.setVisibility(View.VISIBLE);
            mySignUpMessage.setVisibility(View.VISIBLE);
          //  Forgotten();
        }
    }
    private void toggleRefresh() {
        if(myProgressBar.getVisibility() == View.INVISIBLE) {
            myProgressBar.setVisibility(View.VISIBLE);
        } else {
            myProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
